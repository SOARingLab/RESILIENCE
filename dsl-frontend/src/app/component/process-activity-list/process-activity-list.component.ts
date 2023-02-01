import {Component, OnInit} from '@angular/core';
import {ProcessActivity} from "../../model/process-activity";
import {Router} from "@angular/router";
import {ProcessActivityService} from 'src/app/service/process-activity.service';

@Component({
  selector: 'app-process-activity-list',
  templateUrl: './process-activity-list.component.html',
  styleUrls: ['./process-activity-list.component.css']
})
export class ProcessActivityListComponent implements OnInit {

  processActivityList: ProcessActivity[] = [];
  message: string | undefined;

  constructor(
    private router: Router,
    private processActivityService: ProcessActivityService,
  ) {
  }

  ngOnInit(): void {
    this.findByProcessId();
  }

  findByProcessId() {
    let processId = localStorage.getItem('processId');
    if (!processId) {
      return;
    }

    this.processActivityService.findByProcessId(processId).subscribe(processActivityList => {
      this.processActivityList = processActivityList;
    })
  }

  add() {
    let processId = localStorage.getItem('processId');
    if (!processId) {
      return;
    }

    let processActivity = new ProcessActivity();
    processActivity.processId = processId;
    this.processActivityService.save(processActivity).subscribe(processActivity => {
      this.router.navigate(['process-activity', processActivity.id]).then();
    })
  }

  delete(id: number) {
    this.processActivityService.delete(id).subscribe(() => {
      this.findByProcessId();
      this.message = 'Deleted';
    });
  }
}
