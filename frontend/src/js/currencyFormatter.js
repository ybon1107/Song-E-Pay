


// 숫자 포맷팅 함수 추가
const formatNumber = (num) => {
    // 입력값이 숫자이면 포맷, 아니면 그대로 반환
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

// 통화 포맷팅 함수
const formatCurrency = (value, locale, currency) => {
    return new Intl.NumberFormat(locale, {
        style: 'currency',
        currency: currency,
        currencyDisplay: 'code'
    }).format(value);
};


export default {
    formatNumber,
    formatCurrency
}