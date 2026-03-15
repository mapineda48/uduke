import { Component, OnInit, OnDestroy, ViewChild, ElementRef, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ApiService } from '../services/api.service';
import { ChatStompService } from '../services/chat.service';
import { ChatMessage, ChatUser } from '../models/chat.model';

@Component({
  selector: 'app-chat-widget',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './chat-widget.html',
  styleUrl: './chat-widget.scss'
})
export class ChatWidgetComponent implements OnInit, OnDestroy {
  @ViewChild('messagesContainer') messagesContainer!: ElementRef;

  open = signal(false);
  registered = signal(false);
  userName = '';
  userId = '';
  messageText = '';
  messages = signal<ChatMessage[]>([]);
  users = signal<ChatUser[]>([]);
  typingUser = signal<string | null>(null);

  private subs: Subscription[] = [];
  private typingTimeout: any;

  constructor(
    private api: ApiService,
    private chatStomp: ChatStompService
  ) {}

  ngOnInit(): void {
    this.subs.push(
      this.chatStomp.messages$.subscribe(msgs => {
        this.messages.set(msgs);
        setTimeout(() => this.scrollToBottom(), 50);
      }),
      this.chatStomp.users$.subscribe(users => this.users.set(users)),
      this.chatStomp.typing$.subscribe(user => {
        if (user.userId !== this.userId) {
          this.typingUser.set(user.userName);
          clearTimeout(this.typingTimeout);
          this.typingTimeout = setTimeout(() => this.typingUser.set(null), 2000);
        }
      })
    );
  }

  toggleChat(): void {
    this.open.update(v => !v);
  }

  register(): void {
    if (!this.userName.trim()) return;
    this.api.chatRegister(this.userName.trim()).subscribe(user => {
      this.userId = user.userId;
      this.registered.set(true);
      this.chatStomp.connect();

      this.api.getChatMessages().subscribe(msgs => {
        this.chatStomp.messages$.next(msgs);
      });
      this.api.getChatUsers().subscribe(users => {
        this.chatStomp.users$.next(users);
      });
    });
  }

  sendMessage(): void {
    if (!this.messageText.trim()) return;
    this.chatStomp.sendMessage(this.userId, this.messageText.trim());
    this.messageText = '';
  }

  onTyping(): void {
    this.chatStomp.sendTyping(this.userId);
  }

  uploadFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.api.uploadChatMedia(input.files[0]).subscribe(res => {
        this.chatStomp.sendMessage(this.userId, '', [res.url]);
      });
      input.value = '';
    }
  }

  logout(): void {
    this.api.chatUnregister(this.userId).subscribe(() => {
      this.chatStomp.disconnect();
      this.registered.set(false);
      this.userId = '';
      this.messages.set([]);
      this.chatStomp.messages$.next([]);
      this.chatStomp.users$.next([]);
    });
  }

  isImage(url: string): boolean {
    return /\.(jpg|jpeg|png|gif|webp|svg)$/i.test(url);
  }

  private scrollToBottom(): void {
    if (this.messagesContainer) {
      const el = this.messagesContainer.nativeElement;
      el.scrollTop = el.scrollHeight;
    }
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
    if (this.registered()) {
      this.api.chatUnregister(this.userId).subscribe();
    }
    this.chatStomp.disconnect();
  }
}
