package com.darong.malgage_api.global.constants;

public class PromptConstants {

    // 🎯 다중 기록 처리 최적화된 프롬프트
    public static final String SYSTEM_PROMPT = """
        한국어→JSON배열 변환. 여러 기록 분리 추출.
        
        필드: type(income/expense), amount(숫자), description, category, paymentMethod, emotion, isInstallment(bool), installmentMonths(숫자)
        
        카테고리: 식비,카페,교통,쇼핑,의료,문화,마트,교육,투자,미용,운동,술,캠핑,경조사,의류,기타,급여,용돈,부업,투자수익,기타수입
        결제: 신용카드,체크카드,현금,계좌이체
        감정: 기쁨,만족,보통,아쉬움,후회,화남
        
        할부: "할부/분할/개월"→true, "일시불/한번에"→false, 불명확→12
        
        [JSON객체,JSON객체,...] 형태로만 응답
        """;

    // 🚀 극단적 최적화 + 다중 기록 처리
    public static final String ULTRA_COMPACT_PROMPT = """
        한국어→JSON배열. 모든 거래 분리.
        
        예: "김밥3천원,커피2천원" → [{"type":"expense","amount":3000,"description":"김밥","category":"식비","paymentMethod":"신용카드","emotion":"만족","isInstallment":false,"installmentMonths":0},{"type":"expense","amount":2000,"description":"커피","category":"카페","paymentMethod":"신용카드","emotion":"만족","isInstallment":false,"installmentMonths":0}]
        
        카테고리: 식비,카페,교통,쇼핑,의료,문화,마트,교육,투자,미용,운동,술,캠핑,경조사,의류,기타,급여,용돈,부업,투자수익,기타수입
        할부키워드→true, 일시불→false
        배열만 응답
        """;

    // 📊 Few-Shot 예시로 다중 기록 학습 강화
    public static final String FEW_SHOT_PROMPT = """
        한국어→JSON배열. 각 거래 개별 분리.
        
        예시:
        입력: "점심에 김밥 3000원, 저녁에 치킨 15000원 먹었어"
        출력: [{"type":"expense","amount":3000,"description":"김밥","category":"식비","paymentMethod":"신용카드","emotion":"만족","isInstallment":false,"installmentMonths":0},{"type":"expense","amount":15000,"description":"치킨","category":"식비","paymentMethod":"신용카드","emotion":"만족","isInstallment":false,"installmentMonths":0}]
        
        입력: "노트북 100만원 12개월 할부"
        출력: [{"type":"expense","amount":1000000,"description":"노트북","category":"기타","paymentMethod":"신용카드","emotion":"만족","isInstallment":true,"installmentMonths":12}]
        
        카테고리: 식비,카페,교통,쇼핑,의료,문화,마트,교육,투자,미용,운동,술,캠핑,경조사,의류,기타,급여,용돈,부업,투자수익,기타수입
        배열만 응답
        """;

    // 🎯 다중 기록 감지 강화 버전
    public static final String MULTI_RECORD_ENHANCED_PROMPT = """
        한국어 가계부→JSON배열. 쉼표,접속사,시간표현으로 여러 거래 구분.
        
        분리키워드: "그리고","또","다음에","오후에","저녁에","아침에",쉼표,줄바꿈
        
        필드: type,amount,description,category,paymentMethod,emotion,isInstallment,installmentMonths
        카테고리: 식비,카페,교통,쇼핑,의료,문화,마트,교육,투자,미용,운동,술,캠핑,경조사,의류,기타,급여,용돈,부업,투자수익,기타수입
        
        할부키워드→true, 일시불→false
        JSON배열만
        """;

    // 📝 원본 프롬프트 (참고용)
    public static final String ORIGINAL_PROMPT = """
        한국어 가계부 입력을 JSON 배열로 변환하세요.
        
        필수 필드:
        - type: "income"/"expense"
        - amount: 숫자 (총액)
        - description: 간단설명
        - category: 식비,카페,교통/차량,쇼핑,의료,문화생활,마트/쇼핑몰,교육,투자,미용,운동,술/유흥,캠핑,경조사/선물,의류,기타,급여,용돈,부업,투자수익,기타수입
        - paymentMethod: 신용카드,체크카드,현금,계좌이체
        - emotion: 기쁨,만족,보통,아쉬움,후회,화남
        - isInstallment: true/false
        - installmentMonths: 숫자 (할부 개월수, 일시불이면 0)
        
        할부 판단 규칙:
        - "할부", "분할", "개월", "12개월", "24개월" 등이 언급되면 isInstallment: true
        - "일시불", "한번에", "현금" 등이 언급되면 isInstallment: false
        - 할부 개월수가 명시되면 installmentMonths에 해당 숫자 입력
        - 할부라고 하지만 개월수가 불명확하면 installmentMonths: 12 (기본값)
        - 일시불이거나 할부가 아니면 installmentMonths: 0
        
        반드시 JSON 배열 형태로만 응답하세요.
        """;

    // 🎯 추천: 다중 기록 + 토큰 절약
    public static final String RECOMMENDED_PROMPT = SYSTEM_PROMPT;

    // 🚀 최대 절약: Few-Shot으로 정확도 보장
    public static final String TOKEN_SAVER_PROMPT = ULTRA_COMPACT_PROMPT;

    // 🎪 정확도 최우선: 복잡한 다중 기록 처리
    public static final String ACCURACY_FIRST_PROMPT = FEW_SHOT_PROMPT;

    private PromptConstants() {} // 유틸리티 클래스
}