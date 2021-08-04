import { NgModule } from '@angular/core';
import {MatSliderModule} from '@angular/material/slider';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';

import {MatCardModule} from '@angular/material/card';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSidenav, MatSidenavModule} from '@angular/material/sidenav';
import {MatFormFieldModule} from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatFileUploadModule } from 'angular-material-fileupload';
import {MatTableModule} from '@angular/material/table';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatNativeDateModule} from '@angular/material/core';
import {BrowserModule} from '@angular/platform-browser';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatSortModule} from '@angular/material/sort';


const material = [
  MatSliderModule,
  MatButtonModule,
  MatToolbarModule,
  MatIconModule,
  MatMenuModule,

  HttpClientModule,
  MatPaginatorModule,

  MatSortModule,

  ReactiveFormsModule,
  MatNativeDateModule,
  MatCardModule,
  
  MatGridListModule,
  MatDialogModule,
  MatSidenavModule,
  MatFormFieldModule,
  MatInputModule,
  BrowserModule,
  FormsModule,
  BrowserAnimationsModule,
  MatFileUploadModule,
  MatTableModule,
];

@NgModule({
  
  imports: [material],
  exports: [material]
})
export class MaterialModule { }
