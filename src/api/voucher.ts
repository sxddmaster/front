import axios from 'axios';

export interface VoucherRow {
  date: string;
  voucherNo: string;
  summary: string;
  subject: string;
  detailSubject: string;
  debit: string;
  credit: string;
  balance: string;
}

export function fetchVoucherList() {
  return axios.get<VoucherRow[]>('/api/voucher/list');
} 