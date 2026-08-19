import { Component, input } from '@angular/core';

@Component({
  selector: 'app-gauge-chart',
  standalone: true,
  template: `
    <div class="gauge">
      <svg viewBox="0 0 200 120" class="gauge__svg">
        <defs>
          <linearGradient id="gaugeGrad" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stop-color="#ef4444" />
            <stop offset="50%" stop-color="#f59e0b" />
            <stop offset="100%" stop-color="#22c55e" />
          </linearGradient>
        </defs>
        <path
          class="gauge__track"
          d="M 20 100 A 80 80 0 0 1 180 100"
          fill="none"
          stroke-width="14"
          stroke-linecap="round"
        />
        <path
          d="M 20 100 A 80 80 0 0 1 180 100"
          fill="none"
          stroke="url(#gaugeGrad)"
          stroke-width="14"
          stroke-linecap="round"
          [attr.stroke-dasharray]="arcLength"
          [attr.stroke-dashoffset]="dashOffset()"
        />
        <circle class="gauge__needle" [attr.cx]="needleX()" cy="100" r="6" />
        <line
          class="gauge__needle"
          [attr.x1]="100"
          y1="100"
          [attr.x2]="needleX()"
          y2="100"
          stroke-width="2"
        />
      </svg>
      <p class="gauge__value">{{ value() }}%</p>
      <p class="gauge__label">{{ label() }}</p>
    </div>
  `,
  styles: `
    .gauge {
      text-align: center;
    }

    .gauge__svg {
      width: 100%;
      max-width: 260px;
      display: block;
      margin: 0 auto;
    }

    .gauge__track {
      stroke: var(--border);
    }

    .gauge__needle {
      fill: var(--text-primary);
      stroke: var(--text-primary);
    }

    .gauge__value {
      margin: 0.5rem 0 0;
      font-size: clamp(1.5rem, 5vw, 2rem);
      font-weight: 700;
      letter-spacing: -0.03em;
    }

    .gauge__label {
      margin: 0;
      color: var(--text-muted);
      font-size: 0.95rem;
    }
  `,
})
export class GaugeChartComponent {
  readonly value = input(0);
  readonly label = input('Saúde financeira');

  protected readonly arcLength = 251;

  dashOffset(): number {
    const pct = Math.min(100, Math.max(0, this.value())) / 100;
    return this.arcLength * (1 - pct);
  }

  needleX(): number {
    const pct = Math.min(100, Math.max(0, this.value())) / 100;
    const angle = Math.PI * (1 - pct);
    return 100 + Math.cos(angle) * 80;
  }
}
