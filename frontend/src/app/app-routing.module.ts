import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ModulePageComponent } from './module-page/module-page.component';
import { AuthGuard } from './shared/auth.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'contracts', component: ModulePageComponent, canActivate: [AuthGuard], data: { title: 'Contract repository' } },
  { path: 'procurement', component: ModulePageComponent, canActivate: [AuthGuard], data: { title: 'Procurement planning' } },
  { path: 'vendors', component: ModulePageComponent, canActivate: [AuthGuard], data: { title: 'Vendor evaluation' } },
  { path: 'invoices', component: ModulePageComponent, canActivate: [AuthGuard], data: { title: 'Invoices and payments' } },
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: '**', redirectTo: 'dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
