import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { RestService } from '../Services/rest.service';
import { DataService } from '../data.service';
import { NotifierService } from 'angular-notifier';

@Component({
  selector: 'app-update-profile',
  templateUrl: './update-profile.component.html',
  styleUrls: ['./update-profile.component.scss']
})
export class UpdateProfileComponent implements OnInit {

  changeEmail:FormGroup;
  changeCompanyName:FormGroup;
  changePassword: FormGroup;
  updateProfile: FormGroup;
  hide=true;
  hide0=true;
  hide1=true;

  resultData;
  messagePass="";
  isClientPartner: boolean = localStorage.getItem("role") == 'Client Partner' ? true: false;
  constructor(private rs:RestService, private dataService: DataService, private notifier: NotifierService) { 
  }

  updateEmail(data){
      const email=data.userEmail;
      this.rs.updateEmail(email).subscribe(data => {
        this.resultData = data as JSON;
        window.alert(this.resultData.result);
      })
  }

  updateCompany(data){
    const company=data.companyName;
      this.rs.updateCompany(company).subscribe(data => {
        this.resultData = data as JSON;
        window.alert(this.resultData.result);
      })
  }
  
  updateProfileData(){
    let userID  = parseInt(localStorage.getItem("userID"));
    const data= {
                  "userID": userID ,
                  "userEmail": this.changeEmail.value.userEmail,
                  "userCompany":this.changeCompanyName.value.companyName
               }

               this.dataService.updateProfile(data).subscribe((data: any)=>{
                console.log(data);
                this.showNotification( 'success', data.statusText );
               },
               error => {
                 console.log(error);
                 this.showNotification( 'error', error.error.message );
                 }) 
    
  }

  updatePassword(dataString){
    let userID  = parseInt(localStorage.getItem("userID"));
    const oldPass=dataString.oldPassword
    const newPass=dataString.newPassword
    const confirmPass=dataString.confirmPassword
    // this.rs.updatePassword(oldPass,newPass,confirmPass).subscribe(data => {
    //   this.resultData = data as JSON;
    //   console.log(this.resultData.result);
    //   if(this.resultData.result=="failed"){
    //     console.log(this.resultData.msg);
    //     this.messagePass=this.resultData.msg;
    //   }
    //   window.alert(this.resultData.result);
    // })

    const data= {
      "userID": userID ,
      "userPassword": newPass      
   }

   this.dataService.chnagePassword(data).subscribe((data: any)=>{
    console.log(data);
    this.showNotification( 'success', data.statusText );
    this.changePassword.reset();
   },
   error => {
     console.log(error);
     this.showNotification( 'error', error.error.message );
     }) 

  }

  ngOnInit(){
    //change email
    this.changeEmail=new FormGroup({
      userEmail:new FormControl(localStorage.getItem('mail'),[Validators.required,Validators.email])
    });
    
    //change company name
    this.changeCompanyName=new FormGroup({
      companyName:new FormControl(localStorage.getItem('company'),[Validators.required])
    });


    //change password
    this.changePassword=new FormGroup({
      oldPassword:new FormControl('',[Validators.required]),
      newPassword:new FormControl('',[Validators.required]),
      confirmPassword:new FormControl('',[Validators.required])
    });
  }

  public showNotification( type: string, message: string ): void {
		this.notifier.notify( type, message );
	}

}
