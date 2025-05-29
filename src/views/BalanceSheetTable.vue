<template>
  <div class="balance-sheet-table-container">
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
import { ElMessage } from 'element-plus';
import { Download, House, Search } from '@element-plus/icons-vue';
import { ElSelect, ElOption } from 'element-plus';
import { useCompanyStore } from '../store/companyStore';

interface BalanceRow {
  itemName: string;
  itemNo: number;
  closingBalance: number;
  openingBalance: number;
}

interface CompanyOption {
  companyCode: string;
  companyName: string;
  companyType: number;
}

const assetList = ref<BalanceRow[]>([]);
const liabilityList = ref<BalanceRow[]>([]);
const loading = ref(true);
const exporting = ref(false);
const companyList = ref<CompanyOption[]>([]);
const companyStore = useCompanyStore();
const companyName = ref('');

function getDefaultDate() {
  const d = new Date();
  return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}`;
}
const date = ref<string>(getDefaultDate());

function displayBalance(row: BalanceRow, key: 'closingBalance' | 'openingBalance') {
  if (row.closingBalance === 0 && row.openingBalance === 0) {
    return '--';
  }
  return row[key];
}

const handleSearch = () => {
  loadData();
};

const loadData = async () => {
  if (!companyStore.companyCode) {
    ElMessage.warning('请输入公司代码');
    return;
  }
  loading.value = true;
  const params = { companyCode: companyStore.companyCode, date: date.value || undefined };
  const res = await axios.get('/api/sys/balance-sheet/list', { params });
  const all = res.data.data as BalanceRow[];
  // itemNo === 31 为分界点
  const splitIndex = all.findIndex(row => row.itemNo === 31);
  assetList.value = splitIndex === -1 ? all : all.slice(0, splitIndex);
  liabilityList.value = splitIndex === -1 ? [] : all.slice(splitIndex);
  loading.value = false;
};

const handleExport = async () => {
  if (exporting.value) return;
  if (!companyStore.companyCode) {
    ElMessage.warning('请输入公司代码');
    return;
  }
  exporting.value = true;
  try {
    const params = { companyCode: companyStore.companyCode, date: date.value || undefined };
    const response = await axios.get('/api/sys/balance-sheet/export', {
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
    const fileName = `资产负债表_${now.getFullYear()}${(now.getMonth() + 1).toString().padStart(2, '0')}${now.getDate().toString().padStart(2, '0')}.xlsx`;
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