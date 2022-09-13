import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientModule} from '@angular/common/http';

import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatBadgeModule} from '@angular/material/badge';
import {MatBottomSheetModule} from '@angular/material/bottom-sheet';
import {MatButtonModule} from '@angular/material/button';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatStepperModule} from '@angular/material/stepper';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatMenuModule} from '@angular/material/menu';
import {MatNativeDateModule, MatRippleModule} from '@angular/material/core';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelectModule} from '@angular/material/select';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSliderModule} from '@angular/material/slider';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatTreeModule} from '@angular/material/tree';
import {FormsModule} from "@angular/forms";
import {IndexComponent} from './component/index/index.component';
import {EditorComponent} from './component/editor/editor.component';
import {GeneratorComponent} from './component/generator/generator.component';
import {EditNodeComponent} from './component/edit-node/edit-node.component';
import {EditComponent} from './component/edit/edit.component';
import {PublicApiComponent} from './component/public-api/public-api.component';
import { PublicApiListComponent } from './component/public-api-list/public-api-list.component';
import { VerifyComponent } from './component/verify/verify.component';
import { DeployComponent } from './component/deploy/deploy.component';
import { ProcessLogComponent } from './component/process-log/process-log.component';
import { ProcessVariableComponent } from './component/process-variable/process-variable.component';
import { ProcessVariableListComponent } from './component/process-variable-list/process-variable-list.component';
import { ProcessModelListComponent } from './component/process-model-list/process-model-list.component';
import { ProcessModelComponent } from './component/process-model/process-model.component';
import { InstanceMigrationComponent } from './component/instance-migration/instance-migration.component';
import { InstanceMigrationListComponent } from './component/instance-migration-list/instance-migration-list.component';

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    EditorComponent,
    GeneratorComponent,
    EditNodeComponent,
    EditComponent,
    PublicApiComponent,
    PublicApiListComponent,
    VerifyComponent,
    DeployComponent,
    ProcessLogComponent,
    ProcessVariableComponent,
    ProcessVariableListComponent,
    ProcessModelListComponent,
    ProcessModelComponent,
    InstanceMigrationComponent,
    InstanceMigrationListComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatAutocompleteModule,
    MatBadgeModule,
    MatBottomSheetModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatStepperModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatNativeDateModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatRippleModule,
    MatSelectModule,
    MatSidenavModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatSortModule,
    MatTableModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    MatTreeModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
