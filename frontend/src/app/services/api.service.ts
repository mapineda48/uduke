import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Person, CreatePersonRequest } from '../models/person.model';
import { ChatMessage, ChatUser } from '../models/chat.model';

export interface HealthResponse {
  status: string;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getHealth(): Observable<HealthResponse> {
    return this.http.get<HealthResponse>(`${this.apiUrl}/health`);
  }

  // People CRUD
  getPeople(): Observable<Person[]> {
    return this.http.get<Person[]>(`${this.apiUrl}/people`);
  }

  getPerson(id: number): Observable<Person> {
    return this.http.get<Person>(`${this.apiUrl}/people/${id}`);
  }

  createPerson(person: CreatePersonRequest, photo?: File): Observable<Person> {
    const formData = new FormData();
    formData.append('person', new Blob([JSON.stringify(person)], { type: 'application/json' }));
    if (photo) {
      formData.append('photo', photo);
    }
    return this.http.post<Person>(`${this.apiUrl}/people`, formData);
  }

  updatePerson(id: number, person: CreatePersonRequest, photo?: File): Observable<Person> {
    const formData = new FormData();
    formData.append('person', new Blob([JSON.stringify(person)], { type: 'application/json' }));
    if (photo) {
      formData.append('photo', photo);
    }
    return this.http.put<Person>(`${this.apiUrl}/people/${id}`, formData);
  }

  deletePerson(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/people/${id}`);
  }

  // Chat REST
  chatRegister(userName: string): Observable<ChatUser> {
    return this.http.post<ChatUser>(`${this.apiUrl}/chat/register`, { userName });
  }

  chatUnregister(userId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/chat/unregister`, { userId });
  }

  getChatMessages(count = 50): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/chat/messages`, { params: { count } });
  }

  getChatUsers(): Observable<ChatUser[]> {
    return this.http.get<ChatUser[]>(`${this.apiUrl}/chat/users`);
  }

  uploadChatMedia(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string }>(`${this.apiUrl}/chat/upload`, formData);
  }
}
