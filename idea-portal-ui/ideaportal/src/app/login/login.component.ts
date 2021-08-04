import { Component, OnInit } from '@angular/core';
import {FormGroup,FormControl,Validators} from '@angular/forms'
import { DataService } from '../data.service';
import { Router } from  '@angular/router';
import { HttpClient } from '@angular/common/http';
import { RestService } from '../Services/rest.service';
import { MatDialogRef } from '@angular/material/dialog';
import { NotifierService } from 'angular-notifier';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  resultData;
  loginForm: FormGroup;
  hide=true;
  private notifier: NotifierService;
  constructor(private dataService: DataService, 
    private router: Router, private rs:RestService,
    private dialogRef: MatDialogRef<LoginComponent>,notifier: NotifierService ) {
    this.notifier = notifier; }
  message;


  ngOnInit(): void {
    //login form field validation
    this.loginForm=new FormGroup({
      userName:new FormControl('',[Validators.required]),
      password:new FormControl('',[Validators.required])
    });

    this.message="";
  }

  signIn(){
    const user=this.loginForm.value.userName;
    const pass=this.loginForm.value.password;
    this.rs.login(user,pass).subscribe(data => {
      this.resultData = data as JSON;
      if(this.resultData.result == 'success'){
      localStorage.setItem('access_token',this.resultData.token);
      localStorage.setItem('user',this.loginForm.value.userName);
      localStorage.setItem('userID',this.loginForm.value.userID);
      this.dialogRef.close();

      }
      console.log(this.resultData.result);
      if(this.resultData.result=="failed"){
        console.log(this.resultData.msg);
        this.message=this.resultData.msg;
      }
      //window.alert(this.resultData.token);
    })
  }

  login(){
    // stop here if form is invalid
    if (this.loginForm.invalid) {
      return;
  }
  let data= JSON.stringify(
   {"userPassword" :this.loginForm.value.password,
   "userName": this.loginForm.value.userName}
  );
   localStorage.clear();
    this.dataService.login(data).subscribe((data: any)=>{
      console.log(data);
        //setTimeout(() => { alert(data.statusText); }, 100);
        this.showNotification( 'success', data.statusText );
        localStorage.setItem('token', data.token);
        localStorage.setItem('userID', data.result.userID);
        localStorage.setItem('UserName', data.result.userName);
        localStorage.setItem('role', data.result.role.roleName);
        localStorage.setItem('mail', data.result.userEmail);
        localStorage.setItem('company', data.result.userCompany);
        this.dataService.setLoggedIn(true);
        this.dataService.setUser(data.result.userName);
        this.dialogRef.close();

      let currentUrl = this.router.url;
      this.router.routeReuseStrategy.shouldReuseRoute = () => false;
      this.router.onSameUrlNavigation = 'reload';
      this.router.navigate([currentUrl]);
      },
    error => {
      console.log(error);
      this.dataService.setLoggedIn(false);
      //alert(error.error.message);
      this.showNotification( 'error', error.error.message );
      this.dialogRef.disableClose;
    })
  }

  public showNotification( type: string, message: string ): void {
		this.notifier.notify( type, message );
	}

  keyDownFunction(event) {
    if (event.keyCode === 13) {
     this.login();
    }
  }
}

