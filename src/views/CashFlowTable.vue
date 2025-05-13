<template>
  <div class="cash-flow-table-container">
    <div class="table-wrapper">
      <table class="cash-flow-table">
        <thead>
          <tr>
            <th class="item-col">项目</th>
            <th>行次</th>
            <th>本期金额</th>
            <th>上期金额</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in cashFlowList" :key="row.index" :class="{ highlight: row.highlight }">
            <td class="item-col" :style="{ fontWeight: row.bold ? 'bold' : 'normal' }">{{ row.item }}</td>
            <td>{{ row.index }}</td>
            <td>{{ row.currentAmount }}</td>
            <td>{{ row.lastAmount }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

interface CashFlowRow {
  item: string;
  index: number | string;
  currentAmount: string | number;
  lastAmount: string | number;
  bold?: boolean;
  highlight?: boolean;
}

const cashFlowList = ref<CashFlowRow[]>([]);

onMounted(async () => {
  const res = await axios.get('/api/cash-flow');
  cashFlowList.value = res.data.list;
});
</script>

<style scoped>
.cash-flow-table-container {
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
.cash-flow-table {
  border-collapse: separate;
  border-spacing: 0;
  width: max-content;
  min-width: 700px;
  background: #fff;
}
.cash-flow-table th, .cash-flow-table td {
  border: 1px solid #b6c6e3;
  text-align: center;
  padding: 10px 8px;
  font-size: 16px;
  color: #222e3a;
  background: #fff;
  white-space: nowrap;
}
.cash-flow-table th {
  color: #1746a2;
  background: #e3eaff;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 2px solid #2563eb;
}
.item-col {
  text-align: left;
  padding-left: 18px;
  min-width: 200px;
}
.highlight {
  background: #fff9c4;
}
</style> 