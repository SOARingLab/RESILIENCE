import {Component, OnInit} from '@angular/core';
import {VerifyService} from "../../service/verify.service";

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrls: ['./verify.component.css']
})
export class VerifyComponent implements OnInit {

  resultFunctional = 'waiting';
  resultFunctionalDetail: object[] = [];
  resultNonFunctional = 'waiting';
  resultNonFunctionalDetail: object = {};

  constructor(
    private verifyService: VerifyService
  ) {
  }

  ngOnInit(): void {
    this.verifyFunctional();
    this.verifyNonFunctional();
  }

  // verifyFunctional() {
  //   let file = localStorage.getItem('file');
  //   let start = localStorage.getItem('start');
  //   if (file && start) {
  //     this.verifyService.verifyFunctional(file, start).subscribe(resultFunctionalDetail => {
  //       if (JSON.stringify(resultFunctionalDetail) === '{}') {
  //         this.resultFunctional = 'true';
  //       } else {
  //         this.resultFunctional = 'false';
  //       }
  //       this.resultFunctionalDetail = resultFunctionalDetail;
  //     });
  //   }
  // }

  verifyFunctional() {
    let processId = localStorage.getItem('processId');
    let file = localStorage.getItem('file');
    let start = localStorage.getItem('start');
    if (processId && file && start) {
      this.verifyService.verifyFunctional(processId, file, start).subscribe(resultFunctional => {
        this.resultFunctional = resultFunctional.result + '';
        this.resultFunctionalDetail = resultFunctional.detail;
      });
    }
  }

  explainResultFunctional() {
    // for (let state of this.resultFunctionalDetail) {
    //   for (let entry of Object.entries(state)) {
    //     let variable = entry[0];
    //     let value = String(entry[1]);
    //     if (variable === 'state') {
    //       let bpmnNodeIds = value.split('__');
    //     }
    //   }
    // }
    localStorage.setItem('resultFunctionalDetail', JSON.stringify(this.resultFunctionalDetail));
    window.location.href = '/assets/public-2/index.html';
  }

  verifyNonFunctional() {
    let file = localStorage.getItem('file');
    let SLIs = localStorage.getItem('SLIs');
    if (file && SLIs) {
      this.verifyService.verifyNonFunctional(file, SLIs).subscribe(resultNonFunctional => {
        this.resultNonFunctional = resultNonFunctional.result + '';
        this.resultNonFunctionalDetail = resultNonFunctional.detail;
      });
    }
  }

  explainResultNonFunctional() {
    localStorage.setItem('resultNonFunctionalDetail', JSON.stringify(this.resultNonFunctionalDetail));
    window.location.href = '/assets/public-2/index.html';
  }
}
