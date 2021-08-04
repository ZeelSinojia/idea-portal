import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog,MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router'
import { FormGroup, FormControl, Validators} from '@angular/forms';
import { DataService } from '../data.service';
import { Router } from '@angular/router';
import { NotifierService } from 'angular-notifier';

export interface DialogData {
  name: string;
  description: string;
  techstack: string;
}

@Component({
  selector: 'app-themeboard',
  templateUrl: './themeboard.component.html',
  styleUrls: ['./themeboard.component.scss']
})

export class ThemeboardComponent implements OnInit {

  name: string;
  description: string;
  techstack: string;
  id: any;
  card: any;
  role:string;
  artificats:any;
  solnBtnFlag: boolean = localStorage.getItem('role')=='Product Manager'?true : false;
  constructor(public dialog: MatDialog, private route: ActivatedRoute, private dataService: DataService, private router: Router) { }

  openDialog() {
    const dialogRef = this.dialog.open(CreateSolutionDialog
      , {
      width: '100%',
      height: 'auto',
      maxHeight: '100%',
      data: {name: this.name, description: this.description, techstack: this.techstack}
    }
    );

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.name = result;
    });
  }

  
  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    localStorage.setItem("themeId",this.id);
    this.getTheme(this.id);
    this.role = localStorage.getItem("role");
    console.log("role: ", this.role);
  }

  getTheme(id){
    this.dataService.getTheme(id).subscribe((data: any)=>{
      console.log(data);
      this.card = data.result;
      this.artificats = data.result.artifacts;
    });
  }
  getSolutions(){
    this.getIdeas(this.id);
    }
  getIdeas(id){
  this.router.navigateByUrl("/solutions/"+id);
  }

}

@Component({
  selector: 'createsolutiondialog.html',
  templateUrl: 'createsolutiondialog.html',
})
export class CreateSolutionDialog{

  ideaForm: FormGroup;
  themeId :any;
  files: File [] = [];
  constructor(
    public dialogRef: MatDialogRef<CreateSolutionDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData, private dataService: DataService,
    private router: Router, private route: ActivatedRoute, private notifier: NotifierService ) {
    
    }

    ngOnInit():void{

      
      this.ideaForm = new FormGroup({
        themeName: new FormControl('', [Validators.required]),
        description: new FormControl('', [Validators.required]),
        themeDocx: new FormControl('', [Validators.required]),
        themePpt: new FormControl('', [Validators.required]),
        themeVideo: new FormControl('', [Validators.required])
      });
    }

  onNoClick(): void {
    this.dialogRef.close();
  }

  getFileDetails(event){

    for  (var i =  0; i <  event.target.files.length; i++)  {  
      this.files.push(event.target.files[i]);
  }
  }

  get f() { return this.ideaForm.controls; }

  saveIdea(){
   

    // if (this.ideaForm.invalid) {
    //   return;
    // }

    const formData = new FormData();

    formData.append("ideaName",this.ideaForm.value.themeName);
    formData.append("ideaDescription",this.ideaForm.value.description);
    //formData.append("ideaDocx", this.themeDocx);
    //formData.append("files", this.themePpt);
    for (var i = 0; i < this.files.length; i++) { 
      formData.append("files", this.files[i]);
    }
    formData.append("themeID",localStorage.getItem("themeId"));
    formData.append("userID",localStorage.getItem("userID"));
    //alert(localStorage.getItem("themeId"));
    this.dataService.createIdea(formData).subscribe((data: any)=>{
      console.log(data);
      this.showNotification( 'success', data.statusText );
      this.dialogRef.close();
    },
    error => {
      console.log(error);
           this.showNotification( 'error', error.error.message );
     });

    }

    public showNotification( type: string, message: string ): void {
      this.notifier.notify( type, message );
    }

  }
