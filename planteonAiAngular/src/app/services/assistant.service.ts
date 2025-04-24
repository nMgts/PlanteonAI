import { Injectable } from '@angular/core';
import {environment} from "../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AssistantService {
  private baseUrl = `${environment.apiUrl}/assistant`;

  constructor(private http: HttpClient) {}

  public chat(chatId: string, message: string): Observable<any> {
    const headers = this.createHeaders();
    return this.http.post(`${this.baseUrl}/chat/${chatId}`, { message } , { headers });
  }

  private createHeaders() {
    const token = localStorage.getItem('accessToken') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
