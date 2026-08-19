package com.confiapix.infrastructure.integration.bank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class IntegrationCredentialSchemas {

    public static final String GENERIC_OPEN_BANKING = "GENERIC_OPEN_BANKING";
    public static final String OAUTH_CLIENT_CREDENTIALS = "OAUTH_CLIENT_CREDENTIALS";
    public static final String STONE_API_KEY_OR_OPEN_BANKING = "STONE_API_KEY_OR_OPEN_BANKING";
    public static final String INTER_MTLS = "INTER_MTLS";
    public static final String C6_OAUTH = "C6_OAUTH";

    private IntegrationCredentialSchemas() {
    }

    public static Map<String, Object> schema(String schemaId) {
        return switch (schemaId) {
            case STONE_API_KEY_OR_OPEN_BANKING -> stoneSchema();
            case INTER_MTLS -> interSchema();
            case C6_OAUTH -> c6Schema();
            case OAUTH_CLIENT_CREDENTIALS -> oauthSchema(true);
            default -> genericSchema();
        };
    }

    private static Map<String, Object> stoneSchema() {
        List<Map<String, Object>> fields = new ArrayList<>();
        fields.add(section("Autenticação Stone"));
        fields.add(field("authMode", "Modo de autenticação", "select", true, null,
                "Escolha API Key (sk_...) para testes rápidos ou Open Banking para sync PIX.",
                false,
                List.of(option("API_KEY", "Chave API (SecretKey sk_...)"),
                        option("OPEN_BANKING", "Open Banking (OAuth)"))));
        fields.add(field("businessModel", "Modelo de negócio", "select", true, null,
                "Gateway: cobrança direta. Subadquirente: modelo marketplace.",
                false,
                List.of(option("GATEWAY", "Gateway"),
                        option("SUBACQUIRER", "Subadquirente"))));
        fields.add(section("Credenciais"));
        fields.add(field("clientSecret", "Secret Key (sk_...)", "password", true, "sk_live_... ou sk_test_...",
                "Encontre em Conta Stone → Integrações → Chaves de API.", false, null));
        fields.add(field("clientId", "Client ID OAuth", "text", false, "Usado apenas no modo Open Banking",
                "Obrigatório somente quando o modo Open Banking estiver selecionado.", false, null));
        fields.add(section("Identificação da conta"));
        fields.add(field("accountRef", "Account ID Stone", "text", true, "Ex.: 194047458",
                "ID numérico da conta recebedora na Stone (não é o Stonecode).", false, null));
        fields.add(field("merchantRef", "Stonecode / Merchant ID", "text", false, "Ex.: SEU-STONECODE",
                "Opcional. Identificador do estabelecimento comercial na Stone.", false, null));
        return wrap(STONE_API_KEY_OR_OPEN_BANKING, fields);
    }

    private static Map<String, Object> interSchema() {
        List<Map<String, Object>> fields = new ArrayList<>();
        fields.add(section("Aplicativo Inter Empresas"));
        fields.add(field("clientId", "Client ID", "text", true, "UUID gerado no Internet Banking",
                "Portal Inter Empresas → Soluções para sua empresa → Nova integração → Client ID.", false, null));
        fields.add(field("clientSecret", "Client Secret", "password", true, "Chave secreta da integração",
                "Gerado junto com o Client ID. Guarde em local seguro — não é exibido novamente.", false, null));
        fields.add(section("Conta recebedora"));
        fields.add(field("agency", "Agência", "text", true, "Ex.: 0001",
                "Agência da conta corrente vinculada à integração PIX.", false, null));
        fields.add(field("accountNumber", "Conta corrente", "text", true, "Ex.: 12345678",
                "Número da conta sem dígito.", false, null));
        fields.add(field("accountDigit", "Dígito", "text", false, "Ex.: 9",
                "Dígito verificador da conta, se aplicável.", false, null));
        fields.add(section("Certificado mTLS (obrigatório no Inter)"));
        fields.add(field("certificate", "Certificado (.crt / .pem)", "textarea", true,
                "-----BEGIN CERTIFICATE-----\n...\n-----END CERTIFICATE-----",
                "Cole o conteúdo do arquivo .crt exportado no Internet Banking.", true, null));
        fields.add(field("privateKey", "Chave privada (.key)", "textarea", true,
                "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----",
                "Cole o conteúdo do arquivo .key correspondente ao certificado.", true, null));
        return wrap(INTER_MTLS, fields);
    }

    private static Map<String, Object> c6Schema() {
        List<Map<String, Object>> fields = new ArrayList<>();
        fields.add(section("Credenciais C6 Developers"));
        fields.add(field("clientId", "Client ID", "text", true, "Identificador da aplicação",
                "Portal C6 Bank → Developers → sua aplicação → Client ID.", false, null));
        fields.add(field("clientSecret", "Client Secret", "password", true, "Chave secreta da aplicação",
                "Gerado no cadastro da aplicação. Necessário para autenticação OAuth.", false, null));
        fields.add(section("Conta e empresa"));
        fields.add(field("accountNumber", "Número da conta C6", "text", true, "Ex.: 123456789",
                "Conta corrente ou pagamento que receberá os PIX.", false, null));
        fields.add(field("companyDocument", "CNPJ da empresa", "text", false, "00.000.000/0001-00",
                "CNPJ titular da conta. Ajuda na conciliação multi-conta.", false, null));
        fields.add(field("merchantRef", "ID do parceiro / convênio", "text", false, "Opcional",
                "Use se o C6 forneceu um identificador de parceiro ou convênio comercial.", false, null));
        return wrap(C6_OAUTH, fields);
    }

    private static Map<String, Object> genericSchema() {
        List<Map<String, Object>> fields = new ArrayList<>();
        fields.add(section("Credenciais de acesso"));
        fields.add(field("clientId", "Client ID / App ID", "text", false, "Identificador fornecido pelo banco",
                "Disponível no portal de desenvolvedores ou Internet Banking da instituição.", false, null));
        fields.add(field("clientSecret", "Client Secret / API Key", "password", true, "Chave secreta",
                "Chave privada de autenticação. Nunca compartilhe ou exponha publicamente.", false, null));
        fields.add(section("Conta recebedora"));
        fields.add(field("agency", "Agência", "text", false, "Ex.: 1234",
                "Agência da conta que receberá os PIX.", false, null));
        fields.add(field("accountNumber", "Conta", "text", true, "Ex.: 567890",
                "Número da conta vinculada à integração.", false, null));
        fields.add(field("merchantRef", "Identificador adicional", "text", false, "Opcional",
                "Convênio, merchant ID ou outro código fornecido pelo banco.", false, null));
        return wrap(GENERIC_OPEN_BANKING, fields);
    }

    private static Map<String, Object> oauthSchema(boolean clientIdRequired) {
        List<Map<String, Object>> fields = new ArrayList<>();
        fields.add(section("Credenciais OAuth"));
        fields.add(field("clientId", "Client ID", "text", clientIdRequired, "client_id da aplicação",
                "Identificador OAuth registrado no portal do banco.", false, null));
        fields.add(field("clientSecret", "Client Secret", "password", true, "client_secret",
                "Chave secreta gerada no cadastro da aplicação.", false, null));
        fields.add(section("Conta"));
        fields.add(field("accountRef", "Identificador da conta", "text", true, "Conta ou ID fornecido pelo banco",
                "Número da conta, account ID ou identificador equivalente.", false, null));
        fields.add(field("merchantRef", "Identificador comercial (opcional)", "text", false, null,
                "Merchant ID, convênio ou código do estabelecimento.", false, null));
        return wrap(clientIdRequired ? OAUTH_CLIENT_CREDENTIALS : GENERIC_OPEN_BANKING, fields);
    }

    private static Map<String, Object> wrap(String id, List<Map<String, Object>> fields) {
        return Map.of("id", id, "fields", List.copyOf(fields));
    }

    private static Map<String, Object> section(String title) {
        Map<String, Object> field = new LinkedHashMap<>();
        field.put("name", "_section_" + title.hashCode());
        field.put("type", "section");
        field.put("label", title);
        field.put("required", false);
        return field;
    }

    private static Map<String, Object> field(
            String name,
            String label,
            String type,
            boolean required,
            String placeholder,
            String helpText,
            boolean fullWidth,
            List<Map<String, String>> options) {
        Map<String, Object> field = new LinkedHashMap<>();
        field.put("name", name);
        field.put("label", label);
        field.put("type", type);
        field.put("required", required);
        if (placeholder != null) {
            field.put("placeholder", placeholder);
        }
        if (helpText != null) {
            field.put("helpText", helpText);
        }
        if (fullWidth) {
            field.put("fullWidth", true);
        }
        if (options != null) {
            field.put("options", options);
        }
        return field;
    }

    private static Map<String, String> option(String value, String label) {
        return Map.of("value", value, "label", label);
    }
}
