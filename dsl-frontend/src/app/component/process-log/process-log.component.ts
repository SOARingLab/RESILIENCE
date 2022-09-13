import {Component, OnInit} from '@angular/core';
import {ProcessLog} from "../../model/process-log";
import {ProcessLogService} from "../../service/process-log.service";

@Component({
  selector: 'app-process-log',
  templateUrl: './process-log.component.html',
  styleUrls: ['./process-log.component.css']
})
export class ProcessLogComponent implements OnInit {

  businessKey = '';
  processLogs: ProcessLog[] = [];

  constructor(
    private processLogService: ProcessLogService
  ) {
  }

  ngOnInit(): void {
  }

  findByBusinessKey() {
    this.processLogService.findByBusinessKey(this.businessKey)
      .subscribe(processLogs => {
        this.processLogs = processLogs;
      });
  }
}
