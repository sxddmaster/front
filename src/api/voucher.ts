import axios from 'axios';

export interface VoucherRow {
  id: number;
  voucherDate: string;
  voucherNo: string;
  summary: string;
  subjectName: string;
  detailSubject: string;
  debit: number;
  credit: number;
  balance: number;
}

export interface PageParams {
  page: number;
  limit: number;
  date?: string;
}

export interface ExportParams {
  date?: string;
  companyCode: string;
}

interface PageResponse {
  totalCount: number;
  pageSize: number;
  totalPage: number;
  currPage: number;
  list: VoucherRow[];
}

interface ApiResponse<T> {
  code: number;
  msg: string;
  data: T;
}

export function fetchVoucherList(params: PageParams) {
  return axios.get<ApiResponse<PageResponse>>('/api/sys/accounting-voucher/list', { params });
}

export const exportVoucherList = (params: ExportParams) => {
  return axios.get('/api/sys/accounting-voucher/export', { 
    params,
    responseType: 'blob'  // 指定响应类型为blob
  });
}; 