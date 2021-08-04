import { Component, Input, OnInit } from '@angular/core';

import { LoginComponent } from '../login/login.component';
import { RegisterPageComponent } from '../register-page/register-page.component';

import { MatDialog, MatDialogConfig,MatDialogRef,MAT_DIALOG_DATA,MatDialogModule } from '@angular/material/dialog';
import { RestService } from '../Services/rest.service';
import { DataService } from '../data.service';
import { Router } from  '@angular/router';
import { NotifierService } from 'angular-notifier';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  loginStatus: boolean = false;
  resultData;
  @Input() inputSideNav: MatSidenav;
  userName: any;
  constructor(private matDialog: MatDialog,private rs:RestService, private dataSrvice: DataService ,
     private router: Router, private notifier: NotifierService) {
      //this.checkLoginStatus();
       this.dataSrvice.isLoggedIn().subscribe(data =>{
        this.loginStatus= data;
      });
      console.log("loginStatus:", this.loginStatus );

      this.dataSrvice.getUser().subscribe(data =>{
        this.userName = data
        console.log("user:",this.userName);
      })
  }

  public showNotification( type: string, message: string ): void {
		this.notifier.notify( type, message );
	}

  signIn() {
    let config = new MatDialogConfig();
    config.autoFocus = true;
    const regiDialogRef= this.matDialog.open(LoginComponent,config);
    regiDialogRef.afterClosed().subscribe(data => {
      //window.alert(`Dialog sent: ${data.userName},${data.password}`);
      //this.checkLoginStatus();
    });
  }

  signUp() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.autoFocus = true;
    const regiDialogRef= this.matDialog.open(RegisterPageComponent, dialogConfig);

    regiDialogRef.afterClosed().subscribe(data => {
      //window.alert(`Dialog sent: ${data.userEmail},${data.role}`);
      //this.checkLoginStatus();
    });
  }

  logout() {
    // this.rs.logout().subscribe(data => {
    //   localStorage.removeItem('access_token')
    //   localStorage.removeItem('user')
    //   this.resultData = data as JSON;
    //   console.log(this.resultData);
    //   //window.alert(this.resultData);
    //   this.loginStatus=false;
    // })

    localStorage.clear();
    localStorage.setItem('token', "");
    localStorage.setItem('userID', "");
    localStorage.setItem('UserName', "");
    localStorage.setItem('role',"");
    localStorage.setItem('mail', "");
    localStorage.setItem('company', "");
    this.router.navigate(["/dashboard"]);
    this.showNotification( 'error', 'You are logged out successfully' );
    this.dataSrvice.setLoggedIn(false);
  }

  
  checkLoginStatus(){
    if(localStorage.getItem('access_token')!==null)
    this.rs.loggedIn().subscribe(data => {
      this.resultData = data as JSON;
      console.log(this.resultData);
      if(this.resultData.user==localStorage.getItem('user')){
        //window.alert(this.resultData.result+" curr"+this.loginStatus);
          this.loginStatus=true;
      }
      else{
        this.logout()
      }
    })
  }

  ngOnInit(): void {
  }

  myProfile(){
    const dialogConfig = new MatDialogConfig();
    dialogConfig.autoFocus = true;
  }

}
