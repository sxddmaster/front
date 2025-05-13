<template>
  <div class="subject-summary-table-container">
    <div class="table-wrapper">
      <table class="subject-summary-table">
        <thead>
          <tr>
            <th rowspan="2" class="subject-col">科目名称</th>
            <th rowspan="2">期初余额</th>
            <th colspan="2">本期发生额</th>
            <th rowspan="2">期末余额</th>
          </tr>
          <tr>
            <th>借方</th>
            <th>贷方</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in subjectList" :key="item.name">
            <td class="subject-col">{{ item.name }}</td>
            <td>{{ item.beginBalance }}</td>
            <td>{{ item.currentDebit }}</td>
            <td>{{ item.currentCredit }}</td>
            <td>{{ item.endBalance }}</td>
          </tr>
          <tr class="total-row">
            <td class="subject-col">合计</td>
            <td>{{ total.beginBalance }}</td>
            <td>{{ total.currentDebit }}</td>
            <td>{{ total.currentCredit }}</td>
            <td>{{ total.endBalance }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

interface SubjectSummaryRow {
  name: string;
  beginBalance: string | number;
  currentDebit: string | number;
  currentCredit: string | number;
  endBalance: string | number;
}

const subjectList = ref<SubjectSummaryRow[]>([]);
const total = ref<SubjectSummaryRow>({
  name: '合计',
  beginBalance: '',
  currentDebit: '',
  currentCredit: '',
  endBalance: '',
});

onMounted(async () => {
  const res = await axios.get('/api/subject-summary');
  subjectList.value = res.data.list;
  total.value = res.data.total;
});
</script>

<style scoped>
.subject-summary-table-container {
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
.table-wrapper::-webkit-scrollbar {
  height: 8px;
}
.table-wrapper::-webkit-scrollbar-thumb {
  background: #b6c6e3;
  border-radius: 4px;
}
.table-wrapper::-webkit-scrollbar-track {
  background: #f8fafc;
}
.subject-summary-table {
  border-collapse: separate;
  border-spacing: 0;
  width: max-content;
  min-width: 900px;
  background: #fff;
}
.subject-summary-table th, .subject-summary-table td {
  border: 1px solid #b6c6e3;
  text-align: center;
  padding: 10px 8px;
  font-size: 16px;
  color: #222e3a;
  background: #fff;
  white-space: nowrap;
}
.subject-summary-table th {
  color: #1746a2;
  background: #e3eaff;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 2px solid #2563eb;
}
.subject-col {
  text-align: left;
  padding-left: 18px;
  min-width: 160px;
  position: sticky;
  left: 0;
  background: linear-gradient(90deg, #e3eaff 90%, #fff 100%);
  z-index: 3;
  border-right: 2px solid #2563eb;
  box-shadow: 2px 0 8px -4px #b6c6e3;
  color: #1746a2;
  font-weight: 600;
}
.total-row {
  font-weight: bold;
  color: #fff;
  background: #2563eb;
}
@media (max-width: 900px) {
  .subject-summary-table th, .subject-summary-table td {
    font-size: 14px;
    padding: 7px 3px;
  }
  .subject-col {
    min-width: 110px;
    padding-left: 6px;
  }
  .subject-summary-table {
    min-width: 700px;
  }
}
</style> 