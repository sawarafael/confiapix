import { Component, computed, input } from '@angular/core';

export interface AreaPoint {
  label: string;
  value: number;
}

@Component({
  selector: 'app-area-chart',
  standalone: true,
  template: `
    @if (points().length < 2) {
      <div class="area-chart area-chart--empty">
        <div class="area-chart__placeholder" aria-hidden="true"></div>
        <p>Histórico insuficiente — aguardando mais transações PIX</p>
      </div>
    } @else {
      <div class="area-chart">
        <svg [attr.viewBox]="'0 0 ' + width + ' ' + height" preserveAspectRatio="none">
          <defs>
            <linearGradient id="areaFill" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="#6366f1" stop-opacity="0.35" />
              <stop offset="100%" stop-color="#6366f1" stop-opacity="0.02" />
            </linearGradient>
          </defs>
          <path [attr.d]="areaPath()" fill="url(#areaFill)" />
          <path [attr.d]="linePath()" fill="none" stroke="#6366f1" stroke-width="2.5" />
          @for (p of points(); track p.label; let i = $index) {
            <circle [attr.cx]="x(i)" [attr.cy]="y(p.value)" r="4" fill="#6366f1" />
          }
        </svg>
        <div class="area-chart__labels">
          @for (p of points(); track p.label) {
            <span>{{ p.label }}</span>
          }
        </div>
      </div>
    }
  `,
  styles: `
    .area-chart svg {
      width: 100%;
      height: 200px;
      display: block;
    }

    .area-chart__labels {
      display: flex;
      justify-content: space-between;
      margin-top: 0.5rem;
      font-size: 0.75rem;
      color: var(--text-muted);
    }

    .area-chart--empty {
      display: flex;
      flex-direction: column;
      justify-content: center;
      gap: 0.75rem;
      min-height: 200px;
    }

    .area-chart__placeholder {
      height: 120px;
      border-radius: 16px;
      background: linear-gradient(
        180deg,
        rgba(99, 102, 241, 0.08) 0%,
        rgba(99, 102, 241, 0.02) 100%
      );
      border: 1px dashed rgba(99, 102, 241, 0.2);
    }

    .area-chart--empty p {
      margin: 0;
      text-align: center;
      font-size: 0.875rem;
      color: var(--text-muted);
    }
  `,
})
export class AreaChartComponent {
  readonly points = input.required<AreaPoint[]>();

  protected readonly width = 400;
  protected readonly height = 160;
  protected readonly pad = 16;

  private maxValue(): number {
    const vals = this.points().map((p) => p.value);
    return Math.max(...vals, 1);
  }

  x(index: number): number {
    const count = this.points().length;
    if (count <= 1) {
      return this.width / 2;
    }
    const step = (this.width - this.pad * 2) / (count - 1);
    return this.pad + index * step;
  }

  y(value: number): number {
    const max = this.maxValue();
    const usable = this.height - this.pad * 2;
    return this.height - this.pad - (value / max) * usable;
  }

  linePath(): string {
    const pts = this.points();
    if (!pts.length) {
      return '';
    }
    return pts
      .map((p, i) => `${i === 0 ? 'M' : 'L'} ${this.x(i)} ${this.y(p.value)}`)
      .join(' ');
  }

  areaPath(): string {
    const line = this.linePath();
    if (!line) {
      return '';
    }
    const last = this.points().length - 1;
    return `${line} L ${this.x(last)} ${this.height - this.pad} L ${this.x(0)} ${this.height - this.pad} Z`;
  }
}
