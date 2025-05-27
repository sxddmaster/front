<template>
  <div class="data-table-layout">
    <el-row>
      <el-col :span="4">
        <el-menu :default-active="activeTab" @select="handleTabChange" class="tab-menu">
          <el-menu-item index="voucher">记账凭证</el-menu-item>
          <el-menu-item index="subjectSummary">科目汇总表</el-menu-item>
          <!-- <el-menu-item index="equityChange">所有者权益变动表</el-menu-item> -->
          <!-- <el-menu-item index="cashFlow">现金流量表</el-menu-item> -->
          <el-menu-item index="profit">利润表</el-menu-item>
          <el-menu-item index="balanceSheet">资产负债表</el-menu-item>
        </el-menu>
      </el-col>
      <el-col :span="20">
        <component :is="currentComponent" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineComponent } from 'vue';
import VoucherTable from './VoucherTable.vue';
import SubjectSummaryTable from './SubjectSummaryTable.vue';
// import EquityChangeTable from './EquityChangeTable.vue';
import CashFlowTable from './CashFlowTable.vue';
import ProfitTable from './ProfitTable.vue';
import BalanceSheetTable from './BalanceSheetTable.vue';

const activeTab = ref('voucher');
const tabMap: Record<string, any> = {
  voucher: VoucherTable,
  subjectSummary: SubjectSummaryTable,
  // equityChange: EquityChangeTable,
  cashFlow: CashFlowTable,
  profit: ProfitTable,
  balanceSheet: BalanceSheetTable,
};
const EmptyComponent = defineComponent({ template: '<div></div>' });
const currentComponent = computed(() => tabMap[activeTab.value] || EmptyComponent);
const handleTabChange = (key: string) => {
  activeTab.value = key;
};
</script>

<style scoped>
.data-table-layout {
  max-width: 1200px;
  margin: 40px auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.07);
  padding: 32px 24px 24px;
}
.tab-menu {
  min-height: 400px;
  border-right: 1px solid #eee;
}
</style> 