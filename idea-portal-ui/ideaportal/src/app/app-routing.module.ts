import { NgModule } from '@angular/core';
import { RouterModule, Routes} from '@angular/router';
import { NotFoundComponent } from './not-found/not-found.component';
import { UpdateProfileComponent } from './update-profile/update-profile.component';
import { NavbarComponent } from './navbar/navbar.component';
import { SolutionListComponent } from './solution-list/solution-list.component';
import { ThemeboardComponent } from './themeBoard/themeboard.component';
import { IdeaDescriptionPageComponent } from './idea-description-page/idea-description-page.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { AppComponent } from './app.component';
import { CardComponent } from './card/card.component';
import {CreateThemeComponent} from'./create-theme/create-theme.component';
import { UpdatePasswordComponent } from './update-password/update-password.component';
import { LoginComponent } from './login/login.component';

const routes: Routes = [
  { path: 'theme', component: ThemeboardComponent },
  { path:'login' , component: LoginComponent},
  { path: 'home', component: NavbarComponent },
  { path: 'solutions/:id', component: SolutionListComponent },
  { path: 'theme/:id', component: ThemeboardComponent },
  { path: 'dashboard', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'idea/:id', component: IdeaDescriptionPageComponent },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '404', component: NotFoundComponent },
  {path:'updateProfile',component:UpdateProfileComponent},
  {path:'confirmpassword/:id',component:UpdatePasswordComponent},
  {path:'forgotPassword',component:ForgotPasswordComponent},
  {path:'card',component:CardComponent},
  {path:'createtheme',component:CreateThemeComponent},
  {path:'dashboard',component:CardComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
