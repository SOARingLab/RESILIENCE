import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {ProcessModel} from "../model/process-model";

@Injectable({
  providedIn: 'root'
})
export class ProcessModelService {

  rootUrl = environment.compilerBackendUrl + '/api/process-model';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  findByProcessId(processId: string) {
    let url = this.rootUrl + '/find-by-process-id/' + processId;
    return this.httpClient.get<ProcessModel[]>(url);
  }

  getOne(id: number) {
    let url = this.rootUrl + '/get-one/' + id;
    return this.httpClient.get<ProcessModel>(url);
  }

  save(processModel: ProcessModel) {
    let url = this.rootUrl + '/save';
    return this.httpClient.post<ProcessModel>(url, processModel);
  }

  delete(id: number) {
    let url = this.rootUrl + '/delete/' + id;
    return this.httpClient.delete<void>(url);
  }
}
