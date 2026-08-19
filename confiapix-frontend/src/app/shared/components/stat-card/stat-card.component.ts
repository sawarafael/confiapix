import { Component, input } from '@angular/core';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [DecimalPipe],
  template: `
    <article class="stat-card">
      <p class="stat-card__label">{{ label() }}</p>
      <p class="stat-card__value">{{ prefix() }}{{ value() | number: '1.2-2' }}</p>
      @if (delta() !== null) {
        <p class="stat-card__delta" [class.positive]="delta()! >= 0" [class.negative]="delta()! < 0">
          {{ delta()! >= 0 ? '+' : '' }}{{ delta() | number: '1.2-2' }}%
        </p>
      }
    </article>
  `,
  styles: `
    .stat-card {
      background: var(--surface);
      border-radius: var(--radius-lg);
      padding: 1.25rem 1.5rem;
      box-shadow: var(--shadow-card);
      border: 1px solid var(--glass-border);
      min-height: 120px;
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      gap: 0.35rem;
    }

    .stat-card__label {
      margin: 0;
      font-size: 0.875rem;
      color: var(--text-muted);
      font-weight: 500;
    }

    .stat-card__value {
      margin: 0;
      font-size: clamp(1.35rem, 4vw, 1.65rem);
      font-weight: 700;
      color: var(--text-primary);
      letter-spacing: -0.02em;
    }

    .stat-card__delta {
      margin: 0;
      font-size: 0.8rem;
      font-weight: 600;
    }

    .positive {
      color: var(--success);
    }

    .negative {
      color: var(--warning);
    }
  `,
})
export class StatCardComponent {
  readonly label = input.required<string>();
  readonly value = input.required<number>();
  readonly prefix = input('R$ ');
  readonly delta = input<number | null>(null);
}
