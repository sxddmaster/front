<template>
  <div>
    <el-table v-if="!loading && !error" :data="tableData" style="width: 100%">
      <el-table-column prop="date" label="日期" />
      <el-table-column prop="voucherNo" label="凭证号" />
      <el-table-column prop="summary" label="摘要" />
      <el-table-column prop="subject" label="科目名称" />
      <el-table-column prop="detailSubject" label="明细科目" />
      <el-table-column prop="debit" label="借方" />
      <el-table-column prop="credit" label="贷方" />
      <el-table-column prop="balance" label="余额" />
    </el-table>
    <el-empty v-if="!loading && !error && tableData.length === 0" description="暂无数据" />
    <el-skeleton v-if="loading" rows="5" animated />
    <el-alert v-if="error" :title="error" type="error" show-icon />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { fetchVoucherList } from '../api/voucher';
import type { VoucherRow } from '../api/voucher';

const tableData = ref<VoucherRow[]>([]);
const loading = ref(true);
const error = ref('');

onMounted(async () => {
  loading.value = true;
  error.value = '';
  try {
    const res = await fetchVoucherList();
    if (Array.isArray(res.data)) {
      tableData.value = res.data;
    } else {
      throw new Error('接口返回数据格式错误');
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e);
    tableData.value = [];
  } finally {
    loading.value = false;
  }
});
</script> 