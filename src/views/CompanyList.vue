<template>
  <div class="company-list-wrapper">
    <h1 class="company-list-title">公司管理</h1>
    <div class="company-table-ops">
      <el-button type="primary" @click="openAddDialog">新增公司</el-button>
      <el-button @click="fetchCompanyList">刷新</el-button>
      <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">批量删除</el-button>
    </div>
    <el-table
      :data="companyList"
      style="width: 100%; max-width: 900px; margin: 0 auto;"
      @selection-change="handleSelectionChange"
      ref="tableRef"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="companyCode" label="公司代码" width="120" />
      <el-table-column prop="companyName" label="公司名称">
        <template #default="scope">
          <span class="company-link" @click="goToHome(scope.row.companyCode, scope.row.dataSource)">{{ scope.row.companyName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="companyType" label="公司类型" width="120">
        <template #default="scope">
          <span>{{ scope.row.companyType === 0 ? '小企业会计准则' : '企业会计准则' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新增公司' : '编辑公司'">
      <el-form :model="editForm" label-width="90px" style="max-width: 400px; margin: 0 auto;">
        <el-form-item v-if="dialogMode === 'edit'" label="公司代码">
          <el-input v-model="editForm.companyCode" disabled />
        </el-form-item>
        <el-form-item label="公司名称">
          <el-input v-model="editForm.companyName" />
        </el-form-item>
        <el-form-item label="公司类型">
          <el-select v-model="editForm.companyType" placeholder="请选择">
            <el-option label="小企业会计准则" :value="0" />
            <el-option label="企业会计准则" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import { storeToRefs } from 'pinia';
import { useCompanyStore } from '../store/companyStore';

interface CompanyOption {
  id?: number;
  companyCode?: string;
  companyName: string;
  companyType: number;
  dataSource?: string;
}

const companyList = ref<CompanyOption[]>([]);
const router = useRouter();
const tableRef = ref();
const multipleSelection = ref<CompanyOption[]>([]);

// 初始化 store
const store = useCompanyStore();
const { dataSource } = storeToRefs(store);

const dialogVisible = ref(false);
const dialogMode = ref<'add' | 'edit'>('add');
const editForm = reactive<CompanyOption>({
  id: undefined,
  companyName: '',
  companyType: 0
});

const fetchCompanyList = async () => {
  const res = await axios.get('http://localhost:8082/api/sys/company/list');
  if (res.data && res.data.data) {
    companyList.value = res.data.data;
  }
};

const openAddDialog = () => {
  dialogMode.value = 'add';
  editForm.id = undefined;
  delete editForm.companyCode;
  editForm.companyName = '';
  editForm.companyType = 0;
  dialogVisible.value = true;
};

const openEditDialog = (row: CompanyOption) => {
  dialogMode.value = 'edit';
  editForm.id = row.id;
  editForm.companyCode = row.companyCode;
  editForm.companyName = row.companyName;
  editForm.companyType = row.companyType;
  dialogVisible.value = true;
};

const handleSave = async () => {
  if (!editForm.companyName) {
    ElMessage.warning('公司名称不能为空');
    return;
  }
  if (dialogMode.value === 'add') {
    const { companyName, companyType } = editForm;
    await axios.post('http://localhost:8082/api/sys/company/save', { companyName, companyType });
    ElMessage.success('新增成功');
  } else {
    await axios.post('http://localhost:8082/api/sys/company/update', editForm);
    ElMessage.success('修改成功');
  }
  dialogVisible.value = false;
  fetchCompanyList();
};

const handleDelete = (row: CompanyOption) => {
  ElMessageBox.confirm('确定要删除该公司吗？', '提示', { type: 'warning' })
    .then(async () => {
      await axios.post('http://localhost:8082/api/sys/company/delete', [row.id]);
      ElMessage.success('删除成功');
      fetchCompanyList();
    })
    .catch(() => {});
};

const handleBatchDelete = () => {
  if (!multipleSelection.value.length) return;
  ElMessageBox.confirm('确定要批量删除选中的公司吗？', '提示', { type: 'warning' })
    .then(async () => {
      const ids = multipleSelection.value.map(item => item.id);
      await axios.post('http://localhost:8082/api/sys/company/delete', ids);
      ElMessage.success('批量删除成功');
      fetchCompanyList();
      if (tableRef.value) tableRef.value.clearSelection();
    })
    .catch(() => {});
};

const handleSelectionChange = (val: CompanyOption[]) => {
  multipleSelection.value = val;
};

const goToHome = (companyCode: string, dataSource?: string) => {
  try {
    store.setDataSource(dataSource || '');
    router.push({ name: 'home', query: { companyCode } });
  } catch (error) {
    console.error('Error setting dataSource:', error);
    router.push({ name: 'home', query: { companyCode } });
  }
};

onMounted(() => {
  fetchCompanyList();
});
</script>

<style scoped>
.company-list-wrapper {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding-top: 60px;
}
.company-list-title {
  font-size: 2.2rem;
  font-weight: 600;
  color: #303133;
  margin-bottom: 32px;
}
.company-table-ops {
  margin-bottom: 18px;
  width: 100%;
  max-width: 900px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
.company-link {
  color: #409eff;
  cursor: pointer;
  text-decoration: underline;
}
</style> 