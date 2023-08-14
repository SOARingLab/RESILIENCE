import { Component, OnInit } from '@angular/core';
import { ControllabilityModel } from 'src/app/model/controllability-model';
import { ControllabilityModelService } from 'src/app/service/controllability-model.service';
import { VerifyService } from 'src/app/service/verify.service';

@Component({
  selector: 'app-controllability-model',
  templateUrl: './controllability-model.component.html',
  styleUrls: ['./controllability-model.component.css']
})
export class ControllabilityModelComponent implements OnInit {

  controllabilityModel: ControllabilityModel = new ControllabilityModel();
  bpmnVariableDefinitionList: BpmnVariableDefinition[] = [];
  bpmnVariableConditionList: BpmnVariableCondition[] = [];
  bpmnVariableModificationList: BpmnVariableModification[] = [];
  bpmnKpiList: BpmnKpi[] = [];
  message: string | undefined;

  constructor(
    private controllabilityModelService: ControllabilityModelService,
    private verifyService: VerifyService
  ) { }

  ngOnInit(): void {
    this.findByProcessId();
  }

  findByProcessId() {
    let processId = localStorage.getItem('processId');
    if (!processId) {
      return;
    }

    this.controllabilityModelService.findByProcessId(processId).subscribe(controllabilityModelList => {
      this.controllabilityModel = controllabilityModelList[0];

      this.bpmnVariableDefinitionList = JSON.parse(this.controllabilityModel.bpmnVariableDefinitionList);
      this.bpmnVariableConditionList = JSON.parse(this.controllabilityModel.bpmnVariableConditionList);
      this.bpmnVariableModificationList = JSON.parse(this.controllabilityModel.bpmnVariableModificationList);
      this.bpmnKpiList = JSON.parse(this.controllabilityModel.bpmnKpiList);
    });
  }

  save() {
    this.controllabilityModel.bpmnVariableDefinitionList = JSON.stringify(this.bpmnVariableDefinitionList);
    this.controllabilityModel.bpmnVariableConditionList = JSON.stringify(this.bpmnVariableConditionList);
    this.controllabilityModel.bpmnVariableModificationList = JSON.stringify(this.bpmnVariableModificationList);
    this.controllabilityModel.bpmnKpiList = JSON.stringify(this.bpmnKpiList);

    this.controllabilityModelService.save(this.controllabilityModel).subscribe(controllabilityModel => {
      this.controllabilityModel = controllabilityModel;
      this.message = 'Saved';
    });
  }

  buildControllability() {
    this.verifyService.buildControllability(this.controllabilityModel).subscribe(controllabilityModel => {
      this.controllabilityModel = controllabilityModel;
    });
  }

  verifyControllability() {
    this.verifyService.verifyControllability(this.controllabilityModel).subscribe(controllabilityModel => {
      this.controllabilityModel = controllabilityModel;
    });
  }

  range(num: number) {
    let arr = [];
    for (let i = 0; i < num; i++) {
      arr.push(i);
    }
    return arr;
  }

  addBpmnVariableDefinition() {
    this.bpmnVariableDefinitionList.push(new BpmnVariableDefinition());
  }

  deleteBpmnVariableDefinition(index: number) {
    this.bpmnVariableDefinitionList.splice(index, 1);
  }

  addBpmnVariableCondition() {
    this.bpmnVariableConditionList.push(new BpmnVariableCondition());
  }

  deleteBpmnVariableCondition(index: number) {
    this.bpmnVariableConditionList.splice(index, 1);
  }

  addBpmnVariableModification() {
    this.bpmnVariableModificationList.push(new BpmnVariableModification());
  }

  deleteBpmnVariableModification(index: number) {
    this.bpmnVariableModificationList.splice(index, 1);
  }

  addBpmnKpi() {
    this.bpmnKpiList.push(new BpmnKpi());
  }

  deleteBpmnKpi(index: number) {
    this.bpmnKpiList.splice(index, 1);
  }
}

class BpmnVariableDefinition {
  name = '';
  type = '';
  defaultValue = '';
  minValue = '';
  maxValue = '';
}

class BpmnVariableCondition {
  flowId = '';
  condition = '';
}

class BpmnVariableModification {
  nodeId = '';
  variableName = '';
  type = '';
  value = '';
  minValue = '';
  maxValue = '';
}

class BpmnKpi {
  name = '';
  value = '';
}
