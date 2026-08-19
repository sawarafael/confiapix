package com.confiapix.application.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PixStonePayloadParserTest {

    private final PixStonePayloadParser parser = new PixStonePayloadParser(new ObjectMapper());

    @Test
    void shouldParseOpenBankingPaymentItemPayload() {
        String json = """
                {
                  "id": "pix-id-stone",
                  "type": "inbound_pix_payment",
                  "amount": 15050,
                  "status": "SETTLED",
                  "end_to_end_id": "E165015552021062517465eeb40dd2f6",
                  "transaction_id": "TX123456",
                  "created_at": "2024-01-15T10:00:00Z",
                  "settled_at": "2024-01-15T10:00:01Z",
                  "source": {
                    "entity": {
                      "document": "12345678901",
                      "name": "João Silva",
                      "document_type": "cpf"
                    }
                  },
                  "target": {
                    "entity": {
                      "document": "98765432100",
                      "name": "Loja Exemplo",
                      "document_type": "cnpj"
                    }
                  }
                }
                """;

        PixStonePayloadParser.ParsedStonePayload parsed = parser.parse(json);

        assertThat(parsed.getStonePaymentId()).isEqualTo("pix-id-stone");
        assertThat(parsed.getPaymentType()).isEqualTo("inbound_pix_payment");
        assertThat(parsed.getStatus()).isEqualTo("SETTLED");
        assertThat(parsed.getPayer().getName()).isEqualTo("João Silva");
        assertThat(parsed.getReceiver().getName()).isEqualTo("Loja Exemplo");
    }

    @Test
    void shouldParseWebhookPayload() {
        String json = """
                {
                  "env": "sandbox",
                  "event_type": "inbound_pix_payment",
                  "event_happened_at": "2024-01-15T10:00:00Z",
                  "event_notified_at": "2024-01-15T10:00:02Z",
                  "target_data": {
                    "account_id": "194047458",
                    "amount": 10000,
                    "id": "wh-pix-id",
                    "status": "SETTLED",
                    "end_to_end_id": "E2E123",
                    "transaction_id": "TX-WH-1",
                    "created_at": "2024-01-15T09:59:59Z",
                    "settled_at": "2024-01-15T10:00:00Z",
                    "counter_party": {
                      "entity": {
                        "document": "11122233344",
                        "name": "Maria",
                        "document_type": "cpf"
                      }
                    }
                  }
                }
                """;

        PixStonePayloadParser.ParsedStonePayload parsed = parser.parse(json);

        assertThat(parsed.getEnvironment()).isEqualTo("sandbox");
        assertThat(parsed.getEventType()).isEqualTo("inbound_pix_payment");
        assertThat(parsed.getStoneAccountId()).isEqualTo("194047458");
        assertThat(parsed.getPayer().getName()).isEqualTo("Maria");
    }
}
