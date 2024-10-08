<template>
  <div class="container-fluid py-4">
    <h3>Compare Exchange Rate</h3>

    <!-- USD to KRW Section -->
    <div class="card my-4">
      <div class="card-body">
        <div class="row px-3">
          <div class="col-md-8">
            <h6 class="mt-3">1 USD = {{ currentToKrw }} KRW</h6>
            <ExchangeRateChart chartId="toexchangeChart" :period="toSelectedPeriod" chartType="to" />
            <div class="chart-button-container">
              <template v-for="period in ['1y', '6m', '3m', '1m']" :key="period">
                <button class="chart-btn" :class="{ selected: toSelectedPeriod === period }"
                  @click="setToPeriod(period)">
                  {{ period }}
                </button>
              </template>
            </div>
          </div>
          <div class="col-md-4 d-flex flex-column justify-content-center mt-6">
            <input type="number" class="form-control mb-1" v-model.number="usdAmount" @input="convertToKrw"
              aria-label="Amount in USD" />
            <span class="text-center mb-1">=</span>
            <input type="text" class="form-control mb-3" :value="krwAmount" readonly aria-label="Amount in KRW" />
            <button class="btn btn-primary w-100" @click="handleExchange">
              Buy
            </button>
            <!-- 환율 알림 Section -->
            <div class="exchange-alert-container">
              <span class="alert-title">환율 알림 설정</span>
              <span class="alert-content">목표 환율 입력하시오.</span>
              <div class="exchange-input">
                <span>1 USD</span>
                <span class="equals-symbol">=</span>
                <input type="number" v-model="alertRateUsdToKrw" class="form-control alert-input" />
                <span>KRW</span>
              </div>
              <button class="btn btn-warning w-100 mt-3" @click="saveAlertRate(1, 0, alertRateUsdToKrw)">
                알림 설정
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- KRW to USD Section -->
    <div class="card my-4">
      <div class="card-body">
        <div class="row px-3">
          <div class="col-md-8">
            <h6 class="mt-3">1 KRW = {{ currentFromKrw }} USD</h6>
            <ExchangeRateChart chartId="fromexchangeChart" :period="fromSelectedPeriod" chartType="from" />
            <div class="chart-button-container">
              <template v-for="period in ['1y', '6m', '3m', '1m']" :key="period">
                <button class="chart-btn" :class="{ selected: fromSelectedPeriod === period }"
                  @click="setFromPeriod(period)">
                  {{ period }}
                </button>
              </template>
            </div>
          </div>
          <div class="col-md-4 d-flex flex-column justify-content-center mt-6">
            <input type="number" class="form-control mb-1" v-model.number="krwAmountReverse" @input="convertToUsd"
              aria-label="Amount in KRW" />
            <span class="text-center mb-1">=</span>
            <input type="number" class="form-control mb-3" :value="usdAmountReverse" readonly
              aria-label="Amount in USD" />
            <button class="btn btn-danger w-100" @click="reExchange">
              Sell
            </button>
            <!-- 환율 알림 Section -->
            <div class="exchange-alert-container">
              <span class="alert-title">환율 알림 설정</span>
              <span class="alert-content">목표 환율 입력하시오.</span>
              <div class="exchange-input">
                <span>1 KRW</span>
                <span class="equals-symbol">=</span>
                <input type="number" v-model="alertRateKrwToUsd" class="form-control alert-input" />
                <span>USD</span>
              </div>
              <button @click="saveAlertRate(0, 1, alertRateKrwToUsd)" class="btn btn-warning w-100 mt-3">
                알림 설정
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- 자동 환전 설정 섹션 -->
  <div class="container-fluid py-4">
    <h3 class="mt-3">자동 환전 설정</h3>
    <div class="card my-4">
      <div class="card-body">
        <div class="row px-3">
          <div class="col-md-12">
            <div class="exchange-input mb-3">
              <div class="input-group">
                <span class="input-group-text">1 USD =</span>
                <input type="number" v-model="targetExchange" class="form-control" placeholder="목표 환율을 입력하세요." />
                <span class="input-group-text">KRW</span>
              </div>
            </div>
            <div class="exchange-input mb-3">
              <div class="input-group">
                <span class="input-group-text">목표 금액 =</span>
                <input type="number" v-model="targetKrw" class="form-control" placeholder="자동 전환할 금액을 입력하세요." />
                <span class="input-group-text">KRW</span>
              </div>
            </div>
            <button class="btn btn-warning w-100" @click="confirmAutoExchange(1, 0, targetExchange, targetKrw)">
              자동 환전 설정
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useExchangeStore } from "@/stores/exchangeStore";
import ExchangeRateChart from "@/views/Chart/ExchangeRateChart.vue";
import axios from "axios";
import myaccountApi from "../../api/myaccountApi";
import { useRouter } from "vue-router";
import Swal from "sweetalert2";

