import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  errorMessage: string = '';
  passwordFieldType = 'password';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  handleSubmit() {
    if (!this.email || !this.password) {
      this.showError('Email i hasło są wymagane');
      return;
    }

    this.authService.login({email: this.email, password: this.password}).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => this.showError('Niepoprawny email lub hasło')
    });
  }

  togglePasswordField(): void {
    this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
  }

  showError(mess: string) {
    this.errorMessage = mess;
    setTimeout(() => {
      this.errorMessage = ''
    }, 3000);
  }
}
