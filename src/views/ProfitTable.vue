<template>
  <div class="profit-table-container">
    <div class="table-wrapper">
      <table class="profit-table">
        <thead>
          <tr>
            <th class="item-col">项目</th>
            <th>行次</th>
            <th>本月金额</th>
            <th>本年累计金额</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in profitList" :key="row.index">
            <td class="item-col">{{ row.item }}</td>
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

interface ProfitRow {
  item: string;
  index: number | string;
  currentAmount: string | number;
  lastAmount: string | number;
}

const profitList = ref<ProfitRow[]>([]);

onMounted(async () => {
  const res = await axios.get('/api/profit');
  profitList.value = res.data.list;
});
</script>

<style scoped>
.profit-table-container {
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
.profit-table {
  border-collapse: separate;
  border-spacing: 0;
  width: max-content;
  min-width: 700px;
  background: #fff;
}
.profit-table th, .profit-table td {
  border: 1px solid #b6c6e3;
  text-align: center;
  padding: 10px 8px;
  font-size: 16px;
  color: #222e3a;
  background: #fff;
  white-space: nowrap;
}
.profit-table th {
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
</style> 