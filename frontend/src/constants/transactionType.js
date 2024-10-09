export const TRANSACTION_TYPES = {
    PAYMENT: 1,
    TRANSFER: 2,
    DEPOSIT: 3,
    REFUND: 4,
    EXCHANGE: 5,
    RE_EXCHANGE: 6,
};

export const TRANSACTION_TYPES_KEY = [
    'none',
    'transaction_types_payment',
    'transaction_types_transfser',
    'transaction_types_deposit',
    'transaction_types_refund',
    'transaction_types_exchange',
    'transaction_types_reExchange',
];

//historiesmodal에서 사용중, 추후에 제거해야함 
export const TRANSACTION_TYPES_STRING_KO = {
    PAYMENT: '결제',
    TRANSFER: '송금',
    DEPOSIT: '충전',
    REFUND: '환불',
    EXCHANGE: '환전',
    RE_EXCHANGE: '환급',
};
