// import { LoginComponent } from './login/login.component';
// import { RegisterPageComponent } from './register-page/register-page.component'
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {ReactiveFormsModule} from '@angular/forms';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {MatSelectModule} from '@angular/material/select';
// import {ConfirmEqualValidatorDirective} from './shared/confirm-equal-validator.directive';
import { MatDialogModule} from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { NotFoundComponent } from './not-found/not-found.component';
import { IdeaDescriptionPageComponent } from './idea-description-page/idea-description-page.component';
import { CommentBoxComponent } from './comment-box/comment-box.component';
import { LikeDislikeButtonComponent } from './like-dislike-button/like-dislike-button.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';

import {ConfirmEqualValidatorDirective} from './shared/confirm-equal-validator.directive';
import { MatTabsModule } from '@angular/material/tabs'; 
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';  
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material/material.module';
import { NavbarComponent } from './navbar/navbar.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { UpdateProfileComponent } from './update-profile/update-profile.component';
import { ThemeboardComponent, CreateSolutionDialog } from './themeBoard/themeboard.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SolutionListComponent } from './solution-list/solution-list.component';
import { AttachedFilePreviewComponent } from './attached-file-preview/attached-file-preview.component';
import { LoginComponent } from './login/login.component';
import { RegisterPageComponent } from './register-page/register-page.component';
import { CardComponent } from './card/card.component';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';

import {NgxPaginationModule} from 'ngx-pagination';
//import { JwtModule } from '@auth0/angular-jwt';
import { CreateThemeComponent } from './create-theme/create-theme.component';
import { UpdatePasswordComponent } from './update-password/update-password.component';
import { NotifierModule, NotifierOptions } from 'angular-notifier';


/**
 * Custom angular notifier options
 */
 const customNotifierOptions: NotifierOptions = {
  position: {
		horizontal: {
			position: 'middle',
			distance: 12
		},
		vertical: {
			position: 'top',
			distance: 12,
			gap: 10
		}
	},
  theme: 'material',
  behaviour: {
    autoHide: 5000,
    onClick: 'hide',
    onMouseover: 'pauseAutoHide',
    showDismissButton: true,
    stacking: 4
  },
  animations: {
    enabled: true,
    show: {
      preset: 'slide',
      speed: 300,
      easing: 'ease'
    },
    hide: {
      preset: 'fade',
      speed: 300,
      easing: 'ease',
      offset: 50
    },
    shift: {
      speed: 300,
      easing: 'ease'
    },
    overlap: 150
  }
};

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    NotFoundComponent,
    LoginComponent,
    RegisterPageComponent,
    ForgotPasswordComponent,
    ConfirmEqualValidatorDirective,
    NavbarComponent,
    ForgotPasswordComponent,
    UpdateProfileComponent,
    NavbarComponent,
    IdeaDescriptionPageComponent,
    CommentBoxComponent,
    LikeDislikeButtonComponent,
    ThemeboardComponent,
    SolutionListComponent,
    CreateSolutionDialog,
    AttachedFilePreviewComponent,
    CardComponent,
    CreateThemeComponent,
    UpdatePasswordComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    ReactiveFormsModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDialogModule,
    MatTableModule,
    MatPaginatorModule,
    MaterialModule,
    NgbModule,
    NgxPaginationModule,
   // JwtModule  
   NotifierModule.withConfig(customNotifierOptions)
  ],

  entryComponents: [
    CreateSolutionDialog
  ],
  providers: [{provide: LocationStrategy, useClass: HashLocationStrategy}],
  bootstrap: [AppComponent]
})

export class AppModule { }
