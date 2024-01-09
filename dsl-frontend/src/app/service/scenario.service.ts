import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Scenario } from '../model/scenario';

@Injectable({
  providedIn: 'root'
})
export class ScenarioService {

  rootUrl = environment.compilerBackendUrl + '/api/scenario';

  constructor(
    private httpClient: HttpClient
  ) {
  }

  findAll() {
    let url = this.rootUrl + '/find-all';
    return this.httpClient.get<Scenario[]>(url);
  }

  getOne(id: number) {
    let url = this.rootUrl + '/get-one/' + id;
    return this.httpClient.get<Scenario>(url);
  }

  save(scenario: Scenario) {
    let url = this.rootUrl + '/save';
    return this.httpClient.post<Scenario>(url, scenario);
  }

  delete(id: number) {
    let url = this.rootUrl + '/delete/' + id;
    return this.httpClient.delete<void>(url);
  }
}
