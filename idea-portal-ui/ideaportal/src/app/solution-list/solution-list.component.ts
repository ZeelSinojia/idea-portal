import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router'
import { FormGroup, FormControl, Validators} from '@angular/forms';
import { DataService } from '../data.service';
import { Router } from '@angular/router';


 export interface Idea {
  ideaName: string;
  ideaID: number;
  ideaDescription: string;
  ideaDate: string;
}


/**
 * @title Data table with sorting, pagination, and filtering.
 */
@Component({
  selector: 'app-solution-list',
  templateUrl: './solution-list.component.html',
  styleUrls: ['./solution-list.component.scss'],
})
export class SolutionListComponent implements AfterViewInit {
  displayedColumns: string[] = ['ideaName', 'ideaDescription', 'ideaDate',  'ideaID'];
  ideas: Idea[];
  dataSource; 

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  

  constructor(private dataService: DataService, private route: ActivatedRoute) {

  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
 id: any;
 
  ngOnInit(): void{
    this.id = this.route.snapshot.paramMap.get('id');
    this.dataService.getIdeas(this.id).subscribe((data: any)=>{
      console.log(data);
      if(data.status==200){
        this.ideas = data.result;
        this.dataSource = this.ideas;
        console.log(data.result);
       
      }
  })
}
}
