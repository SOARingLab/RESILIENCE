import {Component, OnInit} from '@angular/core';
import {ProcessActivity} from "../../model/process-activity";
import {ActivatedRoute} from "@angular/router";
import {ProcessActivityService} from "../../service/process-activity.service";

@Component({
  selector: 'app-process-activity',
  templateUrl: './process-activity.component.html',
  styleUrls: ['./process-activity.component.css']
})
export class ProcessActivityComponent implements OnInit {

  id = 0;
  processActivity = new ProcessActivity();
  message: string | undefined;

  constructor(
    private route: ActivatedRoute,
    private processActivityService: ProcessActivityService,
  ) {
  }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.getOne();
  }

  getOne() {
    this.processActivityService.getOne(this.id).subscribe(processActivity => {
      this.processActivity = processActivity;
    });
  }

  save() {
    this.processActivityService.save(this.processActivity).subscribe(processActivity => {
      this.processActivity = processActivity;
      this.message = 'Saved';
    });
  }
}
