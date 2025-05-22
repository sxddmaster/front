<template>
  <div class="balance-sheet-table-container">
    <div class="table-wrapper">
      <table class="balance-sheet-table">
        <thead>
          <tr>
            <th class="item-col">资产</th>
            <th>行次</th>
            <th>期末余额</th>
            <th>年初余额</th>
            <th class="item-col">负债和所有者权益（或股东权益）</th>
            <th>行次</th>
            <th>期末余额</th>
            <th>年初余额</th>
          </tr>
        </thead>
        <tbody>
          <tr class="fixed-row">
            <td class="item-col blue-bold">流动资产：</td>
            <td></td>
            <td></td>
            <td></td>
            <td class="item-col blue-bold">流动负债：</td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <tr v-for="i in Math.max(assetList.length, liabilityList.length)" :key="i">
            <td class="item-col">{{ assetList[i-1]?.itemName || '' }}</td>
            <td>{{ assetList[i-1]?.itemNo ?? '' }}</td>
            <td>{{ assetList[i-1] ? displayBalance(assetList[i-1], 'closingBalance') : '' }}</td>
            <td>{{ assetList[i-1] ? displayBalance(assetList[i-1], 'openingBalance') : '' }}</td>
            <td class="item-col">{{ liabilityList[i-1]?.itemName || '' }}</td>
            <td>{{ liabilityList[i-1]?.itemNo ?? '' }}</td>
            <td>{{ liabilityList[i-1] ? displayBalance(liabilityList[i-1], 'closingBalance') : '' }}</td>
            <td>{{ liabilityList[i-1] ? displayBalance(liabilityList[i-1], 'openingBalance') : '' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

interface BalanceRow {
  itemName: string;
  itemNo: number;
  closingBalance: number;
  openingBalance: number;
}

const assetList = ref<BalanceRow[]>([]);
const liabilityList = ref<BalanceRow[]>([]);
const companyCode = ref('01');

function displayBalance(row: BalanceRow, key: 'closingBalance' | 'openingBalance') {
  if (row.closingBalance === 0 && row.openingBalance === 0) {
    return '--';
  }
  return row[key];
}

onMounted(async () => {
  const params = { companyCode: companyCode.value };
  const res = await axios.get('/api/sys/balance-sheet/list', { params });
  const all = res.data.data as BalanceRow[];
  // itemNo === 31 为分界点
  const splitIndex = all.findIndex(row => row.itemNo === 31);
  assetList.value = splitIndex === -1 ? all : all.slice(0, splitIndex);
  liabilityList.value = splitIndex === -1 ? [] : all.slice(splitIndex);
});
</script>

<style scoped>
.balance-sheet-table-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  padding: 0;
  background: #f8fafc;
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
.balance-sheet-table {
  border-collapse: separate;
  border-spacing: 0;
  width: max-content;
  min-width: 900px;
  background: #fff;
}
.balance-sheet-table th, .balance-sheet-table td {
  border: 1px solid #b6c6e3;
  text-align: center;
  padding: 10px 8px;
  font-size: 16px;
  color: #222e3a;
  background: #fff;
  white-space: nowrap;
}
.balance-sheet-table th {
  color: #1746a2;
  background: #e3eaff;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 2px solid #2563eb;
}
.item-col {
  text-align: left;
  padding-left: 18px;
  min-width: 160px;
}
.fixed-row .blue-bold {
  color: #0033cc;
  font-weight: bold;
}
</style>