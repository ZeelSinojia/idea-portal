import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { DataService } from '../data.service';
import { Router} from '@angular/router';
import { MatDialog, MatDialogConfig,MatDialogRef,MAT_DIALOG_DATA,MatDialogModule } from '@angular/material/dialog';
import { LoginComponent } from '../login/login.component';
import { NotifierService } from 'angular-notifier';

@Component({
  selector: 'app-like-dislike-button',
  templateUrl: './like-dislike-button.component.html',
  styleUrls: ['./like-dislike-button.component.scss'],
  animations: [
    trigger('rotatedState', [
      state( 'yes', style({ transform: 'rotate(0)' })),
      state( 'no', style({ transform: 'rotateY(180deg)' })),
      transition('yes => no', animate('0.2s ease-out')),
      transition('no => yes', animate('0.2s ease-in'))
    ])
  ]
})

export class LikeDislikeButtonComponent implements OnInit {

  constructor(private dataService: DataService, private router: Router, private matDialog: MatDialog, private notifier: NotifierService) { }

  ngOnInit(): void {
    this.getLikesCount();
    this.getDisLikesCount();
    this.getCountOfParticipate();
   }

  cntLike: number = 0;
  cntDislike: number = 0;
  cntParticipate: number = 0;
  count_participate: number = 0;
  // likeStatus: string = false;
  // dislikeStatus: string = false;
  // participateStatus: string = false;

  likeStatus = false;
  dislikeStatus = false;
  participateStatus = false;

  flipActivator = '0';

  likeflip = "no";
  dislikeflip = "no";
  participateflip: string = "no";
  // likechecked = true;
  // dislikechecked = true;
  likesArray;
  ddisLikesArray;

  checkLike() {
    if (this.likeStatus == true) {
      this.likeStatus = false;
      this.likeflip = 'no';
      this.cntLike--;
    }
  }

  checkDislike() {
    if (this.dislikeStatus == true) {
      this.dislikeStatus = false;
      this.dislikeflip = 'no';
      this.cntDislike--;
    }
  }
  saveLikes(){
    if(localStorage.getItem("userID") == null){
      this.signIn();
      }else{
        this.addLike();      

      const data = {
              "likeValue" : 1,
              "idea": {
                      "ideaID": localStorage.getItem("ideaID")
                      },
              "user":  {
                      "userID": localStorage.getItem("userID")
                        }		   
               }
          
                this.dataService.saveLikes(data).subscribe((data: any)=>{
                console.log(data);
                this.showNotification( 'success', data.statusText );
               },
               error => {
                     console.log(error);
                     this.showNotification( 'error', error.error.message );
                     this.ngOnInit();
              });
               
              
              //this.ngOnInit();
          }      
  }


  addLike() {    
      this.checkDislike()
      if (this.likeStatus == true) {
        this.likeStatus = false;
        this.cntLike--;
        this.likeflip = 'no';
      } else {
        this.likeStatus = true;
        this.cntLike++;
        this.dislikeStatus = false;
        this.likeflip = 'yes';
      }
  }


  saveDisLikes(){
    if(localStorage.getItem("userID") == null){
      this.signIn();
      return;
      }else{
        this.addDislike();      

      const data = {
              "likeValue" : 0,
              "idea": {
                      "ideaID": localStorage.getItem("ideaID")
                      },
              "user":  {
                      "userID": localStorage.getItem("userID")
                        }		   
               }
          
               this.dataService.saveDisLikes(data).subscribe((data: any)=>{
                console.log(data);
                this.showNotification( 'success', data.statusText );
               
               },
               error => {
                console.log(error);
                     this.showNotification( 'error', error.error.message );
                    //  let currentUrl = this.router.url;
                    //  this.router.routeReuseStrategy.shouldReuseRoute = () => false;
                    //  this.router.onSameUrlNavigation = 'reload';
                    //  this.router.navigate([currentUrl]);
                     this.ngOnInit();
              })
              
          }      
  }


