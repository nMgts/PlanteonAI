import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Chat } from '../../entities/chat';
import { ChatService } from '../../services/chat.service';
import { ChatMessage } from '../../entities/chatMessage';
import { ChatMessageService } from '../../services/chat-message.service';
import { SseService } from '../../services/sse.service';
import { DomSanitizer, SafeHtml} from '@angular/platform-browser';

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
  isLoading = false;

  isLeftSidebarOpen = true;  // Stan dla lewego menu bocznego
  isRightSidebarOpen = false;  // Stan dla prawego menu bocznego
  isAvatarMenuOpen = false;

  selectedModel = 'Model 1';
  models = ['Model 1', 'Model 2', 'Model 3'];

  newMessage = '';
  streamingMessage: string = '';

  constructor(
    private authService: AuthService,
    private chatService: ChatService,
    private chatMessageService: ChatMessageService,
    private sseService: SseService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.loadChats();
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
    } catch (err) {
      console.error('Error creating chat', err);
    }
  }


  deleteChat(chatId: string) {
    this.chatService.deleteChat(chatId).subscribe({
      next: () => {
        this.chatList = this.chatList.filter(entry => entry.chat.id !== chatId);

        if (this.selectedChatId === chatId || this.chatList.length === 0) {
          this.createNewChat();
        }
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

    this.isLoading = true;
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
          this.isLoading = false;
        },
        error: (err) => {
          this.isLoading = false;
          console.error('Error during downloading messages');
        },
      });
    }
  }

  async sendMessage() {
    if (this.isSelectedChatUnsaved()) {
      await this.saveNewChat();
    }

    if (this.newMessage.trim() && this.selectedChatId) {
      let message = this.newMessage;
      this.newMessage = '';

      this.chatMessageService.sendMessage(this.selectedChatId, message).subscribe({
        next: (response) => {
          this.messages.push(response);

          const outputMessage: ChatMessage = {
            text: '',
            type: 'OUTPUT'
          }

          this.messages.push(outputMessage)

          this.sseService.getMessageStream(this.selectedChatId!).subscribe({
            next: (chunk: string) => {
              this.streamingMessage += chunk;
              outputMessage.text += chunk;
            },
            complete: () => {
              const botMessage: ChatMessage = {
                text: this.streamingMessage,
                type: 'OUTPUT'
              };
              this.messages.push(botMessage);
              this.streamingMessage = '';
            },
            error: (err) => {
              const errorMessage: ChatMessage = {
                text: `Wystąpił błąd podczas odbierania wiadomości. ${err}`,
                type: 'OUTPUT',
              };
              this.messages.push(errorMessage);
              console.error('Error receiving message: ', err);
            },
          });

        },
        error: (err) => {
          console.error('Error during sending message', err)
        },
      });
    }
    console.log(this.streamingMessage);
  }

  private fixSpacing(chunk: string): string {
    const lastChar = this.streamingMessage.slice(-1);

    const noSpaceBefore = ['.', ',', '!', '?', ';', ':'];
    const chunkStartsWithPunctuation = noSpaceBefore.some(p => chunk.startsWith(p));

    const needsSpace = lastChar && !lastChar.match(/\s/) && !chunkStartsWithPunctuation;

    return needsSpace ? ' ' + chunk : chunk;
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

  parseMarkdownToHtml(text: string): SafeHtml {
    const html = text
      .replace(/```([^`]+)```/g, '<pre><code>$1</code></pre>') // blok kodu
      .replace(/'''([^']+)'''/g, '<code>$1</code>')             // inline kod
      .replace(/\*\*([^*]+)\*\*/g, '<b>$1</b>')                 // pogrubienie
      .replace(/_([^_]+)_/g, '<i>$1</i>');                      // kursywa

    return this.sanitizer.bypassSecurityTrustHtml(html);
  }
}
