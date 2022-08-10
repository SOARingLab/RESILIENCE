import {Injectable} from '@angular/core';
import {Edit} from "../model/edit";

@Injectable({
  providedIn: 'root'
})
export class EditService {

  edit = new Edit();

  constructor() {
  }
}
