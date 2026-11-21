import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ModulePageComponent } from './module-page/module-page.component';
import { ContractsComponent } from './contracts/contracts.component';
import { ContractDetailComponent } from './contracts/contract-detail.component';
import { ContractFormComponent } from './contracts/contract-form.component';
import { RequisitionsComponent } from './requisitions/requisitions.component';
import { RequisitionDetailComponent } from './requisitions/requisition-detail.component';
import { RequisitionFormComponent } from './requisitions/requisition-form.component';
import { VendorEvaluationsComponent } from './vendors/vendor-evaluations.component';
import { InvoicesComponent } from './invoices/invoices.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    ModulePageComponent,
    ContractsComponent,
    ContractDetailComponent,
    ContractFormComponent,
    RequisitionsComponent,
    RequisitionDetailComponent,
    RequisitionFormComponent,
    VendorEvaluationsComponent,
    InvoicesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    HttpClientXsrfModule.withOptions({ cookieName: 'XSRF-TOKEN', headerName: 'X-XSRF-TOKEN' }),
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
