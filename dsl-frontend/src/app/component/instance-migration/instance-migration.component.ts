import {Component, OnInit} from '@angular/core';
import {GetProcessInstance} from "../../model/get-process-instance";
import {ActivatedRoute} from "@angular/router";
import {DeployService} from "../../service/deploy.service";

@Component({
  selector: 'app-instance-migration',
  templateUrl: './instance-migration.component.html',
  styleUrls: ['./instance-migration.component.css']
})
export class InstanceMigrationComponent implements OnInit {

  processDefinitionKey = '';
  id = '';
  getProcessInstance = new GetProcessInstance();
  message: string | undefined;

  constructor(
    private route: ActivatedRoute,
    private deployService: DeployService
  ) {
  }

  ngOnInit(): void {
    this.id = String(this.route.snapshot.paramMap.get('id'));
    this.getOne();
  }

  getOne() {
    this.deployService.getProcessInstance(this.id).subscribe(getProcessInstance => {
      this.getProcessInstance = getProcessInstance;
    })
  }

  migrateOne() {
    let processDefinitionKey = localStorage.getItem('processDefinitionKey');
    if (processDefinitionKey) {
      this.processDefinitionKey = processDefinitionKey;
    }
    this.deployService.migrateOne(this.processDefinitionKey, this.id).subscribe(numMigrate => {
      this.message = numMigrate + ' instance(s) migrated';
    });
  }
}
