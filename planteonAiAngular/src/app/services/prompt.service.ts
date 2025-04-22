import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {Observable} from 'rxjs';
import {Prompt} from '../entities/prompt';

@Injectable({
  providedIn: 'root'
})
export class PromptService {
  private baseUrl = `${environment.apiUrl}/prompt`;
  private getAllUrl = `${this.baseUrl}/get-all`;
  private getUserUrl = `${this.baseUrl}/get-user`;
  private getSystemUrl = `${this.baseUrl}/get-system`;
  private addWithUserTypeUrl = `${this.baseUrl}/add-with-user-type`;
  private addWithSystemTypeUrl = `${this.baseUrl}/add-with-system-type`;
  private updateUrl = `${this.baseUrl}/update`;
  private deleteUrl = `${this.baseUrl}/delete`;

  constructor(private http: HttpClient) {}

  getAllPrompts(): Observable<Prompt[]> {
    const headers = this.createHeaders();
    return this.http.get<Prompt[]>(this.getAllUrl, { headers });
  }

  getUserPrompts(): Observable<Prompt[]> {
    const headers = this.createHeaders();
    return this.http.get<Prompt[]>(this.getUserUrl, { headers });
  }

  getSystemPrompts(): Observable<Prompt[]> {
    const headers = this.createHeaders();
    return this.http.get<Prompt[]>(this.getSystemUrl, { headers });
  }

  createUserPrompt(prompt: Prompt): Observable<Prompt> {
    const headers = this.createHeaders();
    return this.http.post<Prompt>(this.addWithUserTypeUrl, prompt, { headers });
  }

  createSystemPrompt(prompt: Prompt): Observable<Prompt> {
    const headers = this.createHeaders();
    return this.http.post<Prompt>(this.addWithSystemTypeUrl, prompt, { headers });
  }

  updatePrompt(prompt: Prompt): Observable<Prompt> {
    const headers = this.createHeaders();
    return this.http.put<Prompt>(this.updateUrl, prompt, { headers });
  }

  deletePrompt(promptId: number): Observable<void> {
    const headers = this.createHeaders();
    return this.http.delete<void>(`${this.deleteUrl}/${promptId}`, { headers });
  }

  private createHeaders() {
    const token = localStorage.getItem('accessToken') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
