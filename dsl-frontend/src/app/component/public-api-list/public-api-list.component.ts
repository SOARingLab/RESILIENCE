import {Component, OnInit} from '@angular/core';
import {PublicApi} from "../../model/public-api";
import {PublicApiService} from "../../service/public-api.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-public-api-list',
  templateUrl: './public-api-list.component.html',
  styleUrls: ['./public-api-list.component.css']
})
export class PublicApiListComponent implements OnInit {

  page = 0;
  size = 10;
  totalElements = 0;
  publicApiList: PublicApi[] = [];
  message: string | undefined;

  constructor(
    private router: Router,
    private publicApiService: PublicApiService,
  ) {
  }

  ngOnInit(): void {
    this.findByProcessId();
  }

  findByProcessId() {
    let processId = localStorage.getItem('processId');
    if (!processId) {
      return;
    }

    this.publicApiService.findByProcessId(processId).subscribe(publicApiList => {
      this.publicApiList = publicApiList;
    })
  }

  add() {
    let processId = localStorage.getItem('processId');
    if (!processId) {
      return;
    }

    let publicApi = new PublicApi();
    publicApi.processId = processId;
    this.publicApiService.save(publicApi).subscribe(publicApi => {
      this.router.navigate(['public-api', publicApi.id]).then();
    });
  }

  delete(id: number) {
    this.publicApiService.delete(id).subscribe(() => {
      this.findByProcessId();
      this.message = 'Deleted';
    });
  }
}
