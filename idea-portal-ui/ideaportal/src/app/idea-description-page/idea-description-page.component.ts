import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router'
import { FormGroup, FormControl, Validators} from '@angular/forms';
import { DataService } from '../data.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-idea-description',
  templateUrl: './idea-description-page.component.html',
  styleUrls: ['./idea-description-page.component.scss']
})


export class IdeaDescriptionPageComponent implements OnInit {
  
  IdeaTitle = "";
  IdeaDescription = "";
  TechnologyStack = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus ultricies sapien a enim molestie, eget pretium nisi maximus. Mauris fringilla ligula augue, quis consectetur mi iaculis eget. Aenean vitae quam dapibus, molestie purus tempus, pretium libero. Cras et arcu placerat ipsum tincidunt aliquet et eget eros";
  ideaDocxURL ="";
  artificats: any;
  constructor(private dataService: DataService, private route: ActivatedRoute, private router: Router) { }
  id: any;
  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.getIdea(this.id);
    localStorage.setItem("ideaID",this.id);
  }

  getIdea(id){
    this.dataService.getIdea(id).subscribe((data: any)=>{
      console.log(data);
      this.IdeaTitle = data.result.ideaName;
      this.IdeaDescription = data.result.ideaDescription;
      this.artificats = data.result.artifacts;
      localStorage.setItem("ideaName",data.result.ideaName);
    });
  }
  getIdeaTitle() {
    return this.IdeaTitle;
  }

  getIdeaDescription() {
    return this.IdeaDescription;
  }

  getTechnologyStack() {
    return this.TechnologyStack;
  }
}
