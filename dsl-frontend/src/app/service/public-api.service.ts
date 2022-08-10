import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Page} from "../model/page";
import {PublicApi} from "../model/public-api";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class PublicApiService {

  rootUrl = environment.compilerBackendUrl + '/api/public-api';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  findAll(page: number, size: number) {
    let url = this.rootUrl + '/find-all?page=' + page + '&size=' + size;
    return this.httpClient.get<Page<PublicApi>>(url);
  }

  findByProcessId(processId: string) {
    let url = this.rootUrl + '/find-by-process-id/' + processId;
    return this.httpClient.get<PublicApi[]>(url);
  }

  getOne(id: number) {
    let url = this.rootUrl + '/get-one/' + id;
    return this.httpClient.get<PublicApi>(url);
  }

  save(publicApi: PublicApi) {
    let url = this.rootUrl + '/save';
    return this.httpClient.post<PublicApi>(url, publicApi);
  }

  delete(id: number) {
    let url = this.rootUrl + '/delete/' + id;
    return this.httpClient.delete<void>(url);
  }
}
