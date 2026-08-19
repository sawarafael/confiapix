import { Component, computed, inject, signal, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../core/auth/auth.service';
import { NavItem } from '../../core/models/api.models';
import { NotificationsMenuComponent } from '../notifications-menu/notifications-menu.component';
import { ProfileMenuComponent } from '../profile-menu/profile-menu.component';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NotificationsMenuComponent, ProfileMenuComponent],
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.scss',
})
export class AdminLayoutComponent {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly darkMode = signal(this.readInitialDarkMode());
  readonly menuOpen = signal(false);

  private readonly navItems: NavItem[] = [
    { label: 'Acessos', route: '/access', icon: 'domain_add', platformOnly: true },
    { label: 'Início', route: '/dashboard', icon: 'dashboard' },
    { label: 'PIX', route: '/pix', icon: 'payments' },
    { label: 'Conciliações', route: '/reconciliations', icon: 'sync_alt' },
    { label: 'Integrações', route: '/integrations', icon: 'account_balance' },
    { label: 'Configurações', route: '/settings', icon: 'settings' },
  ];

  readonly visibleNavItems = computed(() =>
    this.navItems.filter((item) => !item.platformOnly || this.auth.isPlatformAdmin()),
  );

  constructor() {
    this.applyTheme(this.darkMode());

    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => this.closeMenu());
  }

  toggleMenu(): void {
    this.menuOpen.update((open) => !open);
    this.syncBodyScroll();
  }

  closeMenu(): void {
    if (!this.menuOpen()) {
      return;
    }
    this.menuOpen.set(false);
    this.syncBodyScroll();
  }

  toggleTheme(): void {
    this.darkMode.update((value) => !value);
    this.applyTheme(this.darkMode());
  }

  private applyTheme(dark: boolean): void {
    document.body.classList.toggle('dark-mode', dark);
    localStorage.setItem('confiapix_theme', dark ? 'dark' : 'light');
  }

  private readInitialDarkMode(): boolean {
    const saved = localStorage.getItem('confiapix_theme');
    if (saved === 'dark') {
      return true;
    }
    if (saved === 'light') {
      return false;
    }
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  }

  private syncBodyScroll(): void {
    document.body.classList.toggle('nav-open', this.menuOpen());
  }
}
