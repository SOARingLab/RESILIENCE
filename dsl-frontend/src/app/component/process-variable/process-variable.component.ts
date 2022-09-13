import {Component, OnInit} from '@angular/core';
import {ProcessVariable} from "../../model/process-variable";
import {ActivatedRoute} from "@angular/router";
import {ProcessVariableService} from "../../service/process-variable.service";

@Component({
  selector: 'app-process-variable',
  templateUrl: './process-variable.component.html',
  styleUrls: ['./process-variable.component.css']
})
export class ProcessVariableComponent implements OnInit {

  types = ['Boolean', 'Number', 'String'];
  id = 0;
  processVariable = new ProcessVariable();
  message: string | undefined;

  constructor(
    private route: ActivatedRoute,
    private processVariableService: ProcessVariableService,
  ) {
  }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.getOne();
  }

  getOne() {
    this.processVariableService.getOne(this.id).subscribe(processVariable => {
      this.processVariable = processVariable;
    });
  }

  save() {
    this.processVariableService.save(this.processVariable).subscribe(processVariable => {
      this.processVariable = processVariable;
      this.message = 'Saved';
    });
  }

  addValue() {
    this.processVariable.valueRange.push('');
  }

  deleteValue(index: number) {
    this.processVariable.valueRange.splice(index, 1);
  }

  range(num: number) {
    let arr = [];
    for (let i = 0; i < num; i++) {
      arr.push(i);
    }
    return arr;
  }
}
