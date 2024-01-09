import { Component } from '@angular/core';
import { Scenario } from 'src/app/model/scenario';
import { ScenarioService } from 'src/app/service/scenario.service';

@Component({
  selector: 'app-scenario',
  templateUrl: './scenario.component.html',
  styleUrls: ['./scenario.component.css']
})
export class ScenarioComponent {

  scenario: Scenario = new Scenario();
  message: string | undefined;

  constructor(
    private scenarioService: ScenarioService
  ) {
  }

  ngOnInit() {
    this.findAll();
  }

  findAll() {
    this.scenarioService.findAll().subscribe(scenarios => {
      this.scenario = scenarios[0];
    });
  }

  save() {
    this.scenarioService.save(this.scenario).subscribe(scenario => {
      this.scenario = scenario;
      this.message = 'Saved';
    });
  }

  range(num: number) {
    let arr = [];
    for (let i = 0; i < num; i++) {
      arr.push(i);
    }
    return arr;
  }

  addProcessId() {
    this.scenario.processIds.push('');
  }

  deleteProcessId(index: number) {
    this.scenario.processIds.splice(index, 1);
  }
}
