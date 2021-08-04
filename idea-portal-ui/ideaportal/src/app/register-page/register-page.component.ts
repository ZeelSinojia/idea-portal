import { Component, OnInit } from '@angular/core';
import {FormGroup,FormControl,Validators} from '@angular/forms'
import { DataService } from '../data.service';
import { RestService } from '../Services/rest.service';
import { NotifierService } from 'angular-notifier';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss']
})
export class RegisterPageComponent implements OnInit {

  private notifier: NotifierService;
  regiForm: FormGroup;
  //hide for toggle icon to hide password and hide1 for toggle icon to hide confirm password
  hide=true;
  hide1=true;
  selected: string;
  resultData;
  //roles
  roles = [
    { value: '1', text: 'Client Partner' },
    { value: '2', text: 'Product Manager' },
    { value: '3', text: 'Participant' }
  ];

  
  constructor(private rs:RestService, private dataService: DataService, notifier: NotifierService ) {
		this.notifier = notifier; 
  }

  signUp(){
    const user=this.regiForm.value.userName;
    const pass=this.regiForm.value.password;
    const email=this.regiForm.value.userEmail;
    const role=this.regiForm.value.role;
    const company=this.regiForm.value.company;
    window.alert(role)
    this.rs.register(user,pass,email,role,company).subscribe(data => {
      this.resultData = data as JSON;
      console.log(this.resultData);
      window.alert(this.resultData.result);
    })
  }

  ngOnInit() {
    //Register form field validattion
    this.regiForm=new FormGroup({
      userName:new FormControl('',[Validators.required]),
      userEmail:new FormControl('',[Validators.required,Validators.email]),
      password:new FormControl('',[Validators.required]),
      confirmPassword:new FormControl('',[Validators.required]),
      role:new FormControl('',[Validators.required]),
      company:new FormControl('',[Validators.required])
    });

    }

    public showNotification( type: string, message: string ): void {
      this.notifier.notify( type, message );
    }

  register(){
      
  const roleName=  this.regiForm.value.role==1?'Client Partner' : this.regiForm.value.role==2?'Project Manager':'Participant';

    const object={
      "userPassword" :this.regiForm.value.password,
     "userName": this.regiForm.value.userName,
     "userEmail":this.regiForm.value.userEmail,
     "userCompany":this.regiForm.value.company,
     "role":{
         "roleID" : this.regiForm.value.role,
         "roleName":roleName
     }
 } 

 this.dataService.signup(JSON.stringify(object)).subscribe((data: any)=>{
  console.log(data);  
  this.showNotification( 'success', data.statusText );
  
  },
error => {
  console.log(error);
  this.showNotification( 'error', error.error.message );
}) 


  }

}
