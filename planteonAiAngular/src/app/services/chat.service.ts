import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Chat } from '../entities/Chat';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private baseUrl = `${environment.apiUrl}/chat`;
  private addUrl = `${this.baseUrl}/create`;
  private getAllUrl = `${this.baseUrl}/get-all`;
  private deleteUrl = `${this.baseUrl}/delete`;
  private renameUrl = `${this.baseUrl}/rename`;

  constructor(private http: HttpClient) {}

  getChats(): Observable<Chat[]> {
    const headers = this.createHeaders();
    return this.http.get<Chat[]>(this.getAllUrl, { headers });
  }

  createChat(title: string): Observable<Chat> {
    const headers = this.createHeaders();
    return this.http.post<Chat>(`${this.addUrl}?title=${encodeURIComponent(title)}`, {}, { headers })
  }

  deleteChat(id: string): Observable<void> {
    const headers = this.createHeaders();
    return this.http.delete<void>(`${this.deleteUrl}/${id}`, { headers });
  }

  renameChat(chat: Chat): Observable<Chat> {
    const headers = this.createHeaders();
    return this.http.put<Chat>(this.renameUrl, { chat }, { headers });
  }

  private createHeaders() {
    const token = localStorage.getItem('accessToken') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
