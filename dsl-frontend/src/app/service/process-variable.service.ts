import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {ProcessVariable} from "../model/process-variable";

@Injectable({
  providedIn: 'root'
})
export class ProcessVariableService {

  rootUrl = environment.compilerBackendUrl + '/api/process-variable';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  findByProcessId(processId: string) {
    let url = this.rootUrl + '/find-by-process-id/' + processId;
    return this.httpClient.get<ProcessVariable[]>(url);
  }

  getOne(id: number) {
    let url = this.rootUrl + '/get-one/' + id;
    return this.httpClient.get<ProcessVariable>(url);
  }

  save(processVariable: ProcessVariable) {
    let url = this.rootUrl + '/save';
    return this.httpClient.post<ProcessVariable>(url, processVariable);
  }

  delete(id: number) {
    let url = this.rootUrl + '/delete/' + id;
    return this.httpClient.delete<void>(url);
  }
}
