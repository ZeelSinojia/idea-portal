import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators} from '@angular/forms';
import { DataService } from '../data.service';
import { Router } from '@angular/router';
import { MatDialogRef } from '@angular/material/dialog';
import { NotifierService } from 'angular-notifier';


@Component({
  selector: 'app-create-theme',
  templateUrl: './create-theme.component.html',
  styleUrls: ['./create-theme.component.scss']
})
export class CreateThemeComponent implements OnInit {


  themeForm: FormGroup;
  themeDocx: any;
  themePpt: any;
  themeVideo: any;
  files: File [] = [];
  private notifier: NotifierService;
  constructor(private dataService: DataService, private router: Router,
     private dialogRef: MatDialogRef<CreateThemeComponent>, notifier: NotifierService ) {
      this.notifier = notifier; 
    }

  ngOnInit(): void {
    this.themeForm = new FormGroup({
      themeName: new FormControl('', [Validators.required]),
      themeDesc: new FormControl('', [Validators.required]),
      files: new FormControl('', [Validators.required])
      });
  }

  onFileChangeDocx(event)  {
    for  (var i =  0; i <  event.target.files.length; i++)  {  
        this.themeDocx=event.target.files[i];
    }
  }

  onFileChangePpt(event)  {
    for  (var i =  0; i <  event.target.files.length; i++)  {  
        this.themePpt=event.target.files[i];
    }
  }

  onFileChangeVideo(event)  {
    for  (var i =  0; i <  event.target.files.length; i++)  {  
        this.themeVideo=event.target.files[i];
    }
  }

  getFileDetails(event){

    for  (var i =  0; i <  event.target.files.length; i++)  {  
      this.files.push(event.target.files[i]);
  }
  }

  get f() { return this.themeForm.controls; }

  saveTheme(){
   

   if (this.themeForm.invalid) {
      return;
  }

    const formData = new FormData();

    formData.append("themeName",this.themeForm.value.themeName);
    formData.append("themeDescription",this.themeForm.value.themeDesc);
    formData.append("userID",localStorage.getItem("userID"));

    for (var i = 0; i < this.files.length; i++) { 
      formData.append("files", this.files[i]);
    }
    
    this.dataService.createTheme(formData).subscribe((data: any)=>{
      console.log(data);
      this.showNotification( 'success', data.statusText );
      this.dialogRef.close();     
      let currentUrl = "/dashboard";
      this.router.routeReuseStrategy.shouldReuseRoute = () => false;
      this.router.onSameUrlNavigation = 'reload';
      this.router.navigate([currentUrl]);
      
     },
      error => {
        console.log(error);
             this.showNotification( 'error', error.error.message );
      }) 

    }

    public showNotification( type: string, message: string ): void {
      this.notifier.notify( type, message );
    }


}
