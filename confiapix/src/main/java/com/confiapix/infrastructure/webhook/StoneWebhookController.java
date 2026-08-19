package com.confiapix.infrastructure.webhook;

import com.confiapix.application.usecase.ProcessStoneWebhookUseCase;
import com.confiapix.infrastructure.integration.stone.dto.StoneWebhookPayload;
import com.confiapix.infrastructure.integration.stone.service.StoneWebhookPayloadResolver;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.StoneWebhookResponse;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks/stone")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Notificações externas")
public class StoneWebhookController {

    private final ProcessStoneWebhookUseCase processStoneWebhookUseCase;
    private final StoneWebhookPayloadResolver webhookPayloadResolver;

    @PostMapping("/pix")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Receber notificação de PIX recebido da Stone")
    public ApiResponse<StoneWebhookResponse> receivePix(@RequestBody JsonNode body) {
        StoneWebhookPayload payload = webhookPayloadResolver.resolve(body);
        StoneWebhookResponse result = processStoneWebhookUseCase.process(payload);
        return ApiResponse.ok("Webhook processado", result);
    }
}
