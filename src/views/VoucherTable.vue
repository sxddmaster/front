<template>
  <div class="voucher-table-container">
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
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="voucherDate" label="日期" />
        <el-table-column prop="voucherNo" label="凭证号" />
        <el-table-column prop="summary" label="摘要" />
        <el-table-column prop="subjectName" label="科目名称" />
        <el-table-column prop="detailSubject" label="明细科目" />
        <el-table-column prop="debit" label="借方">
          <template #default="{ row }">
            {{ row.debit ? row.debit.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="credit" label="贷方">
          <template #default="{ row }">
            {{ row.credit ? row.credit.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额">
          <template #default="{ row }">
            {{ row.balance ? row.balance.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-empty v-if="!loading && !error && tableData.length === 0" description="暂无数据" />
    <el-skeleton v-if="loading" :rows="5" animated />
    <el-alert v-if="error" :title="error" type="error" show-icon />
    
    <div class="pagination-container" v-if="!loading && !error && tableData.length > 0">
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
import { Search, Download } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { fetchVoucherList, exportVoucherList } from '../api/voucher';
import type { VoucherRow } from '../api/voucher';
import axios from 'axios';
import { useCompanyStore } from '../store/companyStore';

const tableData = ref<VoucherRow[]>([]);
const loading = ref(true);
const error = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const totalCount = ref(0);

// 先定义获取默认月份的函数
function getDefaultDate() {
  const d = new Date();
  return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}`;
}
// 再声明 date，并直接初始化
const date = ref<string>(getDefaultDate());
const exporting = ref(false);

const companyStore = useCompanyStore();
const companyName = ref('');

const loadData = async () => {
  loading.value = true;
  error.value = '';
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      date: date.value || undefined,
      companyCode: companyStore.companyCode || ''
    };
    const res = await fetchVoucherList(params);
    if (res.data.code === 200) {
      const { list, totalCount: total } = res.data.data;
      tableData.value = list;
      totalCount.value = total;
    } else {
      throw new Error(res.data.msg || '获取数据失败');
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e);
    tableData.value = [];
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1; // 重置到第一页
  loadData();
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
  
  exporting.value = true;
  try {
    const params = {
      date: date.value || undefined,
      companyCode: companyStore.companyCode || ''
    };
    
    const response = await exportVoucherList(params);
    
    // 创建下载链接
    const blob = new Blob([response.data], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    
    // 设置文件名（确保使用 .xlsx 扩展名）
    const now = new Date();
    const fileName = `凭证列表_${now.getFullYear()}${(now.getMonth() + 1).toString().padStart(2, '0')}${now.getDate().toString().padStart(2, '0')}.xlsx`;
    link.download = fileName;
    
    // 触发下载
    document.body.appendChild(link);
    link.click();
    
    // 清理
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
.voucher-table-container {
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

.table-wrapper {
  flex: 1 1 auto;
  min-height: 0;
  max-height: 60vh;
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
  width: 8px;
}

.table-wrapper::-webkit-scrollbar-thumb {
  background: #b6c6e3;
  border-radius: 4px;
}

.table-wrapper::-webkit-scrollbar-track {
  background: #f8fafc;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 0 20px;
  background: #fff;
}

:deep(.el-table) {
  --el-table-border-color: #b6c6e3;
  --el-table-header-bg-color: #e3eaff;
  --el-table-header-text-color: #1746a2;
  --el-table-row-hover-bg-color: #f0f4ff;
}

:deep(.el-table th) {
  font-weight: 600;
  font-size: 16px;
  border-bottom: 2px solid #2563eb;
}

:deep(.el-table td) {
  font-size: 16px;
  color: #222e3a;
}

@media (max-width: 900px) {
  .table-wrapper {
    max-height: 40vh;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    font-size: 14px;
    padding: 7px 3px;
  }
}
</style> 