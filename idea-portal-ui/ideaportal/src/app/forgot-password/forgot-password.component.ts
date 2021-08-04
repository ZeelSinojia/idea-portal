import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DataService } from '../data.service';
import { NotifierService } from 'angular-notifier';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  forgotPasswordForm: FormGroup;
  hide=true;

  constructor(private _service:DataService, private notifier: NotifierService) {}

  ngOnInit(): void {
    //login form field validation
    this.forgotPasswordForm=new FormGroup({
      userEmail:new FormControl('',[Validators.required,Validators.email])
    });
  }

  sendEmail(){
    this._service.resetPassword(this.forgotPasswordForm.get('userEmail').value).subscribe((data:any)=>{
      this.showNotification( 'success', data.statusText );
    },(err:HttpErrorResponse)=>{
      console.log(err.message);
      this.showNotification( 'error', err.message );
    })
  }

  
  public showNotification( type: string, message: string ): void {
		this.notifier.notify( type, message );
	}

}
