import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class DeployService {

  rootUrl = environment.compilerBackendUrl + '/api/deployment';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  deploy(processId: string, filename: string, file: string) {
    let url = this.rootUrl + '/deploy';
    let map = {'processId': processId, 'filename': filename, 'file': file};
    return this.httpClient.post<void>(url, map);
  }

  deployWithAnnotations(processId: string, filename: string, file: string) {
    let url = this.rootUrl + '/deploy-with-annotations';
    let map = {'processId': processId, 'filename': filename, 'file': file};
    return this.httpClient.post<void>(url, map);
  }
}