// Data variables
const usdAmount = ref(1);
const krwAmount = ref(0);
const krwAmountReverse = ref(1000);
const usdAmountReverse = ref(0);

const store = useExchangeStore();

// 가져온 값을 Pinia에서 사용
const currentToKrw = computed(() => store.currentToKrw);
const currentFromKrw = computed(() => store.currentFromKrw);

// Conversion functions
const convertToKrw = () => {
  krwAmount.value = (usdAmount.value * currentToKrw.value).toFixed(2);
};

const convertToUsd = () => {
  usdAmountReverse.value = (
    krwAmountReverse.value * currentFromKrw.value
  ).toFixed(2);
};

// 환율 데이터를 가져오는 함수
const fetchExchangeRates = async () => {
  try {
    convertToKrw();
    convertToUsd();
  } catch (error) {
    console.error("환율 데이터를 가져오는 중 오류 발생:", error);
  }
};

// Period state
const toSelectedPeriod = ref("1y");
const fromSelectedPeriod = ref("1y");

// Set period functions
const setToPeriod = (period) => {
  toSelectedPeriod.value = period;
};

const setFromPeriod = (period) => {
  fromSelectedPeriod.value = period;
};

// 환전 함수
const handleExchange = async () => {
  try {
    const userNo = "5"; // 실제 사용자 번호로 대체해야 합니다
    const krwNo = "1234"; // 실제 KRW 계좌 번호로 대체해야 합니다
    const songNo = "1234"; // 실제 송이 페이 계좌 번호로 대체해야 합니다
    const exchangeRate = currentToKrw.value;
    const amount = usdAmount.value;

    const response = await myaccountApi.exchange({
      amount,
      exchangeRate,
      songAccountDTO: {
        songNo,
      },
      krwAccountDTO: {
        krwNo,
      },
      historyDTO: {
        userNo,
        songNo,
        krwNo,
        typeCode: 5, // 환전 코드
        stateCode: 1, // 상태 코드 (성공)
        historyContent: "USD → KRW 환전",
        amount,
        exchangeRate,
      },
    });

    if (response && response.data) {
      console.log("환전 성공:", response.data);
      Swal.fire({
        title: "성공!",
        text: "환전이 성공적으로 완료되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      });
    }
  } catch (error) {
    console.error("환전 중 오류 발생:", error);
    Swal.fire({
      title: "실패",
      text: "환전 중 오류가 발생했습니다. 다시 시도해 주세요.",
      icon: "error",
      confirmButtonText: "확인",
    });
  }
};

// 환급 함수
const reExchange = async () => {
  try {
    const userNo = "5"; // 실제 사용자 번호로 대체해야 합니다
    const krwNo = "1234"; // 실제 KRW 계좌 번호로 대체해야 합니다
    const songNo = "1234"; // 실제 송이 페이 계좌 번호로 대체해야 합니다
    const exchangeRate = currentFromKrw.value;
    const amount = krwAmountReverse.value;

    const response = await myaccountApi.reExchange({
      amount,
      exchangeRate,
      krwAccountDTO: {
        krwNo,
      },
      songAccountDTO: {
        songNo,
      },
      historyDTO: {
        userNo,
        songNo,
        krwNo,
        typeCode: 6, // 환급 코드
        stateCode: 1, // 상태 코드 (성공)
        historyContent: "KRW → USD 환급",
        amount,
        exchangeRate,
      },
    });

    if (response && response.data) {
      console.log("환급 성공:", response.data);
      Swal.fire({
        title: "성공!",
        text: "환급이 성공적으로 완료되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      });
    }
  } catch (error) {
    console.error("환급 중 오류 발생:", error);
    Swal.fire({
      title: "실패",
      text: "환급 중 오류가 발생했습니다. 다시 시도해 주세요.",
      icon: "error",
      confirmButtonText: "확인",
    });
  }
};

// 알림 저장 함수
const alertRateUsdToKrw = ref(null); // USD -> KRW 알림 목표 환율
const alertRateKrwToUsd = ref(null); // KRW -> USD 알림 목표 환율

const saveAlertRate = async (baseCode, targetCode, targetExchange) => {
  try {
    // const token = localStorage.getItem("jwt_token"); // JWT 토큰 가져오기
    // userNo를 추가(임시)
    const userNo = 1;
    console.log(userNo, baseCode, targetCode, targetExchange);
    // 서버에 POST 요청 보내기
    const response = await axios.post(
      "/api/exchange-reservation",
      {
        userNo: userNo,
        baseCode: baseCode,
        targetCode: targetCode,
        targetExchange: targetExchange,
      }
      // {
      //   headers: {
      //     Authorization: `Bearer ${token}`, // 인증 헤더에 토큰 추가
      //   },
      // }
    );

    if (response.status === 200) {
      Swal.fire({
        title: "성공!",
        text: "환율 알림이 성공적으로 저장되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      });
      router.push("/my-accounts");
    }
  } catch (error) {
    console.error("환율 알림 저장 중 오류 발생:", error);
    Swal.fire({
      title: "실패",
      html: `환율 알림 저장에 실패했습니다.<br>이유 : ${error.response?.data || "알 수 없는 오류"}`,
      icon: "error",
      confirmButtonText: "확인",
    });
  }
};

// 컴포넌트가 마운트될 때 데이터를 가져옴
onMounted(() => {
  fetchExchangeRates();
});

const router = useRouter();

// 상태 변수들을 ref로 선언
const targetExchange = ref(null); // 자동 환전 목표 환율
const targetKrw = ref(null); // 목표 금액

// 자동 환전 저장 함수
const confirmAutoExchange = async (
  baseCode,
  targetCode,
  targetExchange,
  targetKrw
) => {
  try {
    // const token = localStorage.getItem("jwt_token"); // JWT 토큰 가져오기
    // userNo를 추가(임시)
    const userNo = 1;
    console.log(userNo, baseCode, targetCode, targetExchange, targetKrw);
    // 서버에 POST 요청 보내기
    const response = await axios.post(
      "/api/exchange-reservation/setalert",
      {
        userNo: userNo,
        baseCode: baseCode,
        targetCode: targetCode,
        targetExchange: targetExchange,
        targetKrw: targetKrw,
      }
      // {
      //   headers: {
      //     Authorization: `Bearer ${token}`, // 인증 헤더에 토큰 추가
      //   },
      // }
    );

    if (response.status === 200) {
      Swal.fire({
        title: "성공!",
        text: "자동 환전 예약이 성공적으로 저장되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      });
      router.push("/my-accounts");
    }
  } catch (error) {
    console.error("자동 환전 예약 중 오류 발생:", error);
    Swal.fire({
      title: "실패",
      html: `자동 환전 예약에 실패했습니다.<br>이유 : ${error.response?.data || "알 수 없는 오류"}`,
      icon: "error",
      confirmButtonText: "확인",
    });
  }
};
</script>

<style scoped>
input[type="number"]::-webkit-outer-spin-button,
input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
}

.clickable-alert {
  text-align: center;
  cursor: pointer;
}

.chart-button-container {
  display: flex;
  justify-content: center;
  width: 100%;
  padding: 1rem;
}

.chart-btn {
  flex-grow: 1;
  /* 버튼들이 남은 공간을 균등하게 차지 */
  padding: 1px 0;
  /* 버튼 높이 */
  background-color: #e0e0e0;
  /* 기본 배경 색상 */
  color: #333;
  /* 기본 텍스트 색상 */
  border: none;
  border-radius: 20px;
  /* 둥근 모서리 */
  text-align: center;
  cursor: pointer;
  margin: 0 5px;
  /* 버튼 간 좌우 간격 */
}

.chart-btn.selected {
  background-color: #ffd700;
  /* 선택된 버튼의 배경색 */
  color: #fff;
  /* 선택된 버튼의 텍스트 색상 */
}

.chart-btn:hover {
  background-color: #f0f0f0;
  /* 호버 시 버튼 배경색 */
}

.exchange-alert-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 1rem;
  text-align: center;
}

.alert-title {
  font-weight: bold;
  margin-bottom: 0.5rem;
  text-align: left;
  width: 100%;
}

.alert-content {
  margin-bottom: 0.5rem;
  text-align: left;
  width: 100%;
  font-size: small;
}

.exchange-input {
  display: flex;
  /* align-items: center;
  justify-content: center; */
  gap: 0.5rem;
  width: 100%;
}

.alert-input {
  width: 60%;
  text-align: center;
  white-space: nowrap;
}

.equals-symbol {
  margin: 0 0.2rem;
  font-weight: bold;
}

.btn-warning {
  background-color: #ffcc00;
  border: none;
}

.btn-warning:hover {
  background-color: #ffbb00;
}

.delete-btn {
  flex-grow: 1;
  padding: 8px 16px;
  /* 적당한 크기로 버튼 높이 조정 */
  background-color: #f44336;
  /* 기본 배경 색상: 밝은 빨간색 */
  color: white;
  /* 텍스트 색상: 흰색 */
  border: none;
  border-radius: 20px;
  /* 둥근 모서리 */
  text-align: center;
  cursor: pointer;
  margin: 0 5px;
  /* 버튼 간 좌우 간격 */
  font-size: 14px;
  /* 텍스트 크기 */
  transition: background-color 0.3s ease;
  /* 배경색 전환 효과 */
}

.delete-btn:hover {
  background-color: #d32f2f;
  /* 호버 시 더 짙은 빨간색 */
}

.delete-btn:active {
  background-color: #b71c1c;
  /* 클릭 시 색상 */
}

.delete-btn:disabled {
  background-color: #e0e0e0;
  /* 비활성화된 버튼 색상 */
  color: #9e9e9e;
  /* 비활성화된 텍스트 색상 */
  cursor: not-allowed;
  /* 비활성화 상태에서는 커서 변경 */
}

.list-group-item {
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.list-group-item p {
  font-size: 0.9rem;
}

.btn-primary {
  background-color: #5a9;
  border-color: #5a9;
}

.btn-danger {
  background-color: #e57373;
  border-color: #e57373;
}

.targetbox {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.exchange-input {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.exchange-input input {
  flex-grow: 1;
}

.btn-warning {
  background-color: #ffcc00;
  border: none;
  color: #333;
}

.btn-warning:hover {
  background-color: #ffbb00;
}

.exchange-input .input-group {
  width: 100%;
}

.exchange-input .input-group-text {
  background-color: #f8f9fa;
  border-color: #ced4da;
}

.exchange-input .form-control {
  flex: 1;
}
</style>
