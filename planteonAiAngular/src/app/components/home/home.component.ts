import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements AfterViewInit {
  @ViewChild('textareaRef') textareaRef!: ElementRef<HTMLTextAreaElement>;

  messages: { sender: string, text: string }[] = [];
  isLeftSidebarOpen = false;  // Stan dla lewego menu bocznego
  isRightSidebarOpen = false;  // Stan dla prawego menu bocznego
  isAvatarMenuOpen = false;
  chatList = [
    { name: 'Czat z Markiem' },
    { name: 'Wsparcie techniczne' },
    { name: 'Projekt: AI Chat' },
  ];
  selectedModel = 'Model 1';
  models = ['Model 1', 'Model 2', 'Model 3'];
  user = { firstName: 'John', lastName: 'Doe' };
  newMessage = '';
  isFirstMessage = true;

  constructor(
    private authService: AuthService
  ) {}

  ngAfterViewInit(): void {
    this.resizeTextarea();
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

  openChat(chat: any) {
    console.log('Opening chat:', chat);
  }

  sendMessage() {
    if (this.newMessage.trim()) {
      this.messages.push({ sender: 'user', text: this.newMessage });
      this.newMessage = '';

      // symulacja odpowiedzi AI
      setTimeout(() => {
        this.messages.push({ sender: 'bot', text: 'To przykładowa odpowiedź.' });
      }, 600);
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
