import { Component, OnInit, signal, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { PixApiService } from '../../core/services/api.services';
import { PixDetail } from '../../core/models/api.models';
import {
  formatDocumentType,
  formatEnvironment,
  formatPaymentType,
  formatPixSource,
  formatReconciliationStatus,
  formatReconciliationStatusClass,
  formatStoneStatus,
  formatWebhookEvent,
} from '../../core/utils/display-labels';

@Component({
  selector: 'app-pix-detail-page',
  standalone: true,
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './pix-detail-page.component.html',
  styleUrl: './pix-detail-page.component.scss',
})
export class PixDetailPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly pixApi = inject(PixApiService);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly pix = signal<PixDetail | null>(null);

  protected readonly formatDocumentType = formatDocumentType;
  protected readonly formatPaymentType = formatPaymentType;
  protected readonly formatSource = formatPixSource;
  protected readonly formatStoneStatus = formatStoneStatus;
  protected readonly formatReconciliationStatus = formatReconciliationStatus;
  protected readonly reconciliationClass = formatReconciliationStatusClass;
  protected readonly formatEnvironment = formatEnvironment;
  protected readonly formatWebhookEvent = formatWebhookEvent;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error.set('PIX não informado');
      this.loading.set(false);
      return;
    }

    this.pixApi.getDetail(id).subscribe({
      next: (res) => {
        this.pix.set(res.data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha ao carregar detalhes do PIX');
        this.loading.set(false);
      },
    });
  }

  statusClass(status?: string): string {
    if (!status) {
      return 'badge';
    }
    return `badge badge--${status.toLowerCase()}`;
  }
}
