import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {DeployService} from "../../service/deploy.service";

@Component({
  selector: 'app-instance-migration-list',
  templateUrl: './instance-migration-list.component.html',
  styleUrls: ['./instance-migration-list.component.css']
})
export class InstanceMigrationListComponent implements OnInit {

  processDefinitionKey = '';
  processInstanceIds: string[] = [];
  message: string | undefined;

  constructor(
    private router: Router,
    private deployService: DeployService
  ) {
  }

  ngOnInit(): void {
    this.getProcessDefinitionKey();
  }

  getProcessDefinitionKey() {
    let processDefinitionKey = localStorage.getItem('processDefinitionKey');
    if (processDefinitionKey) {
      this.processDefinitionKey = processDefinitionKey;
    }
  }

  findOldProcessInstance() {
    localStorage.setItem('processDefinitionKey', this.processDefinitionKey);
    this.deployService.findOldProcessInstance(this.processDefinitionKey).subscribe(processInstanceIds => {
      this.processInstanceIds = processInstanceIds;
    });
  }

  migrateAll() {
    this.deployService.migrateAll(this.processDefinitionKey).subscribe(numMigrate => {
      this.message = numMigrate + ' instance(s) migrated';
    })
  }
}
