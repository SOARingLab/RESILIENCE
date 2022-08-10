import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit {

  processId = '';

  constructor() {
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
}
