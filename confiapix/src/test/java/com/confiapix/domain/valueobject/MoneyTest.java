package com.confiapix.domain.valueobject;

import com.confiapix.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void shouldRejectNegativeAmount() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-1.00")))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("negativo");
    }

    @Test
    void shouldMatchEqualAmounts() {
        Money first = Money.of(new BigDecimal("10.50"));
        Money second = Money.of(new BigDecimal("10.5"));

        assertThat(first.matches(second)).isTrue();
    }
}
