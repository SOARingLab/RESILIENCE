import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientModule} from '@angular/common/http';

import {MatLegacyAutocompleteModule as MatAutocompleteModule} from '@angular/material/legacy-autocomplete';
import {MatBadgeModule} from '@angular/material/badge';
import {MatBottomSheetModule} from '@angular/material/bottom-sheet';
import {MatLegacyButtonModule as MatButtonModule} from '@angular/material/legacy-button';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';
import {MatLegacyCheckboxModule as MatCheckboxModule} from '@angular/material/legacy-checkbox';
import {MatLegacyChipsModule as MatChipsModule} from '@angular/material/legacy-chips';
import {MatStepperModule} from '@angular/material/stepper';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatLegacyDialogModule as MatDialogModule} from '@angular/material/legacy-dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatIconModule} from '@angular/material/icon';
import {MatLegacyInputModule as MatInputModule} from '@angular/material/legacy-input';
import {MatLegacyListModule as MatListModule} from '@angular/material/legacy-list';
import {MatLegacyMenuModule as MatMenuModule} from '@angular/material/legacy-menu';
import {MatNativeDateModule, MatRippleModule} from '@angular/material/core';
import {MatLegacyPaginatorModule as MatPaginatorModule} from '@angular/material/legacy-paginator';
import {MatLegacyProgressBarModule as MatProgressBarModule} from '@angular/material/legacy-progress-bar';
import {MatLegacyProgressSpinnerModule as MatProgressSpinnerModule} from '@angular/material/legacy-progress-spinner';
import {MatLegacyRadioModule as MatRadioModule} from '@angular/material/legacy-radio';
import {MatLegacySelectModule as MatSelectModule} from '@angular/material/legacy-select';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatLegacySliderModule as MatSliderModule} from '@angular/material/legacy-slider';
import {MatLegacySlideToggleModule as MatSlideToggleModule} from '@angular/material/legacy-slide-toggle';
import {MatLegacySnackBarModule as MatSnackBarModule} from '@angular/material/legacy-snack-bar';
import {MatSortModule} from '@angular/material/sort';
import {MatLegacyTableModule as MatTableModule} from '@angular/material/legacy-table';
import {MatLegacyTabsModule as MatTabsModule} from '@angular/material/legacy-tabs';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatLegacyTooltipModule as MatTooltipModule} from '@angular/material/legacy-tooltip';
import {MatTreeModule} from '@angular/material/tree';
import {FormsModule} from "@angular/forms";
import {IndexComponent} from './component/index/index.component';
import {EditorComponent} from './component/editor/editor.component';
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
import { ProcessActivityListComponent } from './component/process-activity-list/process-activity-list.component';
import { ProcessActivityComponent } from './component/process-activity/process-activity.component';
import { ControllabilityModelComponent } from './component/controllability-model/controllability-model.component';
import { ControllabilityVerifyComponent } from './component/controllability-verify/controllability-verify.component';

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    EditorComponent,
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
    ProcessActivityListComponent,
    ProcessActivityComponent,
    ControllabilityModelComponent,
    ControllabilityVerifyComponent,
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
