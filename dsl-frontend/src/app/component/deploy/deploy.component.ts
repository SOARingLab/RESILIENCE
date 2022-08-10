import {Component, OnInit} from '@angular/core';
import {DeployService} from "../../service/deploy.service";

@Component({
  selector: 'app-deploy',
  templateUrl: './deploy.component.html',
  styleUrls: ['./deploy.component.css']
})
export class DeployComponent implements OnInit {

  result = 'Waiting';

  constructor(
    private deployService: DeployService
  ) {
  }

  ngOnInit(): void {
    this.deploy();
  }

  deploy() {
    let processId = localStorage.getItem('processId');
    let filename = localStorage.getItem('filename');
    let file = localStorage.getItem('file');
    let withAnnotations = localStorage.getItem('withAnnotations');
    if (processId && filename && file && withAnnotations) {
      if (withAnnotations === 'yes') {
        this.deployService.deployWithAnnotations(processId, filename, file).subscribe(() => {
          this.result = 'Deployed with annotations';
        });
      } else {
        this.deployService.deploy(processId, filename, file).subscribe(() => {
          this.result = 'Deployed';
        });
      }
    }
  }
}
