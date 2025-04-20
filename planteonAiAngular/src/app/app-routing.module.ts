import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { StartComponent } from './components/start/start.component';
import { userGuard } from './guard/guard';

const routes: Routes = [
  { path: 'start', component: StartComponent },
  { path: 'login', component: LoginComponent },
  { path: '', component: HomeComponent, canActivate: [userGuard] },
  { path: '**', redirectTo: 'start', pathMatch: 'full'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
