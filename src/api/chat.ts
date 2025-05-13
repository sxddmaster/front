import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export interface ChatResponse {
  message: string;
}

export const sendChatMessage = async (message: string, files: File[]): Promise<ChatResponse> => {
  const formData = new FormData();
  
  if (message) {
    formData.append('message', message);
  }
  
  files.forEach(file => {
    formData.append('files', file);
  });

  const response = await axios.post<ChatResponse>(`${API_BASE_URL}/chat`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data;
}; 