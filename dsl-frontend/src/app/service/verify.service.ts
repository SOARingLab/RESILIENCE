import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { environment } from "../../environments/environment";
import { FunctionalVerificationResult } from "../model/functional-verification-result";
import { NonFunctionalVerificationResult } from "../model/non-functional-verification-result";
import { ControllabilityModel } from '../model/controllability-model';

@Injectable({
  providedIn: 'root'
})
export class VerifyService {

  rootUrl = environment.compilerBackendUrl + '/api/verifier';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  // verifyFunctional(file: string, start: string) {
  //   let url = this.rootUrl + '/verify-functional';
  //   let map = {'file': file, 'start': start};
  //   return this.httpClient.post<object>(url, map);
  // }

  verifyFunctional(processId: string, file: string, start: string) {
    let url = this.rootUrl + '/verify-functional';
    let map = { 'processId': processId, 'file': file, 'start': start };
    return this.httpClient.post<FunctionalVerificationResult>(url, map);
  }

  verifyNonFunctional(file: string, SLIs: string, logic: string) {
    let url = this.rootUrl + '/verify-non-functional';
    let map = { 'file': file, 'SLIs': SLIs, 'logic': logic };
    return this.httpClient.post<NonFunctionalVerificationResult>(url, map);
  }

  buildControllability(controllabilityModel: ControllabilityModel) {
    let url = this.rootUrl + '/build-controllability';
    return this.httpClient.post<ControllabilityModel>(url, controllabilityModel);
  }

  verifyControllability(controllabilityModel: ControllabilityModel) {
    let url = this.rootUrl + '/verify-controllability';
    return this.httpClient.post<ControllabilityModel>(url, controllabilityModel);
  }
}
