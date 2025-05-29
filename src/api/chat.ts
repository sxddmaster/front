import axios from 'axios';

export interface VoucherEntry {
  subject: string;
  detailSubject: string;
  debit: string;
  credit: string;
}
export interface VoucherResponse {
  summary: string;
  voucherDate: string;
  businessNo: string;
  entries: VoucherEntry[];
}

export const sendChatMessage = async (formData: FormData, companyCode?: string): Promise<VoucherResponse> => {
  if (companyCode) {
    formData.append('companyCode', companyCode);
  }
  const response = await axios.post<VoucherResponse>('http://localhost:8081/api/open-ai/send', formData, {
    headers: { 'Content-Type': 'multipart/form-data; charset=UTF-8' }
  });
  return response.data;
}; 