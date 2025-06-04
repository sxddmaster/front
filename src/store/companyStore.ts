import { defineStore } from 'pinia';

export const useCompanyStore = defineStore('company', {
  state: () => ({
    companyCode: '' as string,
    dataSource: '' as string
  }),
  actions: {
    setCompanyCode(code: string) {
      this.companyCode = code;
    },
    setDataSource(code: string) {
      this.dataSource = code;
    }
  }
}); 