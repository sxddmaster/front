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
          <tr v-for="i in Math.max(assetList.length, liabilityList.length)" :key="i">
            <td class="item-col">{{ assetList[i-1]?.item || '' }}</td>
            <td>{{ assetList[i-1]?.index || '' }}</td>
            <td>{{ assetList[i-1]?.end || '' }}</td>
            <td>{{ assetList[i-1]?.begin || '' }}</td>
            <td class="item-col">{{ liabilityList[i-1]?.item || '' }}</td>
            <td>{{ liabilityList[i-1]?.index || '' }}</td>
            <td>{{ liabilityList[i-1]?.end || '' }}</td>
            <td>{{ liabilityList[i-1]?.begin || '' }}</td>
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
  item: string;
  index: number | string;
  end: string | number;
  begin: string | number;
}

const assetList = ref<BalanceRow[]>([]);
const liabilityList = ref<BalanceRow[]>([]);

onMounted(async () => {
  const res = await axios.get('/api/balance-sheet');
  const all = res.data.list as BalanceRow[];
  // 假设 index === '34' 为分界点
  const splitIndex = all.findIndex(row => row.index === '34');
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
</style> 