package com.darong.malgage_api.external.gpt.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PromptTemplates {

    private PromptTemplates() {}

    public static String systemPrompt(String categoryJson, String emotionJson, String paymentMethodsJson, String baseDateTime) {

        return """
            한국어 지출/수입 문장을 JSON 배열로 변환합니다. 여러 거래가 있으면 각 거래를 분리합니다.

            반드시 아래 스키마로만 "JSON 배열"을 반환하세요. 다른 텍스트는 절대 포함하지 마세요.
            [{
              "type": "income|expense",
              "amount": <number>,
              "memo": <string>,
              "categoryId": <number>,   // 매칭 실패 시 0
              "paymentMethod": <string>,     // 아래 결제수단 중 하나 (Enum 이름 그대로)
              "emotionId": <number>,    // 매칭 실패 시 0
              "isInstallment": <boolean>,
              "installmentMonths": <number>
              "date": "<YYYY-MM-DDTHH:mm:ss>"
            }]

            카테고리 목록(JSON): %s
            감정 목록(JSON): %s
            사용 가능한 결제수단(Enum): %s
            기준시각(ISO8601, 서버가 주입): %s

            규칙:
            - categoryId와 emotionId는 위 목록에서 "정확히 찾을 수 있을 때" 해당 id 사용, 그렇지 않으면 0 반환
            - paymentMethod는 반드시 위 Enum 중 하나로 응답 (예: "CREDIT_CARD")
            - "할부/분할/개월" 언급 시 isInstallment=true, 개월수 미상이면 installmentMonths=12
            - "일시불/한번에"면 isInstallment=false 및 installmentMonths=0
            - 금액은 숫자만 응답, 한글 띄워쓰기 유의 (오 천원 = 5,000원 | 만 칠천원 = 17,000원)
            - 최종 응답은 JSON 배열만
            
            날짜/시간 파싱 규칙:
            - "오늘/어제/그저께/내일/모레" 등 상대 날짜는 기준시각을 기준으로 계산
            - "이번 주/지난 주 + 요일", "다음 주 + 요일" 지원 (한국어 요일 인식: 월~일)
            - "8월 3일", "2025년 8월 3일" 등 절대 날짜 인식 (연도 없으면 기준시각의 연도)
            - 시간 표현(오전/오후/AM/PM/시/분)이 있으면 HH:mm:ss로 반영, 없으면 "T00:00:00"
            - 시간만 있고 날짜 언급이 없으면 기준시각의 날짜를 사용
            - 여러 거래에 서로 다른 날짜가 언급되면 각 거래별 date를 다르게 설정
            - 반환 형식은 항상 "YYYY-MM-DDTHH:mm:ss" (로컬 시간 기준; 타임존 표기 금지)
            
            
            메모(memo) 구성 규칙:
            - 사용자가 말한 "장소/상호/플랫폼/지점/지역" 등의 맥락(예: '스타벅스에서', '강남역 이마트에서', '배달의민족으로')이 있으면 이를 memo에 포함
            - 품목/상세(예: '아메리카노', '점심', '버스비', '택시')가 있으면 함께 포함
            - 불필요한 조사/감탄사/군더더기는 제거하고 핵심 키워드만 간결하게 기록
            """.formatted(categoryJson, emotionJson, paymentMethodsJson, baseDateTime);
    }
}
