import axios from 'axios';

export interface VoucherEntry {
  subject: string;
  detailSubject: string;
  debit: string;
  credit: string;
}
export interface VoucherResponse {
  summary: string;
  entries: VoucherEntry[];
}

export const sendChatMessage = async (formData: FormData): Promise<VoucherResponse> => {
  const response = await axios.post<VoucherResponse>('http://localhost:8081/api/open-ai/send', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  return response.data;
}; 