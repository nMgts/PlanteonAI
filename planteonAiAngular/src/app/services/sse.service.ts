import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventSourcePolyfill } from 'ng-event-source';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SseService {
  private baseUrl = `${environment.apiUrl}/assistant`;

  getMessageStream(chatId: string, userMessage: string): Observable<string> {
    return new Observable<string>((observer) => {
      const token = localStorage.getItem('accessToken') || '';
      const eventSource = new EventSourcePolyfill(
        `${this.baseUrl}/chat/${chatId}?userMessage=${encodeURIComponent(userMessage)}`,
        {
          headers: {

          },
        }
      );

      //Authorization: `Bearer ${token}`,

      eventSource.onmessage = (event) => {
        if (event.data === '__END__') {
          observer.complete();   // zakończ strumień
          eventSource.close();   // zamknij EventSource
          return;
        }
        observer.next(event.data);
      };

      eventSource.onerror = (error: Event) => {
        observer.error(error);
        eventSource.close();
      };

      return () => {
        eventSource.close();
      };
    });
  }
}
