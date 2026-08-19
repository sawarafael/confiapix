import { Component, OnInit, signal } from '@angular/core';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { DonutChartComponent, DonutSegment } from '../../shared/components/donut-chart/donut-chart.component';
import { GaugeChartComponent } from '../../shared/components/gauge-chart/gauge-chart.component';
import { AreaChartComponent, AreaPoint } from '../../shared/components/area-chart/area-chart.component';
import {
  DashboardApiService,
  PixApiService,
  ReconciliationApiService,
} from '../../core/services/api.services';
import { DashboardData, Reconciliation } from '../../core/models/api.models';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [
    StatCardComponent,
    DonutChartComponent,
    GaugeChartComponent,
    AreaChartComponent,
  ],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.scss',
})
export class DashboardPageComponent implements OnInit {
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly summary = signal<DashboardData | null>(null);
  readonly healthScore = signal(0);
  readonly healthLabel = signal('Carregando...');
  readonly donutSegments = signal<DonutSegment[]>([]);
  readonly areaPoints = signal<AreaPoint[]>([]);
  readonly kpis = signal<{ label: string; value: string }[]>([]);

  constructor(
    private readonly dashboardApi: DashboardApiService,
    private readonly pixApi: PixApiService,
    private readonly reconciliationApi: ReconciliationApiService,
  ) {}

  ngOnInit(): void {
    forkJoin({
      dashboard: this.dashboardApi.getSummary(),
      pix: this.pixApi.list(0, 50),
      reconciliations: this.reconciliationApi.list(),
    }).subscribe({
      next: ({ dashboard, pix, reconciliations }) => {
        const data = dashboard.data;
        this.summary.set(data);
        this.buildStats(data, reconciliations.data);
        this.buildDonut(reconciliations.data);
        this.buildArea(pix.data.content);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha ao carregar o painel');
        this.loading.set(false);
      },
    });
  }

  private buildStats(data: DashboardData, reconciliations: Reconciliation[]): void {
    const matched = reconciliations.filter((r) => r.status === 'MATCHED').length;
    const total = reconciliations.length || 1;
    const score = Math.round((matched / total) * 100);
    this.healthScore.set(score);
    this.healthLabel.set(
      score >= 70 ? 'Saudável' : score >= 40 ? 'Parcialmente seguro' : 'Atenção necessária',
    );

    this.kpis.set([
      { label: 'Conciliações concluídas', value: String(matched) },
      { label: 'Total de conciliações', value: String(reconciliations.length) },
      {
        label: 'Saldo projetado',
        value: `R$ ${Number(data.projectedBalance).toFixed(2)}`,
      },
      {
        label: 'Inadimplência (recebíveis)',
        value: `R$ ${Number(data.overdueReceivable).toFixed(2)}`,
      },
    ]);
  }

  private buildDonut(reconciliations: Reconciliation[]): void {
    const groups: Record<string, number> = {};
    for (const r of reconciliations) {
      groups[r.status] = (groups[r.status] ?? 0) + Number(r.receivedAmount);
    }

    const colors: Record<string, string> = {
      MATCHED: '#22c55e',
      PENDING: '#f59e0b',
      DIVERGENT: '#ef4444',
      MISMATCH: '#ef4444',
      MANUAL: '#6366f1',
    };

    const labels: Record<string, string> = {
      MATCHED: 'Conciliado',
      PENDING: 'Pendente',
      DIVERGENT: 'Divergente',
      MISMATCH: 'Divergente',
      MANUAL: 'Manual',
    };

    const segments = Object.entries(groups).map(([status, value]) => ({
      label: labels[status] ?? status,
      value,
      color: colors[status] ?? '#94a3b8',
    }));

    this.donutSegments.set(
      segments.length
        ? segments
        : [{ label: 'Sem dados', value: 1, color: '#64748b' }],
    );
  }

  private buildArea(pixList: { receivedAt: string; amount: number }[]): void {
    const byMonth = new Map<string, number>();
    for (const pix of pixList) {
      const d = new Date(pix.receivedAt);
      const key = d.toLocaleDateString('pt-BR', { month: 'short' });
      byMonth.set(key, (byMonth.get(key) ?? 0) + Number(pix.amount));
    }

    const points = [...byMonth.entries()].slice(-6).map(([label, value]) => ({ label, value }));
    this.areaPoints.set(
      points.length ? points : [{ label: '—', value: 0 }],
    );
  }
}
