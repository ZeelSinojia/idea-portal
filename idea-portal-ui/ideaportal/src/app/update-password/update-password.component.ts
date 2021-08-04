import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../data.service';
import { NotifierService } from 'angular-notifier';

@Component({
  selector: 'app-update-password',
  templateUrl: './update-password.component.html',
  styleUrls: ['./update-password.component.scss']
})
export class UpdatePasswordComponent implements OnInit {
  updatePasswordForm: FormGroup;
  hide = true;
  hide1 = true;
  id:any;
  constructor(private _service:DataService, private _route:ActivatedRoute, private _router:Router, private notifier: NotifierService ) {}

  ngOnInit(): void {
    this.id = this._route.snapshot.params.id;
    console.log(this.id);
    this.updatePasswordForm=new FormGroup({
      userPassword:new FormControl('',[Validators.required]),
      confirmPassword:new FormControl('',[Validators.required])
    });
  }

  setPassword(){
    let userPasswordDetails = JSON.stringify(
      {
        "userID":this.id,
        "userPassword": this.updatePasswordForm.get('userPassword').value
    })
    this._service.setPassword(userPasswordDetails).subscribe((data:any)=>{
      this.showNotification( 'success', data.statusText );
      this._router.navigateByUrl('/dashboard')
    },error => {
      console.log(error);
           this.showNotification( 'error', error.error.message );
    })
  }

  public showNotification( type: string, message: string ): void {
		this.notifier.notify( type, message );
	}
}
