import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {IndexComponent} from "./component/index/index.component";
import {EditorComponent} from "./component/editor/editor.component";
import {PublicApiComponent} from "./component/public-api/public-api.component";
import {PublicApiListComponent} from "./component/public-api-list/public-api-list.component";
import {VerifyComponent} from "./component/verify/verify.component";
import {DeployComponent} from "./component/deploy/deploy.component";
import {ProcessLogComponent} from "./component/process-log/process-log.component";
import {ProcessVariableListComponent} from "./component/process-variable-list/process-variable-list.component";
import {ProcessVariableComponent} from "./component/process-variable/process-variable.component";
import {ProcessModelListComponent} from "./component/process-model-list/process-model-list.component";
import {ProcessModelComponent} from "./component/process-model/process-model.component";
import {InstanceMigrationComponent} from "./component/instance-migration/instance-migration.component";
import {InstanceMigrationListComponent} from "./component/instance-migration-list/instance-migration-list.component";
import {ProcessActivityListComponent} from "./component/process-activity-list/process-activity-list.component";
import {ProcessActivityComponent} from "./component/process-activity/process-activity.component";
import {ControllabilityModelComponent} from './component/controllability-model/controllability-model.component';
import {ControllabilityVerifyComponent} from './component/controllability-verify/controllability-verify.component';
import { ScenarioComponent } from './component/scenario/scenario.component';

const routes: Routes = [
  {path: '', redirectTo: '/index', pathMatch: 'full'},
  {path: 'index', component: IndexComponent},
  {path: 'editor', component: EditorComponent},
  {path: 'scenario', component: ScenarioComponent},
  {path: 'process-model-list', component: ProcessModelListComponent},
  {path: 'process-model/:id', component: ProcessModelComponent},
  {path: 'process-activity-list', component: ProcessActivityListComponent},
  {path: 'process-activity/:id', component: ProcessActivityComponent},
  {path: 'process-variable-list', component: ProcessVariableListComponent},
  {path: 'process-variable/:id', component: ProcessVariableComponent},
  {path: 'public-api-list', component: PublicApiListComponent},
  {path: 'public-api/:id', component: PublicApiComponent},
  {path: 'deploy', component: DeployComponent},
  {path: 'verify', component: VerifyComponent},
  {path: 'controllability-model', component: ControllabilityModelComponent},
  {path: 'controllability-verify', component: ControllabilityVerifyComponent},
  {path: 'instance-migration-list', component: InstanceMigrationListComponent},
  {path: 'instance-migration/:id', component: InstanceMigrationComponent},
  {path: 'process-log', component: ProcessLogComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
