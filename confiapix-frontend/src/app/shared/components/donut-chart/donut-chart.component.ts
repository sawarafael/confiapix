import { Component, computed, input } from '@angular/core';
import { DecimalPipe } from '@angular/common';

export interface DonutSegment {
  label: string;
  value: number;
  color: string;
}

@Component({
  selector: 'app-donut-chart',
  standalone: true,
  imports: [DecimalPipe],
  template: `
    <div class="donut">
      <svg viewBox="0 0 42 42" class="donut__svg">
        @for (segment of arcs(); track segment.label) {
          <circle
            class="donut__ring"
            cx="21"
            cy="21"
            r="15.915"
            fill="transparent"
            [attr.stroke]="segment.color"
            stroke-width="4"
            [attr.stroke-dasharray]="segment.dash"
            [attr.stroke-dashoffset]="segment.offset"
          />
        }
      </svg>
      <div class="donut__center">
        <span class="donut__amount">R$ {{ centerValue() | number: '1.2-2' }}</span>
        <span class="donut__label">{{ centerLabel() }}</span>
      </div>
    </div>
    <ul class="donut__legend">
      @for (item of segments(); track item.label) {
        <li>
          <span class="dot" [style.background]="item.color"></span>
          <span>{{ item.label }}</span>
          <strong>{{ percent(item.value) }}%</strong>
        </li>
      }
    </ul>
  `,
  styles: `
    :host {
      display: grid;
      grid-template-columns: minmax(140px, 200px) minmax(0, 1fr);
      gap: 1.5rem 2rem;
      align-items: center;
      width: 100%;
      max-width: 520px;
      margin: 0 auto;
    }

    .donut {
      position: relative;
      width: 180px;
      height: 180px;
      margin: 0 auto;
    }

    .donut__svg {
      transform: rotate(-90deg);
      width: 100%;
      height: 100%;
    }

    .donut__center {
      position: absolute;
      inset: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      text-align: center;
      padding: 1rem;
    }

    .donut__amount {
      font-size: 1.1rem;
      font-weight: 700;
    }

    .donut__label {
      font-size: 0.75rem;
      color: var(--text-muted);
    }

    .donut__legend {
      list-style: none;
      margin: 0;
      padding: 0;
      display: flex;
      flex-direction: column;
      gap: 0.65rem;
    }

    .donut__legend li {
      display: grid;
      grid-template-columns: 12px 1fr auto;
      gap: 0.5rem;
      align-items: center;
      font-size: 0.85rem;
      color: var(--text-secondary);
    }

    .dot {
      width: 10px;
      height: 10px;
      border-radius: 3px;
    }

    strong {
      color: var(--text-primary);
    }

    @media (max-width: 640px) {
      :host {
        grid-template-columns: 1fr;
      }

      .donut {
        width: 160px;
        height: 160px;
      }
    }
  `,
})
export class DonutChartComponent {
  readonly segments = input.required<DonutSegment[]>();
  readonly centerLabel = input('Total');

  readonly total = computed(() =>
    this.segments().reduce((sum, s) => sum + s.value, 0),
  );

  readonly centerValue = computed(() => {
    const segs = this.segments();
    if (!segs.length) {
      return 0;
    }
    return Math.max(...segs.map((s) => s.value));
  });

  readonly arcs = computed(() => {
    const total = this.total() || 1;
    let offset = 25;
    return this.segments().map((segment) => {
      const pct = (segment.value / total) * 100;
      const dash = `${pct} ${100 - pct}`;
      const arc = { ...segment, dash, offset: `${offset}` };
      offset -= pct;
      return arc;
    });
  });

  percent(value: number): string {
    const total = this.total();
    if (!total) {
      return '0';
    }
    return ((value / total) * 100).toFixed(0);
  }
}
