import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ProcessLog} from "../model/process-log";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ProcessLogService {

  rootUrl = environment.engineBackendUrl + '/api/process-log';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  findByBusinessKey(businessKey: string) {
    const url = this.rootUrl + '/findByBusinessKey?businessKey=' + businessKey;
    return this.httpClient.get<ProcessLog[]>(url);
  }
}
