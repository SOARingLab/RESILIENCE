import {Component, OnInit} from '@angular/core';
import {ProcessModel} from "../../model/process-model";
import {ActivatedRoute} from "@angular/router";
import {ProcessModelService} from "../../service/process-model.service";

@Component({
  selector: 'app-process-model',
  templateUrl: './process-model.component.html',
  styleUrls: ['./process-model.component.css']
})
export class ProcessModelComponent implements OnInit {

  id = 0;
  processModel = new ProcessModel();
  message: string | undefined;

  constructor(
    private route: ActivatedRoute,
    private processModelService: ProcessModelService,
  ) {
  }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.getOne();
  }

  getOne() {
    this.processModelService.getOne(this.id).subscribe(processModel => {
      this.processModel = processModel;
    });
  }

  save() {
    this.processModelService.save(this.processModel).subscribe(processModel => {
      this.processModel = processModel;
      this.message = 'Saved';
    });
  }
}
