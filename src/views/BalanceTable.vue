<template>
  <div class="balance-table-container">
    <div class="filter-bar">
      <div class="filter-actions">
        <el-date-picker
          v-model="date"
          type="month"
          placeholder="请选择月份"
          format="YYYY-MM"
          value-format="YYYY-MM"
          :clearable="true"
          style="width: 200px;"
        />
        <el-button type="primary" @click="handleSearch" :loading="loading">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button type="success" @click="handleExport" :loading="exporting">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </div>
      <span v-if="companyName" class="company-name-label">公司：{{ companyName }}</span>
    </div>
    <div class="table-wrapper" v-if="!loading && !error">
      <table class="balance-table">
        <thead>
          <tr>
            <th rowspan="2" class="subject-col">科目名称</th>
            <th colspan="2">期初余额</th>
            <th colspan="2">本期发生额</th>
            <th colspan="2">本年累计发生额</th>
            <th colspan="2">期末余额</th>
          </tr>
          <tr>
            <th><b>借方</b></th>
            <th><b>贷方</b></th>
            <th><b>借方</b></th>
            <th><b>贷方</b></th>
            <th><b>借方</b></th>
            <th><b>贷方</b></th>
            <th><b>借方</b></th>
            <th><b>贷方</b></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in balanceList" :key="item.id">
            <td class="subject-col">{{ item.subjectName }}</td>
            <td>{{ formatNumber(item.openingDebit) }}</td>
            <td>{{ formatNumber(item.openingCredit) }}</td>
            <td>{{ formatNumber(item.currentDebit) }}</td>
            <td>{{ formatNumber(item.currentCredit) }}</td>
            <td>{{ formatNumber(item.yearDebit) }}</td>
            <td>{{ formatNumber(item.yearCredit) }}</td>
            <td>{{ formatNumber(item.endingDebit) }}</td>
            <td>{{ formatNumber(item.endingCredit) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <el-empty v-if="!loading && !error && balanceList.length === 0" description="暂无数据" />
    <el-skeleton v-if="loading" :rows="5" animated />
    <el-alert v-if="error" :title="error" type="error" show-icon />
    <div class="pagination-container" v-if="!loading && !error && balanceList.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="totalCount"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { Download, Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { useCompanyStore } from '../store/companyStore';

const companyStore = useCompanyStore();
const companyName = ref('');

function getDefaultDate() {
  const d = new Date();
  return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}`;
}
const date = ref<string>(getDefaultDate());

interface BalanceRow {
  id: number;
  subjectName: string;
  openingDebit: number;
  openingCredit: number;
  currentDebit: number;
  currentCredit: number;
  yearDebit: number;
  yearCredit: number;
  endingDebit: number;
  endingCredit: number;
}

const balanceList = ref<BalanceRow[]>([]);
const loading = ref(true);
const error = ref('');
const exporting = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const totalCount = ref(0);

const formatNumber = (value: number) => {
  if (value === null || value === undefined) return '0.00';
  return value.toFixed(2);
};

const handleSearch = () => {
  currentPage.value = 1;
  loadData();
};

const loadData = async () => {
  if (!companyStore.companyCode) {
    ElMessage.warning('请输入公司代码');
    return;
  }
  loading.value = true;
  error.value = '';
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      companyCode: companyStore.companyCode,
      date: (date?.value ?? undefined)
    };
    const res = await axios.get('/api/sys/balance/list', { params });
    if (res.data.code === 200) {
      const { list, totalCount: totalItems, pageSize: size, currPage } = res.data.data;
      balanceList.value = list;
      totalCount.value = totalItems;
      pageSize.value = size;
      currentPage.value = currPage;
    } else {
      throw new Error(res.data.msg || '获取数据失败');
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e);
    balanceList.value = [];
    totalCount.value = 0;
  } finally {
    loading.value = false;
  }
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
  loadData();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  loadData();
};

const handleExport = async () => {
  if (exporting.value) return;
  if (!companyStore.companyCode) {
    ElMessage.warning('请输入公司代码');
    return;
  }
  exporting.value = true;
  try {
    const params = {
      companyCode: companyStore.companyCode,
      date: (date?.value ?? undefined)
    };
    const response = await axios.get('/api/sys/balance/export', {
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
    const fileName = `科目余额表_${now.getFullYear()}${(now.getMonth() + 1).toString().padStart(2, '0')}${now.getDate().toString().padStart(2, '0')}.xlsx`;
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
  axios.get('/api/sys/company/list').then(res => {
    if (res.data && res.data.data) {
      const found = res.data.data.find((item: any) => item.companyCode === companyStore.companyCode);
      if (found) companyName.value = found.companyName;
    }
  });
  loadData();
});
</script>

<style scoped>
.balance-table-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  padding: 0;
  background: #f8fafc;
}
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  margin-bottom: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  height: 60px;
}
.filter-actions {
  display: flex;
  gap: 12px;
  align-items: center;
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
.balance-table {
  border-collapse: separate;
  border-spacing: 0;
  width: max-content;
  min-width: 1100px;
  background: #fff;
}
.balance-table th, .balance-table td {
  border: 1px solid #b6c6e3;
  text-align: center;
  padding: 10px 8px;
  font-size: 16px;
  color: #222e3a;
  background: #fff;
  white-space: nowrap;
}
.balance-table th {
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
@media (max-width: 1100px) {
  .balance-table th, .balance-table td {
    font-size: 14px;
    padding: 7px 3px;
  }
  .subject-col {
    min-width: 110px;
    padding-left: 6px;
  }
  .balance-table {
    min-width: 800px;
  }
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 0 20px;
}
.company-name-label {
  font-size: 1.08rem;
  color: #1746a2;
  font-weight: 600;
  background: #f0f9eb;
  border-radius: 8px;
  padding: 6px 18px;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
}
</style> 