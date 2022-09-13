import {Component, OnInit} from '@angular/core';
import {PublicApi} from "../../model/public-api";
import {PublicApiService} from "../../service/public-api.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-public-api',
  templateUrl: './public-api.component.html',
  styleUrls: ['./public-api.component.css']
})
export class PublicApiComponent implements OnInit {

  methods = ['GET', 'POST', 'PUT', 'DELETE'];
  id = 0;
  publicApi = new PublicApi();
  message: string | undefined;

  constructor(
    private route: ActivatedRoute,
    private publicApiService: PublicApiService,
  ) {
  }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.getOne();
  }

  range(num: number) {
    let arr = [];
    for (let i = 0; i < num; i++) {
      arr.push(i);
    }
    return arr;
  }

  getOne() {
    this.publicApiService.getOne(this.id).subscribe(publicApi => {
      this.publicApi = publicApi;
    });
  }

  save() {
    this.publicApiService.save(this.publicApi).subscribe(publicApi => {
      this.publicApi = publicApi;
      this.message = 'Saved';
    });
  }

  addInput() {
    this.publicApi.inputTos.push('');
    this.publicApi.inputFroms.push('');
  }

  deleteInput(index: number) {
    this.publicApi.inputFroms.splice(index, 1);
    this.publicApi.inputTos.splice(index, 1);
  }

  addOutput() {
    this.publicApi.outputTos.push('');
    this.publicApi.outputFroms.push('');
  }

  deleteOutput(index: number) {
    this.publicApi.outputFroms.splice(index, 1);
    this.publicApi.outputTos.splice(index, 1);
  }
}
