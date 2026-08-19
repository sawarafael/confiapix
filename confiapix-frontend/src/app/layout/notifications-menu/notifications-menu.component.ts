import {
  Component,
  ElementRef,
  HostListener,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NotificationApiService } from '../../core/services/api.services';
import { AppNotification } from '../../core/models/api.models';

@Component({
  selector: 'app-notifications-menu',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './notifications-menu.component.html',
  styleUrl: './notifications-menu.component.scss',
})
export class NotificationsMenuComponent implements OnInit {
  private readonly notificationApi = inject(NotificationApiService);
  private readonly elementRef = inject(ElementRef);

  readonly open = signal(false);
  readonly loading = signal(false);
  readonly items = signal<AppNotification[]>([]);
  readonly unreadCount = signal(0);

  ngOnInit(): void {
    this.refreshUnreadCount();
  }

  toggle(): void {
    const next = !this.open();
    this.open.set(next);
    if (next) {
      this.loadNotifications();
    }
  }

  close(): void {
    this.open.set(false);
  }

  markAllRead(): void {
    this.notificationApi.markAllAsRead().subscribe({
      next: (res) => {
        this.unreadCount.set(res.data.count);
        this.items.update((list) => list.map((item) => ({ ...item, read: true })));
      },
    });
  }

  onItemClick(item: AppNotification): void {
    if (item.read) {
      return;
    }

    this.notificationApi.markAsRead(item.id).subscribe({
      next: () => {
        this.items.update((list) =>
          list.map((current) => (current.id === item.id ? { ...current, read: true } : current)),
        );
        this.unreadCount.update((count) => Math.max(0, count - 1));
      },
    });
  }

  iconFor(type: AppNotification['type']): string {
    switch (type) {
      case 'PIX_RECEIVED':
        return 'payments';
      case 'RECONCILIATION_MATCHED':
        return 'check_circle';
      case 'RECONCILIATION_DIVERGENT':
        return 'warning';
      case 'RECONCILIATION_PENDING':
        return 'schedule';
      case 'STONE_SYNC':
        return 'sync_alt';
      default:
        return 'notifications';
    }
  }

  linkFor(item: AppNotification): string | null {
    if (item.referenceType === 'PIX' && item.referenceId) {
      return `/pix/${item.referenceId}`;
    }
    if (item.referenceType === 'RECONCILIATION') {
      return '/reconciliations';
    }
    if (item.type === 'PIX_RECEIVED') {
      return '/pix';
    }
    return null;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.open()) {
      return;
    }

    const target = event.target as Node;
    if (!this.elementRef.nativeElement.contains(target)) {
      this.close();
    }
  }

  private loadNotifications(): void {
    this.loading.set(true);
    this.notificationApi.list(0, 10).subscribe({
      next: (res) => {
        this.items.set(res.data.content);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  private refreshUnreadCount(): void {
    this.notificationApi.unreadCount().subscribe({
      next: (res) => this.unreadCount.set(res.data.count),
    });
  }
}
