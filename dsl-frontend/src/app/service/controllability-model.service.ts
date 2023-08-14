import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {ControllabilityModel} from "../model/controllability-model";

@Injectable({
  providedIn: 'root'
})
export class ControllabilityModelService {

  rootUrl = environment.compilerBackendUrl + '/api/controllability-model';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  findByProcessId(processId: string) {
    let url = this.rootUrl + '/find-by-process-id/' + processId;
    return this.httpClient.get<ControllabilityModel[]>(url);
  }

  getOne(id: number) {
    let url = this.rootUrl + '/get-one/' + id;
    return this.httpClient.get<ControllabilityModel>(url);
  }

  save(controllabilityModel: ControllabilityModel) {
    let url = this.rootUrl + '/save';
    return this.httpClient.post<ControllabilityModel>(url, controllabilityModel);
  }

  delete(id: number) {
    let url = this.rootUrl + '/delete/' + id;
    return this.httpClient.delete<void>(url);
  }
}
