import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {

  isLeftSidebarOpen = false;  // Stan dla lewego menu bocznego
  isRightSidebarOpen = false;  // Stan dla prawego menu bocznego
  chats = [{ name: 'Chat 1' }, { name: 'Chat 2' }, { name: 'Chat 3' }];
  selectedModel = 'Model 1';
  models = ['Model 1', 'Model 2', 'Model 3'];
  user = { firstName: 'John', lastName: 'Doe' };
  messages = ['W czym mogę Ci pomóc?'];
  newMessage = '';
  isFirstMessage = true;

  ngOnInit() {}

  toggleSidebar(side: string) {
    if (side === 'left') {
      this.isLeftSidebarOpen = !this.isLeftSidebarOpen;
    } else {
      this.isRightSidebarOpen = !this.isRightSidebarOpen;
    }
  }

  onModelChange() {
    console.log('Model changed to:', this.selectedModel);
  }

  openNewChat() {
    console.log('Opening new chat...');
  }

  openChat(chat: any) {
    console.log('Opening chat:', chat);
  }

  sendMessage() {
    if (this.newMessage.trim()) {
      this.messages.push(this.newMessage);
      this.newMessage = '';
      this.isFirstMessage = false; // Po wysłaniu pierwszej wiadomości
    }
  }

  autoResize(event: Event): void {
    const textarea = event.target as HTMLTextAreaElement;
    textarea.style.height = 'auto'; // reset wysokości
    textarea.style.height = Math.min(textarea.scrollHeight, 150) + 'px'; // max 150px
  }
}
