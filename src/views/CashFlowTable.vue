<template>
  <div class="cash-flow-table-container">
    <div class="filter-container">
      <el-date-picker
        v-model="date"
        type="month"
        placeholder="请选择月份"
        format="YYYY-MM"
        value-format="YYYY-MM"
        :clearable="true"
        style="width: 200px"
      />
      <el-input
        v-model="companyCode"
        placeholder="请输入公司代码"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><House /></el-icon>
        </template>
      </el-input>
      <el-select v-model="companyType" placeholder="请选择会计准则" style="width: 200px">
        <el-option label="小企业会计准则" :value="0" />
        <el-option label="企业会计准则" :value="1" />
      </el-select>
      <el-button type="primary" @click="handleSearch" :loading="loading">
        <el-icon><Search /></el-icon>
        查询
      </el-button>
      <el-button type="success" @click="handleExport" :loading="exporting">
        <el-icon><Download /></el-icon>
        导出
      </el-button>
    </div>
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
import { ElMessage } from 'element-plus';
import { Download, House, Search } from '@element-plus/icons-vue';
import { ElSelect, ElOption } from 'element-plus';

interface CashFlowRow {
  item: string;
  index: number | string;
  currentAmount: string | number;
  lastAmount: string | number;
  bold?: boolean;
  highlight?: boolean;
}

const cashFlowList = ref<CashFlowRow[]>([]);
const companyCode = ref('01');
const companyType = ref(0);
const loading = ref(true);
const exporting = ref(false);

function getDefaultDate() {
  const d = new Date();
  return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}`;
}
const date = ref<string>(getDefaultDate());

const handleSearch = () => {
  loadData();
};

const loadData = async () => {
  if (!companyCode.value) {
    ElMessage.warning('请输入公司代码');
    return;
  }
  loading.value = true;
  const params = { companyCode: companyCode.value, companyType: companyType.value, date: date.value || undefined };
  const res = await axios.get('/api/sys/cash-flow/list', { params });
  cashFlowList.value = res.data.data.list;
  loading.value = false;
};

const handleExport = async () => {
  if (exporting.value) return;
  if (!companyCode.value) {
    ElMessage.warning('请输入公司代码');
    return;
  }
  exporting.value = true;
  try {
    const params = { companyCode: companyCode.value, companyType: companyType.value, date: date.value || undefined };
    const response = await axios.get('/api/sys/cash-flow/export', {
      params,
      responseType: 'blob'
    });
    const blob = new Blob([response.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    const now = new Date();
    const fileName = `现金流量表_${now.getFullYear()}${(now.getMonth() + 1).toString().padStart(2, '0')}${now.getDate().toString().padStart(2, '0')}.xlsx`;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    ElMessage.success('导出成功');
  } catch (e) {
    ElMessage.error('导出失败：' + (e instanceof Error ? e.message : String(e)));
  } finally {
    exporting.value = false;
  }
};

onMounted(() => {
  loadData();
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
.filter-container {
  margin-bottom: 20px;
  padding: 0 20px;
  display: flex;
  gap: 12px;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  height: 60px;
}
</style> 