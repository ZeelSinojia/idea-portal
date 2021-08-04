import { Component, OnInit } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { DataService } from '../data.service';
import { Router} from '@angular/router';
import { MatDialog, MatDialogConfig,MatDialogRef,MAT_DIALOG_DATA,MatDialogModule } from '@angular/material/dialog';
import { LoginComponent } from '../login/login.component';
import { NotifierService } from 'angular-notifier';
import { FormGroup, FormControl, Validators } from '@angular/forms';


@Component({
  selector: 'app-comment-box',
  templateUrl: './comment-box.component.html',
  styleUrls: ['./comment-box.component.scss'],
  animations: [
    trigger('bodyExpansion', [
      state('collapsed, void', style({ height: '0px', visibility: 'hidden' })),
      state('expanded', style({ height: '*', visibility: 'visible'})),
      transition('expanded <=> collapsed',
        animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ])
  ]
})
export class CommentBoxComponent implements OnInit {

  comments: string[] = [];
  buttonString : string = "+";
  state = 'collapsed';
  warning: string = "";
  commentResponse;

  setComment(val: string) {
    if(localStorage.getItem("userID") == null){
      this.signIn();
      }else{
        if(val==null || val== undefined|| val==''){
         this.showNotification( 'error', "please enter the comments" );
         return;
        }
    this.state = 'expanded';
    this.comments.push(localStorage.getItem("UserName")+" : "+val);

    const data = {
      "commentValue" : val,
      "idea": {
              "ideaID": localStorage.getItem("ideaID")
              },
      "user":  {
              "userID": localStorage.getItem("userID")
                }		   
       }
       this.dataService.saveComments(data).subscribe((data: any)=>{
       console.log(data);
       this.showNotification( 'success', data.statusText );
       this.ngOnInit();
       },
       error => {
        console.log(error);
             this.showNotification( 'error', error.error.message );
       }) ;
      }
  }

  getbuttonString() {
    return this.buttonString;
  }

  toggle(): void {
    this.buttonString = this.buttonString === "-" ? "+" : "-";
    this.state = this.state === 'collapsed' ? 'expanded' : 'collapsed';
  }

  getComments() {
    return this.comments;
  }

  getWarning() {
    if (this.comments.length == 0) {
      this.warning = "No comments found";
    } else {
      this.warning = "";
    }
    return this.warning;
  }

  constructor( private dataService: DataService, private router: Router, private matDialog: MatDialog ,private notifier: NotifierService) { }

  commentForm: FormGroup;
  ngOnInit(): void {
    this.getAllComments();
   
     //add comment value 
     this.commentForm=new FormGroup({
      commentText:new FormControl('',[Validators.required])
    });
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

  getAllComments(){
    this.dataService.getComments(localStorage.getItem("ideaID")).subscribe((data: any)=>{
      console.log(data);
      this.populateComments(data.result);          
     }) ;
  }

  populateComments(data){
    this.comments= [];
    if(data.length>0){
      this.state = 'expanded';
      this.buttonString = "-";
            
    data.forEach(element => {
      this.comments.push(element.user.userName+" : "+element.commentValue);
  });

    } 
    
  }

  public showNotification( type: string, message: string ): void {
		this.notifier.notify( type, message );
	}

}
