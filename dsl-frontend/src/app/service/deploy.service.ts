import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {GetProcessInstance} from "../model/get-process-instance";

@Injectable({
  providedIn: 'root'
})
export class DeployService {

  compilerUrl = environment.compilerBackendUrl + '/api/deployment';
  engineUrl = environment.engineBackendUrl + '/api/deployment';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  deploy(processId: string, filename: string, file: string) {
    let url = this.compilerUrl + '/deploy';
    let map = {'processId': processId, 'filename': filename, 'file': file};
    return this.httpClient.post<void>(url, map);
  }

  deployWithAnnotations(processId: string, filename: string, file: string) {
    let url = this.compilerUrl + '/deploy-with-annotations';
    let map = {'processId': processId, 'filename': filename, 'file': file};
    return this.httpClient.post<void>(url, map);
  }

  findOldProcessInstance(processDefinitionKey: string) {
    let url = this.engineUrl + '/find-old-process-instance?processDefinitionKey=' + processDefinitionKey;
    return this.httpClient.get<string[]>(url);
  }

  getProcessInstance(processInstanceId: string) {
    let url = this.engineUrl + '/get-process-instance?processInstanceId=' + processInstanceId;
    return this.httpClient.get<GetProcessInstance>(url);
  }

  migrateAll(processDefinitionKey: string) {
    let url = this.engineUrl + '/migrate-all';
    let map = {'processDefinitionKey': processDefinitionKey};
    return this.httpClient.post<number>(url, map);
  }

  migrateOne(processDefinitionKey: string, processInstanceId: string) {
    let url = this.engineUrl + '/migrate-one';
    let map = {'processDefinitionKey': processDefinitionKey, 'processInstanceId': processInstanceId};
    return this.httpClient.post<number>(url, map);
  }
}
