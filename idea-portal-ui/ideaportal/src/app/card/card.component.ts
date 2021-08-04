import { SelectorMatcher } from '@angular/compiler';
import { Component, OnInit } from '@angular/core';
import { DataService } from '../data.service';
import { Router} from '@angular/router';
import { MatDialog, MatDialogConfig,MatDialogRef,MAT_DIALOG_DATA,MatDialogModule } from '@angular/material/dialog';
import { LoginComponent } from '../login/login.component';
import { CreateThemeComponent } from '../create-theme/create-theme.component';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {

  card:any;

  totalLength : any;
  page:number=1;
  Themename:any;
  loginStatus: boolean;
  flag: boolean = localStorage.getItem('role') == 'Client Partner'? true: false;
  constructor(private dataService: DataService, private router: Router, private matDialog: MatDialog) {
    
  //this.totalLength=this.card.length;
  // console.log(this.totalLength);

   }

   name = document.getElementById("search");
  ngOnInit(): void {
    this.getThemes();
    
    this.dataService.isLoggedIn().subscribe(data =>{
      if(data==false){
        localStorage.clear();
      }
      this.loginStatus = data;
    });

  }


  getThemes(){
    this.dataService.themes().subscribe((data: any)=>{
      console.log(data);
      this.card = data.result;
      
    })
  }

  getTheme(id){
    
    this.router.navigate(['/theme/'+id]);
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

  createThemeLoginCheck(){
    if(!this.loginStatus){
      this.signIn();
      }else{
      this.openDialog();
    }
  }
    openDialog() {      
      const dialogRef = this.matDialog.open(CreateThemeComponent
        , {
        width: '100%',
        height: 'auto',
        maxHeight: '100%',
        data: {name: this.name, description: '', techstack: ''}
      }
      );
  
      dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.name = result;
      });
    }
  
}
