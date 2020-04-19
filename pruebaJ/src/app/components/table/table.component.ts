import { Component, OnInit } from '@angular/core';
import { TableServiceService } from '../../services/table-service.service';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit {
  
  data:any[] = []
  data$: any

  constructor(private tableService: TableServiceService) { }


  ngOnInit() {
    console.log(this.data);
   
    this.getData()
  }

  getData(){
    try{
      this.tableService.getData()
      .subscribe(resp => {
        this.data$ = resp
        console.log(this.data$);
        
      },
      error => {
        console.log(error, "error")
      })
    }catch (e){
      console.log(e);
    }
  }
  

}
