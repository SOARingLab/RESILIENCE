import {Component, OnInit} from '@angular/core';
import {ProcessVariable} from "../../model/process-variable";
import {Router} from "@angular/router";
import {ProcessVariableService} from "../../service/process-variable.service";

@Component({
  selector: 'app-process-variable-list',
  templateUrl: './process-variable-list.component.html',
  styleUrls: ['./process-variable-list.component.css']
})
export class ProcessVariableListComponent implements OnInit {

  processVariableList: ProcessVariable[] = [];

  constructor(
    private router: Router,
    private processVariableService: ProcessVariableService,
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

    this.processVariableService.findByProcessId(processId).subscribe(processVariableList => {
      this.processVariableList = processVariableList;
    })
  }

  add() {
    let processId = localStorage.getItem('processId');
    if (!processId) {
      return;
    }

    let processVariable = new ProcessVariable();
    processVariable.processId = processId;
    this.processVariableService.save(processVariable).subscribe(processVariable => {
      this.router.navigate(['process-variable', processVariable.id]).then();
    })
  }

  delete(id: number) {
    this.processVariableService.delete(id).subscribe(() => {
      this.findByProcessId();
    });
  }
}