  addDislike() {
    this.checkLike();
    if (this.dislikeStatus == true) {
      this.dislikeStatus = false;
      this.cntDislike--;
      this.dislikeflip = 'no';
    } else {
      this.dislikeStatus = true;
      this.cntDislike++;
      this.likeStatus = false;
      this.dislikeflip = 'yes';
    }
    

    // if (this.dislikeStatus == false) {
    //   this.cntDislike++;
    // }
    // this.dislikeStatus = true
    // if (this.likeStatus == true) {
    //   this.cntLike--;
    //   this.likeStatus = false;
    //   this.likechecked = false;
    // }
    // this.likechecked = !this.likechecked;
  }

  addParticipate() {
    // this.flipActivator != this.flipActivator;

    if (this.participateStatus == false) {
      this.cntParticipate++;
      this.participateStatus = true;
      this.participateflip = 'yes';
      this.saveParticipate();
    } else {
      this.cntParticipate--;
      this.participateStatus = false;
      this.participateflip = 'no';
    }
    // this.participate = !this.participate;
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

  getLikesCount(){
    this.dataService.getAllLikes(localStorage.getItem("ideaID")).subscribe((data: any)=>{
      console.log(data);
     this.cntLike = data.result.length;
     this.checkCurrentUserLike(data.result);  
     
     }) 

  }

  getDisLikesCount(){
    this.dataService.getAllDisLikes(localStorage.getItem("ideaID")).subscribe((data: any)=>{
      console.log(data);
     this.cntDislike = data.result.length;
     this.checkCurrentUserDisLike(data.result);  
     
     }) 

  }

  checkCurrentUserLike(data){
  console.log("check", data);
    data.forEach(user => {
      if(user.userID == localStorage.getItem("userID")){
        this.likeStatus = true;
        //alert(user.userID);
        //this.cntLike++;
        this.dislikeStatus = false;
        this.likeflip = 'yes';
      }
    });
  }

  checkCurrentUserDisLike(data){

    console.log("check DisLike", data);
    data.forEach(user => {
      if(user.userID == localStorage.getItem("userID")){
      this.dislikeStatus = true;
      this.likeStatus = false;
      this.dislikeflip = 'yes';
      }
    });

  }

  saveParticipate(){

    if(localStorage.getItem("userID") == null){
      this.signIn();
      this.participateStatus = false;
      this.participateflip = 'no';
      this.cntParticipate--;
    
       }else{

        const data = {
              
          "idea": {
                  "ideaID": localStorage.getItem("ideaID")
                  },
          "user":  {
                  "userID": localStorage.getItem("userID")
                    },
          "theme": {
                  "themeID": localStorage.getItem("themeId")
                }						
           }
         
           this.dataService.saveParticipant(data).subscribe((data: any)=>{
           console.log(data);
           this.showNotification( 'success', data.statusText );
           this.ngOnInit();
          },
          error => {
            console.log(error);
            this.showNotification( 'error', error.error.message );
            this.participateStatus = false;
            this.participateflip = 'no';
            this.cntParticipate--;
            this.ngOnInit();
            }) 

      }

  }

  //get count of participants

  getCountOfParticipate(){
    this.dataService.getParticipants(localStorage.getItem("ideaID")).subscribe((data: any)=>{
      console.log(data);
     this.cntParticipate = data.result[localStorage.getItem("ideaName")];
     
     this.checkCurrentUserDisLike(data.result[localStorage.getItem("ideaName")]);  
     this.count_participate = data.result[localStorage.getItem("ideaName")];
     
     }) 

  }

  checkCurrentUserParticipant(data){
    
      if(data!=0){
        this.participateStatus = true;
        this.participateflip = 'yes';
        this.cntParticipate = this.count_participate;
      } 
    } 

    public showNotification( type: string, message: string ): void {
      this.notifier.notify( type, message );
    }
}
