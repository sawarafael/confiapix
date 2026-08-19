import { Routes } from '@angular/router';
import { authGuard, guestGuard, platformAdminGuard } from './core/auth/auth.guard';
import { AdminLayoutComponent } from './layout/admin-layout/admin-layout.component';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/auth/login-page.component').then((m) => m.LoginPageComponent),
  },
  {
    path: '',
    canActivate: [authGuard],
    component: AdminLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'access',
        canActivate: [platformAdminGuard],
        loadComponent: () =>
          import('./features/access/tenant-access-page.component').then(
            (m) => m.TenantAccessPageComponent,
          ),
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard-page.component').then(
            (m) => m.DashboardPageComponent,
          ),
      },
      {
        path: 'pix',
        loadComponent: () =>
          import('./features/pix/pix-page.component').then((m) => m.PixPageComponent),
      },
      {
        path: 'pix/:id',
        loadComponent: () =>
          import('./features/pix/pix-detail-page.component').then((m) => m.PixDetailPageComponent),
      },
      {
        path: 'reconciliations',
        loadComponent: () =>
          import('./features/reconciliations/reconciliations-page.component').then(
            (m) => m.ReconciliationsPageComponent,
          ),
      },
      {
        path: 'integrations',
        loadComponent: () =>
          import('./features/integrations/integrations-page.component').then(
            (m) => m.IntegrationsPageComponent,
          ),
      },
      {
        path: 'integrations/:provider',
        loadComponent: () =>
          import('./features/integrations/integration-detail-page.component').then(
            (m) => m.IntegrationDetailPageComponent,
          ),
      },
      {
        path: 'stone',
        redirectTo: 'integrations/stone',
        pathMatch: 'full',
      },
      {
        path: 'settings',
        loadComponent: () =>
          import('./features/settings/settings-page.component').then((m) => m.SettingsPageComponent),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile-page.component').then((m) => m.ProfilePageComponent),
      },
    ],
  },
  { path: '**', redirectTo: 'login' },
];
