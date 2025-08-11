package com.darong.malgage_api.external.gpt.util;

public class PromptTemplates {

    private PromptTemplates() {}

    public static String systemPrompt(String categoryJson, String emotionJson, String paymentMethodsJson) {
        return """
            한국어 지출/수입 문장을 JSON 배열로 변환합니다. 여러 거래가 있으면 각 거래를 분리합니다.

            반드시 아래 스키마로만 "JSON 배열"을 반환하세요. 다른 텍스트는 절대 포함하지 마세요.
            [{
              "type": "income|expense",
              "amount": <number>,
              "description": <string>,
              "categoryId": <number|null>,   // 매칭 실패 시 null
              "paymentMethod": <string>,     // 아래 결제수단 중 하나 (Enum 이름 그대로)
              "emotionId": <number|null>,    // 매칭 실패 시 null
              "isInstallment": <boolean>,
              "installmentMonths": <number>
            }]

            카테고리 목록(JSON): %s
            감정 목록(JSON): %s
            사용 가능한 결제수단(Enum): %s

            규칙:
            - categoryId와 emotionId는 위 목록에서 "정확히 찾을 수 있을 때" 해당 id 사용, 그렇지 않으면 null로 설정
            - paymentMethod는 반드시 위 Enum 중 하나로 응답 (예: "CREDIT_CARD")
            - "할부/분할/개월" 언급 시 isInstallment=true, 개월수 미상이면 installmentMonths=12
            - "일시불/한번에"면 isInstallment=false 및 installmentMonths=0
            - 금액은 숫자만(원 단위 추정)
            - 최종 응답은 JSON 배열만
            """.formatted(categoryJson, emotionJson, paymentMethodsJson);
    }
}
