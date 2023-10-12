import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit {

  processId = '';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  ngOnInit(): void {
    this.getProcessId();
  }

  getProcessId() {
    let processId = localStorage.getItem('processId');
    if (processId) {
      this.processId = processId;
    } else {
      localStorage.setItem('processId', 'online_grocery');
      this.processId = 'online_grocery';
    }
  }

  setProcessId() {
    localStorage.setItem('processId', this.processId);
  }

  startExperiment() {
    this.httpClient.get('/assets/lng-logistics-experiment.bpmn', {responseType: 'text'}).subscribe(file => {
      localStorage.setItem('processId', 'lng_logistics');
      localStorage.setItem('filename', 'lng-logistics-experiment.bpmn');
      localStorage.setItem('file', file);
      window.location.href = '/assets/public-2/index.html';
    });
  }
}
