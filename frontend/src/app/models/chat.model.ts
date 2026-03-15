export interface ChatMessage {
  userId: string;
  userName: string;
  type: string;
  text: string;
  mediaUrls: string[];
  timestamp: string;
}

export interface ChatUser {
  userId: string;
  userName: string;
}

export interface ChatEvent {
  type: 'message' | 'user-joined' | 'user-left' | 'typing';
  payload: any;
}
