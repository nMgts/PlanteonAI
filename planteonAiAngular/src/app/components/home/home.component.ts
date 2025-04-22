import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Chat } from '../../entities/chat';
import { ChatService } from '../../services/chat.service';
import { ChatMessage } from '../../entities/chatMessage';
import { ChatMessageService } from '../../services/chat-message.service';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit, AfterViewInit {
  @ViewChild('textareaRef') textareaRef!: ElementRef<HTMLTextAreaElement>;

  messages: ChatMessage[] = [];
  chatList: Chat[] = [];
  selectedChatId: string | null = null;

  isLeftSidebarOpen = false;  // Stan dla lewego menu bocznego
  isRightSidebarOpen = false;  // Stan dla prawego menu bocznego
  isAvatarMenuOpen = false;

  selectedModel = 'Model 1';
  models = ['Model 1', 'Model 2', 'Model 3'];

  user = { firstName: 'John', lastName: 'Doe' };
  newMessage = '';
  isFirstMessage = true;

  constructor(
    private authService: AuthService,
    private chatService: ChatService,
    private chatMessageService: ChatMessageService
  ) {}

  ngOnInit() {
    this.loadChats();
    this.createNewChat();
  }

  ngAfterViewInit() {
    this.resizeTextarea();
  }

  loadChats() {
    this.chatService.getChats().subscribe({
      next: chats => this.chatList = chats,
      error: err => console.error('Error loading chats, error')
    });
  }

  createNewChat() {
    this.chatService.createChat('Nowy chat').subscribe({
      next: chat => {
        this.chatList.unshift(chat);
        this.selectedChatId = chat.id;
      },
      error: err => console.error('error creating chat', err)
    });
  }

  deleteChat(chatId: string) {
    this.chatService.deleteChat(chatId).subscribe({
      next: () => {
        this.chatList = this.chatList.filter(chat => chat.id !== chatId);
        if (this.selectedChatId === chatId) {
          this.selectedChatId = null;
          this.messages = [];
        }
      },
      error: err => console.error('Error deleting chat', err)
    });
  }

  openChat(chat: Chat) {
    this.selectedChatId = chat.id;
    console.log('Opening chat: ', chat.title);
    // todo
  }

  toggleAvatarMenu() {
    this.isAvatarMenuOpen = !this.isAvatarMenuOpen;
  }

  onModelChange() {
    console.log('Model changed to:', this.selectedModel);
  }

  openNewChat() {
    console.log('Opening new chat...');
  }

  sendMessage(): void {
    if (this.newMessage.trim() && this.selectedChatId) {
      this.chatMessageService.sendMessage(this.selectedChatId, this.newMessage).subscribe({
        next: (response) => {
          // Po wysłaniu wiadomości dodaj ją do lokalnej listy
          this.messages.push(response);
          this.newMessage = ''; // Czyścimy pole input
        },
        error: (err) => {
          console.error('Error sending message:', err);
        },
      });
    }
  }

  onInputChange(): void {
    this.resizeTextarea();
  }

  private resizeTextarea(): void {
    const textarea = this.textareaRef?.nativeElement;
    if (textarea) {
      textarea.style.height = 'auto'; // Resetuj, aby pozwolić zmierzyć aktualny scrollHeight
      const newHeight = Math.min(textarea.scrollHeight, 150);
      textarea.style.height = `${newHeight}px`;
    }
  }

  logout() {
    this.authService.logout().subscribe(
      () => {
        console.log('Logged out successfully')
      },
      () => {
        console.log('Logged out successfully.')
      }
    );
  }
}
