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
  chatList: { chat: Chat, saved: boolean }[] = [];
  selectedChatId: string | null = null;

  isLeftSidebarOpen = true;  // Stan dla lewego menu bocznego
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
    console.log(this.chatList);
  }

  ngAfterViewInit() {
    this.resizeTextarea();
  }

  loadChats() {
    this.chatService.getChats().subscribe({
      next: chats => {
        this.chatList = chats.reverse().map(chat => ({
          chat: chat,
          saved: true
        }));
        this.createNewChat();
      },
      error: err => console.error('Error during loading chats', err)
    });
  }

  createNewChat() {
    const newChat: Chat = {
      id: '-1',
      title: 'Nowy chat'
    };

    const chatEntry = {
      chat: newChat,
      saved: false
    };

    this.chatList.unshift(chatEntry);
    this.selectedChatId = newChat.id;
    this.messages = [];
    console.log(this.chatList);
  }

  async saveNewChat() {
    const selectedChatEntry = this.chatList.find(
      entry => entry.chat.id === this.selectedChatId
    );

    if (!selectedChatEntry) {
      console.error('Chat not found during saving');
      return;
    }

    try {
      const chatFromServer = await this.chatService.createChat('Zapisany chat').toPromise();
      if (chatFromServer) {
        selectedChatEntry.chat = chatFromServer;
        selectedChatEntry.saved = true;
        this.selectedChatId = chatFromServer.id;
      }
      console.log('Chat created');
    } catch (err) {
      console.error('Error creating chat', err);
    }
  }


  deleteChat(chatId: string) {
    this.chatService.deleteChat(chatId).subscribe({
      next: () => {
        this.chatList = this.chatList.filter(entry => entry.chat.id !== chatId);

        if (this.selectedChatId === chatId) {
          this.selectedChatId = null;
          this.messages = [];
        }

        console.log('Chat deleted');
      },
      error: err => console.error('Error deleting chat', err)
    });
  }

  isSelectedChatUnsaved(): boolean {
    return !!this.chatList.find(item => item.chat.id === this.selectedChatId && !item.saved);
  }

  openChat(chat: Chat) {
    const unsavedChatIndex = this.chatList.findIndex(item => item.chat.id === this.selectedChatId && !item.saved);

    // Jeśli taki chat istnieje, usuwamy go z listy
    if (unsavedChatIndex !== -1) {
      this.chatList.splice(unsavedChatIndex, 1); // Usuwamy niezapisany chat z listy
    }

    this.selectedChatId = chat.id;
    this.getMessages();
  }

  toggleAvatarMenu() {
    this.isAvatarMenuOpen = !this.isAvatarMenuOpen;
  }

  onModelChange() {
    console.log('Model changed to:', this.selectedModel);
  }

  getMessages() {
    if (this.selectedChatId) {
      this.chatMessageService.getMessages(this.selectedChatId).subscribe({
        next: (response) => {
          this.messages = response;
        },
        error: (err) => {
          console.error('Error during downloading messages');
        },
      });
    }
  }

  async sendMessage() {
    if (this.isSelectedChatUnsaved()) {
      // Czekaj na zakończenie zapisu czatu przed wysłaniem wiadomości
      await this.saveNewChat();
    }

    if (this.newMessage.trim() && this.selectedChatId) {
      const messageData: ChatMessage = {
        text: this.newMessage,
        type: 'INPUT',
      };
      this.messages.push(messageData);

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
