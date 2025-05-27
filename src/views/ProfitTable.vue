<template>
  <div class="profit-table-container">
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
          <tr v-for="row in profitList" :key="row.itemNo">
            <td class="item-col">{{ row.itemName }}</td>
            <td>{{ row.itemNo }}</td>
            <td>{{ formatNumber(row.currentAmount) }}</td>
            <td>{{ formatNumber(row.totalAmount) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <el-pagination
      v-if="totalCount > 0"
      v-model:current-page="currPage"
      v-model:page-size="pageSize"
      :total="totalCount"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination-container"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { Download, House, Search } from '@element-plus/icons-vue';
import { ElSelect, ElOption } from 'element-plus';
import { useCompanyStore } from '../store/companyStore';

interface ProfitRow {
  itemName: string;
  itemNo: number | string;
  currentAmount: number;
  totalAmount: number;
}

interface CompanyOption {
  companyCode: string;
  companyName: string;
  companyType: number;
}

const profitList = ref<ProfitRow[]>([]);
const currPage = ref(1);
const pageSize = ref(10);
const totalCount = ref(0);
const exporting = ref(false);
const loading = ref(true);
const companyList = ref<CompanyOption[]>([]);
const companyStore = useCompanyStore();
const companyName = ref('');

function getDefaultDate() {
  const d = new Date();
  return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}`;
}
const date = ref<string>(getDefaultDate());

const formatNumber = (val: number) => {
  if (val === null || val === undefined) return '--';
  const formatted = Number(val).toFixed(2);
  return formatted === '0.00' ? '--' : formatted;
};

const handleSearch = () => {
      currPage.value = 1;
  loadData();
};

const loadData = async () => {
  if (!companyStore.companyCode) {
    ElMessage.warning('请输入公司代码');
    return;
  }
  loading.value = true;
  const params = {
    companyCode: companyStore.companyCode,
    page: currPage.value,
    limit: pageSize.value,
    date: date.value || undefined
  };
  const res = await axios.get('/api/sys/profit/list', { params });
  if (res.data && res.data.data) {
    profitList.value = res.data.data.list;
    totalCount.value = res.data.data.totalCount;
    pageSize.value = res.data.data.pageSize;
    currPage.value = res.data.data.currPage;
  }
  loading.value = false;
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currPage.value = 1;
  loadData();
};

const handleCurrentChange = (val: number) => {
  currPage.value = val;
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
      date: date.value || undefined
     };
    const response = await axios.get('/api/sys/profit/export', {
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
    const fileName = `利润表_${now.getFullYear()}${(now.getMonth() + 1).toString().padStart(2, '0')}${now.getDate().toString().padStart(2, '0')}.xlsx`;
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
  // 获取公司名称
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
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 0 20px;
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
.company-name-label {
  font-size: 1.08rem;
  color: #1746a2;
  font-weight: 600;
  background: #f0f9eb;
  border-radius: 8px;
  padding: 6px 18px;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
}
.filter-container, .company-name-bar { display: none; }
</style> 