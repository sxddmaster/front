<template>
  <div class="equity-change-table-container">
    <div class="table-wrapper">
      <table class="equity-change-table">
        <thead>
          <tr>
            <th rowspan="2">项目</th>
            <th rowspan="2">行次</th>
            <th colspan="6">本年金额</th>
            <th colspan="6">上年金额</th>
          </tr>
          <tr>
            <th>实收资本(或股本)</th>
            <th>资本公积</th>
            <th>减：库存股</th>
            <th>盈余公积</th>
            <th>未分配利润</th>
            <th>所有者权益合计</th>
            <th>实收资本(或股本)</th>
            <th>资本公积</th>
            <th>减：库存股</th>
            <th>盈余公积</th>
            <th>未分配利润</th>
            <th>所有者权益合计</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in equityList" :key="row.rowNo">
            <td>{{ row.item }}</td>
            <td>{{ row.rowNo }}</td>
            <td>{{ row.currentPaidInCapital }}</td>
            <td>{{ row.currentCapitalReserve }}</td>
            <td>{{ row.currentLessTreasuryStock }}</td>
            <td>{{ row.currentSurplusReserve }}</td>
            <td>{{ row.currentUndistributedProfit }}</td>
            <td>{{ row.currentTotalEquity }}</td>
            <td>{{ row.lastPaidInCapital }}</td>
            <td>{{ row.lastCapitalReserve }}</td>
            <td>{{ row.lastLessTreasuryStock }}</td>
            <td>{{ row.lastSurplusReserve }}</td>
            <td>{{ row.lastUndistributedProfit }}</td>
            <td>{{ row.lastTotalEquity }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

interface EquityChangeRow {
  item: string;
  rowNo: string;
  currentPaidInCapital: string;
  currentCapitalReserve: string;
  currentLessTreasuryStock: string;
  currentSurplusReserve: string;
  currentUndistributedProfit: string;
  currentTotalEquity: string;
  lastPaidInCapital: string;
  lastCapitalReserve: string;
  lastLessTreasuryStock: string;
  lastSurplusReserve: string;
  lastUndistributedProfit: string;
  lastTotalEquity: string;
}

const equityList = ref<EquityChangeRow[]>([]);

onMounted(async () => {
  const res = await axios.get('/api/equity-change');
  equityList.value = res.data.list;
});
</script>

<style scoped>
.equity-change-table-container {
  padding: 0;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}
.table-wrapper {
  flex: 1 1 auto;
  min-height: 0;
  max-height: 70vh;
  overflow-y: auto;
  overflow-x: auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 0;
  scrollbar-width: thin;
  scrollbar-color: #b6c6e3 #f8fafc;
}
.equity-change-table {
  border-collapse: separate;
  border-spacing: 0;
  width: max-content;
  min-width: 1200px;
  background: #fff;
}
.equity-change-table th, .equity-change-table td {
  border: 1px solid #b6c6e3;
  text-align: center;
  padding: 10px 8px;
  font-size: 16px;
  color: #222e3a;
  background: #fff;
  white-space: nowrap;
}
.equity-change-table th {
  color: #1746a2;
  background: #e3eaff;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 2px solid #2563eb;
}
@media (max-width: 900px) {
  .equity-change-table th, .equity-change-table td {
    font-size: 14px;
    padding: 7px 3px;
  }
  .equity-change-table {
    min-width: 800px;
  }
}
</style> 