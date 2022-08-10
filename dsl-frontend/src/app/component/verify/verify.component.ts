import {Component, OnInit} from '@angular/core';
import {VerifyService} from "../../service/verify.service";

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrls: ['./verify.component.css']
})
export class VerifyComponent implements OnInit {

  resultFunctional = 'waiting';
  resultFunctionalDetail = {};
  resultNonFunctional = 'waiting';

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
        this.resultFunctional = resultFunctional + '';
      });
    }
  }

  verifyNonFunctional() {
    let file = localStorage.getItem('file');
    if (file) {
      this.verifyService.verifyNonFunctional(file).subscribe(resultNonFunctional => {
        this.resultNonFunctional = resultNonFunctional + '';
      });
    }
  }
}
