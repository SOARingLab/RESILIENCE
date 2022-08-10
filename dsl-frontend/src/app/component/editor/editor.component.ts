import {Component, OnInit} from '@angular/core';
import {Grammar} from "../../model/grammar";
import {ProcessVariableService} from "../../service/process-variable.service";

@Component({
  selector: 'app-editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.css']
})
export class EditorComponent implements OnInit {

  grammarConditions = [
    [['risk_level'], ['==', '!='], ['"low"', '"medium"', '"high"']],
    [['delivery_method'], ['==', '!='], ['"home_delivery"', '"contactless_locker"', '"at_store"']],
  ];
  grammarActions = [
    [['order_status'], ['='], ['"running"', '"finished"', '"canceled"']],
    [['delivery_method'], ['='], ['"home_delivery"', '"contactless_locker"', '"at_store"']],
    [['amount'], ['='], ['']],
  ];
  grammarConditionList = [''];
  grammarActionList = [''];
  grammarConditionMap = new Map();
  grammarActionMap = new Map();
  inputs = [
    [[['', '', '', '']], [['', '', '']]]
  ];
  text = '';
  editorReady = false;

  constructor(
    private processVariableService: ProcessVariableService
  ) {
  }

  ngOnInit(): void {
    // let grammarString = localStorage.getItem("grammar");
    // if (grammarString) {
    //   let grammar = JSON.parse(grammarString);
    //   this.grammarConditions = grammar.grammarConditions;
    //   this.grammarActions = grammar.grammarActions;
    // }
    // this.editorInit();
    this.processVariableToGrammar();
  }

  processVariableToGrammar() {
    let processId = localStorage.getItem('processId');
    if (!processId) {
      return;
    }

    this.grammarConditions = [[[''], [''], ['']]];
    this.grammarConditions.pop();

    this.grammarActions = [[[''], [''], ['']]];
    this.grammarActions.pop();

    this.processVariableService.findByProcessId(processId).subscribe(processVariableList => {
      for (let processVariable of processVariableList) {
        let variable: string[] = [processVariable.name];
        let operator: string[] = [];
        let value: string[] = [];
        if (processVariable.type === 'Number') {
          operator = ['==', '!=', '<', '<=', '>', '>='];
        } else {
          operator = ['==', '!='];
        }
        if (processVariable.type === 'Boolean') {
          value = ['true', 'false'];
        } else if (processVariable.type === 'String') {
          value = processVariable.valueRange;
        }
        this.grammarConditions.push([variable, operator, value]);
      }

      for (let processVariable of processVariableList) {
        let variable: string[] = [processVariable.name];
        let operator: string[] = ['='];
        let value: string[] = [];
        if (processVariable.type === 'Boolean') {
          value = ['true', 'false'];
        } else if (processVariable.type === 'String') {
          value = processVariable.valueRange;
        }
        this.grammarActions.push([variable, operator, value]);
      }

      this.editorInit();
    });
  }

  editorInit() {
    this.grammarConditionList.pop();
    for (let i = 0; i < this.grammarConditions.length; i++) {
      for (let k = 0; k < this.grammarConditions[i][0].length; k++) {
        this.grammarConditionList.push(this.grammarConditions[i][0][k]);
        this.grammarConditionMap.set(this.grammarConditions[i][0][k], i);
      }
    }
    this.grammarActionList.pop();
    for (let i = 0; i < this.grammarActions.length; i++) {
      for (let k = 0; k < this.grammarActions[i][0].length; k++) {
        this.grammarActionList.push(this.grammarActions[i][0][k]);
        this.grammarActionMap.set(this.grammarActions[i][0][k], i);
      }
    }
    // this.inputs[0][0][0] = ['AND', this.conditionGrammar[0][0][0], this.conditionGrammar[0][1][0], this.conditionGrammar[0][2][0]];
    // this.inputs[0][1][0] = [this.actionGrammar[0][0][0], this.actionGrammar[0][1][0], this.actionGrammar[0][2][0]];
    this.inputDelete(0);
    this.inputAdd(0);
    this.editorReady = true;
  }

  inputAdd(inputId: number) {
    this.inputs.splice(inputId, 0, [[['', '', '', '']], [['', '', '']]]);
    this.inputConditionDelete(inputId, 0);
    this.inputConditionAdd(inputId, 0);
    this.inputActionDelete(inputId, 0);
    this.inputActionAdd(inputId, 0);
  }

  inputDelete(inputId: number) {
    this.inputs.splice(inputId, 1);
  }

  inputConditionAdd(inputId: number, conditionId: number) {
    this.inputs[inputId][0].splice(conditionId, 0, ['AND', this.grammarConditions[0][0][0], this.grammarConditions[0][1][0], this.grammarConditions[0][2][0]]);
  }

  inputConditionDelete(inputId: number, conditionId: number) {
    this.inputs[inputId][0].splice(conditionId, 1);
  }

  inputConditionChange(inputCondition: Array<string>) {
    inputCondition[2] = this.grammarConditions[this.grammarConditionMap.get(inputCondition[1])][1][0];
    inputCondition[3] = this.grammarConditions[this.grammarConditionMap.get(inputCondition[1])][2][0];
  }

  inputActionAdd(inputId: number, actionId: number) {
    this.inputs[inputId][1].splice(actionId, 0, [this.grammarActions[0][0][0], this.grammarActions[0][1][0], this.grammarActions[0][2][0]]);
  }

  inputActionDelete(inputId: number, actionId: number) {
    this.inputs[inputId][1].splice(actionId, 1);
  }

  inputActionChange(inputAction: Array<string>) {
    inputAction[1] = this.grammarActions[this.grammarActionMap.get(inputAction[0])][1][0];
    inputAction[2] = this.grammarActions[this.grammarActionMap.get(inputAction[0])][2][0];
  }

  generateText() {
    this.text = '';
    for (let i = 0; i < this.inputs.length; i++) {
      for (let k = 0; k < this.inputs[i][0].length; k++) {
        if (k === 0) {
          this.text += 'WHEN';
        } else {
          this.text += this.inputs[i][0][k][0];
        }
        this.text += ' ' + this.inputs[i][0][k][1] + ' ' + this.inputs[i][0][k][2] + ' ' + this.inputs[i][0][k][3] + '\n';
      }
      for (let k = 0; k < this.inputs[i][1].length; k++) {
        this.text += 'SET ' + this.inputs[i][1][k][0] + ' ' + this.inputs[i][1][k][1] + ' ' + this.inputs[i][1][k][2] + '\n';
      }
    }
    localStorage.setItem('text', this.text);
    window.location.href = '/assets/public-2/index.html';
    // console.log(this.inputs);
  }
}
