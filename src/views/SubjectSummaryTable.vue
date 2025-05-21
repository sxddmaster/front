<template>
  <div class="subject-summary-table-container">
    <div class="filter-container">
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
      <el-button type="primary" @click="handleSearch" :loading="loading">
        <el-icon><Search /></el-icon>
        查询
      </el-button>
      <el-button type="success" @click="handleExport" :loading="exporting">
        <el-icon><Download /></el-icon>
        导出
      </el-button>
    </div>

    <div class="table-wrapper" v-if="!loading && !error">
      <table class="subject-summary-table">
        <thead>
          <tr>
            <th rowspan="2" class="subject-col">科目名称</th>
            <th rowspan="2">期初余额</th>
            <th colspan="2">本期发生额</th>
            <th rowspan="2">期末余额</th>
            <th style="width: 40px"></th>
          </tr>
          <tr>
            <th>借方</th>
            <th>贷方</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <template v-for="item in subjectList" :key="item.id">
            <tr>
              <td class="subject-col">{{ item.subjectName }}</td>
              <td>{{ formatNumber(item.openingBalance) }}</td>
              <td>{{ formatNumber(item.debit) }}</td>
              <td>{{ formatNumber(item.credit) }}</td>
              <td>{{ formatNumber(item.closingBalance) }}</td>
              <td>
                <el-button
                  type="text"
                  size="small"
                  @click="toggleRow(item.id)"
                  v-if="item.list && item.list.length"
                >
                  <el-icon>
                    <ArrowDown v-if="!expandedRows.has(item.id)" />
                    <ArrowUp v-else />
                  </el-icon>
                </el-button>
              </td>
            </tr>
            <tr v-if="expandedRows.has(item.id)">
              <td colspan="6" style="padding:0;">
                <table class="nested-table">
                  <thead>
                    <tr>
                      <th>二级科目名称</th>
                      <th>借方</th>
                      <th>贷方</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="sub in item.list" :key="sub.itemName">
                      <td>{{ sub.itemName }}</td>
                      <td>{{ formatNumber(sub.debit) }}</td>
                      <td>{{ formatNumber(sub.credit) }}</td>
                    </tr>
                  </tbody>
                </table>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
    <el-empty v-if="!loading && !error && subjectList.length === 0" description="暂无数据" />
    <el-skeleton v-if="loading" :rows="5" animated />
    <el-alert v-if="error" :title="error" type="error" show-icon />
    
    <div class="pagination-container" v-if="!loading && !error && subjectList.length > 0">
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
import { Download, Search, House, ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

interface SubjectSummaryRow {
  id: number;
  subjectCode: string;
  subjectName: string;
  openingBalance: number;
  debit: number;
  credit: number;
  closingBalance: number;
  list?: { itemName: string; debit: number; credit: number }[];
}

const subjectList = ref<SubjectSummaryRow[]>([]);
const loading = ref(true);
const error = ref('');
const exporting = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const totalCount = ref(0);
const companyCode = ref('01');
const expandedRows = ref<Set<number>>(new Set());

const formatNumber = (value: number) => {
  if (value === null || value === undefined) return '0.00';
  return value.toFixed(2);
};

const handleSearch = () => {
  currentPage.value = 1; // 重置到第一页
  loadData();
};

const loadData = async () => {
  if (!companyCode.value) {
    ElMessage.warning('请输入公司代码');
    return;
  }
  
  loading.value = true;
  error.value = '';
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      companyCode: companyCode.value
    };
    const res = await axios.get('/api/sys/subject/list', { params });
    if (res.data.code === 200) {
      const { list, totalCount: totalItems, pageSize: size, currPage } = res.data.data;
      subjectList.value = list;
      totalCount.value = totalItems;
      pageSize.value = size;
      currentPage.value = currPage;
    } else {
      throw new Error(res.data.msg || '获取数据失败');
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e);
    subjectList.value = [];
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
  if (!companyCode.value) {
    ElMessage.warning('请输入公司代码');
    return;
  }
  
  exporting.value = true;
  try {
    const params = {
      companyCode: companyCode.value
    };
    
    const response = await axios.get('/api/sys/subject/export', {
      params,
      responseType: 'blob'
    });
    
    // 创建下载链接
    const blob = new Blob([response.data], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    
    // 设置文件名
    const date = new Date();
    const fileName = `科目汇总表_${date.getFullYear()}${(date.getMonth() + 1).toString().padStart(2, '0')}${date.getDate().toString().padStart(2, '0')}.xlsx`;
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

const toggleRow = (id: number) => {
  if (expandedRows.value.has(id)) {
    expandedRows.value.delete(id);
  } else {
    expandedRows.value.add(id);
  }
};

// 恢复 onMounted 钩子，以便首次加载数据
onMounted(() => {
  loadData();
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
  display: none;
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 0 20px;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #b6c6e3 inset;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #2563eb inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #2563eb inset;
}

:deep(.el-input__prefix) {
  color: #1746a2;
}

.nested-table {
  width: 100%;
  border-collapse: collapse;
  background: #f6f8fa;
  margin: 0;
}
.nested-table th, .nested-table td {
  border: 1px solid #b6c6e3;
  text-align: center;
  padding: 6px 8px;
  font-size: 14px;
  color: #333;
}
</style> 