import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-start',
  standalone: false,
  templateUrl: './start.component.html',
  styleUrl: './start.component.scss'
})
export class StartComponent implements OnInit {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.authService.checkAuth().subscribe(isAuthenticated => {
      if (isAuthenticated) {
        this.router.navigate(['/']);
      } else {
        this.router.navigate(['/login']);
      }
    });
  }
}
