import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {FunctionalVerificationResult} from "../model/functional-verification-result";

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
    let map = {'processId': processId, 'file': file, 'start': start};
    return this.httpClient.post<FunctionalVerificationResult>(url, map);
  }

  verifyNonFunctional(file: string) {
    let url = this.rootUrl + '/verify-non-functional';
    return this.httpClient.post<string>(url, file);
  }
}
