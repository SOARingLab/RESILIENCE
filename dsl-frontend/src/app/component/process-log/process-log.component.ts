import {Component, OnInit} from '@angular/core';
import {ProcessLog} from "../../model/process-log";
import {ProcessLogService} from "../../service/process-log.service";
import {PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'app-process-log',
  templateUrl: './process-log.component.html',
  styleUrls: ['./process-log.component.css']
})
export class ProcessLogComponent implements OnInit {

  businessKey = '';
  page = 0;
  size = 10;
  content: ProcessLog[] = [];
  totalElements = 0;
  displayedColumns: string[] = ['id', 'businessKey', 'message'];

  constructor(
    private processLogService: ProcessLogService
  ) {
  }

  ngOnInit(): void {
  }

  findByBusinessKey() {
    this.processLogService.findByBusinessKey(this.businessKey, this.page, this.size)
      .subscribe(processLogPage => {
        this.content = processLogPage.content;
        this.totalElements = processLogPage.totalElements;
      });
  }

  onPageChange(event: PageEvent) {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.findByBusinessKey();
  }
}
