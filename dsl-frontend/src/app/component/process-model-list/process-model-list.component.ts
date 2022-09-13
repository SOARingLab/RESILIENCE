import {Component, OnInit} from '@angular/core';
import {ProcessModel} from "../../model/process-model";
import {Router} from "@angular/router";
import {ProcessModelService} from 'src/app/service/process-model.service';

@Component({
  selector: 'app-process-model-list',
  templateUrl: './process-model-list.component.html',
  styleUrls: ['./process-model-list.component.css']
})
export class ProcessModelListComponent implements OnInit {

  processModelList: ProcessModel[] = [];
  message: string | undefined;

  constructor(
    private router: Router,
    private processModelService: ProcessModelService,
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

    this.processModelService.findByProcessId(processId).subscribe(processModelList => {
      this.processModelList = processModelList;
    })
  }

  add() {
    let processId = localStorage.getItem('processId');
    if (!processId) {
      return;
    }

    let processModel = new ProcessModel();
    processModel.processId = processId;
    this.processModelService.save(processModel).subscribe(processModel => {
      this.router.navigate(['process-model', processModel.id]).then();
    })
  }

  delete(id: number) {
    this.processModelService.delete(id).subscribe(() => {
      this.findByProcessId();
      this.message = 'Deleted';
    });
  }

  open(id: number) {
    this.processModelService.getOne(id).subscribe(processModel => {
      localStorage.setItem('filename', processModel.filename);
      localStorage.setItem('file', processModel.data);
      window.location.href = '/assets/public-2/index.html';
    });
  }
}
