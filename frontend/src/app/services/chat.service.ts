import { Injectable, OnDestroy } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { BehaviorSubject, Subject } from 'rxjs';
import { ChatMessage, ChatUser } from '../models/chat.model';

@Injectable({
  providedIn: 'root'
})
export class ChatStompService implements OnDestroy {
  private client: Client | null = null;

  messages$ = new BehaviorSubject<ChatMessage[]>([]);
  users$ = new BehaviorSubject<ChatUser[]>([]);
  typing$ = new Subject<ChatUser>();
  connected$ = new BehaviorSubject<boolean>(false);

  connect(): void {
    if (this.client?.active) return;

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${protocol}//${window.location.host}/ws/websocket`;

    this.client = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 5000,
      onConnect: () => {
        this.connected$.next(true);
        this.subscribeToTopics();
      },
      onDisconnect: () => {
        this.connected$.next(false);
      },
      onStompError: (frame) => {
        console.error('STOMP error', frame);
      }
    });

    this.client.activate();
  }

  disconnect(): void {
    if (this.client?.active) {
      this.client.deactivate();
    }
    this.connected$.next(false);
  }

  sendMessage(userId: string, text: string, mediaUrls: string[] = []): void {
    this.client?.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({ userId, text, mediaUrls })
    });
  }

  sendTyping(userId: string): void {
    this.client?.publish({
      destination: '/app/chat.typing',
      body: JSON.stringify({ userId })
    });
  }

  private subscribeToTopics(): void {
    this.client!.subscribe('/topic/chat.message', (msg: IMessage) => {
      const message: ChatMessage = JSON.parse(msg.body);
      const current = this.messages$.value;
      this.messages$.next([...current, message]);
    });

    this.client!.subscribe('/topic/chat.user-joined', (msg: IMessage) => {
      const user: ChatUser = JSON.parse(msg.body);
      const current = this.users$.value;
      if (!current.find(u => u.userId === user.userId)) {
        this.users$.next([...current, user]);
      }
    });

    this.client!.subscribe('/topic/chat.user-left', (msg: IMessage) => {
      const user: ChatUser = JSON.parse(msg.body);
      this.users$.next(this.users$.value.filter(u => u.userId !== user.userId));
    });

    this.client!.subscribe('/topic/chat.typing', (msg: IMessage) => {
      const user: ChatUser = JSON.parse(msg.body);
      this.typing$.next(user);
    });
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
