import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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
import { AuthGuard } from './shared/auth.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'contracts/new', component: ContractFormComponent, canActivate: [AuthGuard] },
  { path: 'contracts/:id/edit', component: ContractFormComponent, canActivate: [AuthGuard] },
  { path: 'contracts/:id', component: ContractDetailComponent, canActivate: [AuthGuard] },
  { path: 'contracts', component: ContractsComponent, canActivate: [AuthGuard] },
  { path: 'procurement/new', component: RequisitionFormComponent, canActivate: [AuthGuard] },
  { path: 'procurement/:id/edit', component: RequisitionFormComponent, canActivate: [AuthGuard] },
  { path: 'procurement/:id', component: RequisitionDetailComponent, canActivate: [AuthGuard] },
  { path: 'procurement', component: RequisitionsComponent, canActivate: [AuthGuard] },
  { path: 'vendors', component: VendorEvaluationsComponent, canActivate: [AuthGuard] },
  { path: 'invoices', component: InvoicesComponent, canActivate: [AuthGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: '**', redirectTo: 'dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
