import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {IndexComponent} from "./component/index/index.component";
import {EditorComponent} from "./component/editor/editor.component";
import {GeneratorComponent} from "./component/generator/generator.component";
import {EditComponent} from "./component/edit/edit.component";
import {PublicApiComponent} from "./component/public-api/public-api.component";
import {PublicApiListComponent} from "./component/public-api-list/public-api-list.component";
import {VerifyComponent} from "./component/verify/verify.component";
import {DeployComponent} from "./component/deploy/deploy.component";
import {ProcessLogComponent} from "./component/process-log/process-log.component";
import {ProcessVariableListComponent} from "./component/process-variable-list/process-variable-list.component";
import {ProcessVariableComponent} from "./component/process-variable/process-variable.component";
import {ProcessModelListComponent} from "./component/process-model-list/process-model-list.component";
import {ProcessModelComponent} from "./component/process-model/process-model.component";

const routes: Routes = [
  {path: '', redirectTo: '/index', pathMatch: 'full'},
  {path: 'index', component: IndexComponent},
  {path: 'editor', component: EditorComponent},
  {path: 'generator', component: GeneratorComponent},
  {path: 'edit', component: EditComponent},
  {path: 'process-model-list', component: ProcessModelListComponent},
  {path: 'process-model/:id', component: ProcessModelComponent},
  {path: 'process-variable-list', component: ProcessVariableListComponent},
  {path: 'process-variable/:id', component: ProcessVariableComponent},
  {path: 'public-api-list', component: PublicApiListComponent},
  {path: 'public-api/:id', component: PublicApiComponent},
  {path: 'deploy', component: DeployComponent},
  {path: 'verify', component: VerifyComponent},
  {path: 'process-log', component: ProcessLogComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
