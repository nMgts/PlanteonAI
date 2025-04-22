import { Injectable } from '@angular/core';
import {environment} from '../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {ChatMessage} from '../entities/chatMessage';

@Injectable({
  providedIn: 'root'
})
export class ChatMessageService {
  private baseUrl = `${environment.apiUrl}/message`;

  constructor(private http: HttpClient) {}

  sendMessage(chatId: string, text: string) {
    const headers = this.createHeaders();
    return this.http.post<ChatMessage>(`${this.baseUrl}/${chatId}/send`, text, { headers });
  }

  private createHeaders() {
    const token = localStorage.getItem('accessToken') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
