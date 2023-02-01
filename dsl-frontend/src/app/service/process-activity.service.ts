import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {ProcessActivity} from "../model/process-activity";

@Injectable({
  providedIn: 'root'
})
export class ProcessActivityService {

  rootUrl = environment.compilerBackendUrl + '/api/process-activity';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  findByProcessId(processId: string) {
    let url = this.rootUrl + '/find-by-process-id/' + processId;
    return this.httpClient.get<ProcessActivity[]>(url);
  }

  getOne(id: number) {
    let url = this.rootUrl + '/get-one/' + id;
    return this.httpClient.get<ProcessActivity>(url);
  }

  save(processActivity: ProcessActivity) {
    let url = this.rootUrl + '/save';
    return this.httpClient.post<ProcessActivity>(url, processActivity);
  }

  delete(id: number) {
    let url = this.rootUrl + '/delete/' + id;
    return this.httpClient.delete<void>(url);
  }
}
