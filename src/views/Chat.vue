<template>
  <div class="chat-page">
    <div class="chat-container">
      <div class="messages" ref="messagesRef">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['message', msg.role]">
          <div class="message-content" :class="msg.role">
            <div class="avatar">
              <el-icon v-if="msg.role === 'user'"><User /></el-icon>
              <el-icon v-else><Service /></el-icon>
            </div>
            <div class="text">
              <div class="role-name">{{ msg.role === 'user' ? '你' : '' }}</div>
              <div class="content" :class="msg.role">
                <!-- 用户消息：自动格式化JSON并靠左显示 -->
                <template v-if="msg.role === 'user'">
                  <div class="user-message-bubble">
                    <template v-if="msg.fileName && msg.fileRaw">
                      <span class="chat-file-badge" @click="handlePreviewFile({ name: msg.fileName, raw: msg.fileRaw, uid: '' })">
                        <el-icon class="chat-file-icon"><PictureFilled /></el-icon>
                        <span class="chat-file-name">{{ msg.fileName }}</span>
                      </span>
                    </template>
                    <template v-if="msg.image && msg.imageType === 'image'">
                      <img :src="msg.image" class="chat-image" />
                    </template>
                    <template v-if="msg.text">
                      <div>{{ msg.text }}</div>
                    </template>
                  </div>
                </template>
                <!-- AI消息：优先展示text，其次voucher -->
                <template v-else-if="msg.role === 'ai'">
                  <template v-if="msg.text">
                    <div style="white-space: pre-wrap;">{{ msg.text }}</div>
                  </template>
                  <template v-else-if="msg.vouchers">
                    <div v-for="(voucher, index) in msg.vouchers" :key="index" class="voucher-container">
                      <div class="voucher-title">凭证 {{ index + 1 }}</div>
                      <div>摘要：{{ voucher.summary }}</div>
                      <div>业务编码：{{ voucher.businessNo || '-' }}</div>
                      <div class="voucher-date">凭证日期：{{ voucher.voucherDate || '-' }}</div>
                      <table class="voucher-table">
                        <thead>
                          <tr>
                            <th>序号</th>
                            <th>科目</th>
                            <th>明细科目</th>
                            <th>借方</th>
                            <th>贷方</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="(entry, i) in voucher.entries" :key="i">
                            <td>{{ entry.orderNo }}</td>
                            <td>{{ entry.subject }}</td>
                            <td>{{ entry.detailSubject }}</td>
                            <td>{{ entry.debit }}</td>
                            <td>{{ entry.credit }}</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </template>
                </template>
              </div>
            </div>
          </div>
        </div>
        <div v-if="!messages.length" class="welcome-message">
          <h2>欢迎记账凭证助手</h2>
          <p>开始对话吧，请上传原始凭证</p>
        </div>
      </div>
      
      <div class="input-area">
        <div v-if="uploadedFiles.length" class="uploaded-files-list">
          <div v-for="(file, idx) in uploadedFiles" :key="file.uid || file.name" class="file-card" @click="handlePreviewFile(file)">
            <el-icon class="file-icon"><PictureFilled /></el-icon>
            <span class="file-name">{{ file.name }}</span>
            <el-button class="file-remove-btn" type="danger" link @click.stop="removeUploadedFile(idx)">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>
        <div class="input-wrapper">
          <el-input
            v-model="input"
            type="textarea"
            :rows="1"
            :autosize="{ minRows: 1, maxRows: 4 }"
            placeholder="输入消息，按 Enter 发送，Shift + Enter 换行..."
            @keydown.enter.exact.prevent="handleEnter"
            class="chat-input"
          />
          <div class="button-group">
            <el-upload
              ref="uploadRef"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleFileChange"
              :before-remove="handleBeforeRemove"
              :accept="'.png,.jpg,.jpeg,.bmp,.gif,.tiff,.webp,.pdf,.xlsx,.xls'"
              class="upload-btn"
            >
              <el-button 
                type="primary" 
                class="upload-button"
                :loading="uploading"
                circle
              >
                <el-icon><PictureFilled /></el-icon>
              </el-button>
            </el-upload>
            <el-button 
              type="primary" 
              :disabled="!canSend" 
              @click="send"
              class="send-btn"
              :loading="loading"
              circle
            >
              <el-icon><Position /></el-icon>
            </el-button>
          </div>
        </div>
        <div class="input-tips">AI 助手可能会出错，请验证重要信息</div>
      </div>
      <el-dialog v-model="previewVisible" width="60vw" :title="previewFile?.name || '文件预览'">
        <div v-if="isImagePreview">
          <img :src="previewUrl" class="preview-image" />
        </div>
        <div v-else-if="isPdfPreview">
          <iframe :src="previewUrl" class="preview-pdf"></iframe>
        </div>
        <div v-else>暂不支持预览该文件类型</div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, computed } from 'vue'
import { User, Service, Position, PictureFilled, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { sendChatMessage } from '../api/chat'
import type { UploadFile, UploadInstance } from 'element-plus'
import { useCompanyStore } from '../store/companyStore'
import type { VoucherResponse, VoucherEntry } from '../api/chat'

interface ChatMessage {
  role: 'user' | 'ai'
  text: string
  vouchers?: VoucherResponseLocal[]
  image?: string
  imageType?: string
  fileName?: string
  fileRaw?: File | undefined
}

// 本地扩展类型用于渲染（带orderNo）
interface VoucherEntryLocal extends VoucherEntry {
  orderNo: string;
}
interface VoucherResponseLocal extends Omit<VoucherResponse, 'entries'> {
  entries: VoucherEntryLocal[];
}

const input = ref('')
const messages = ref<ChatMessage[]>([])
const loading = ref(false)
const uploading = ref(false)
const messagesRef = ref<HTMLElement>()
const uploadRef = ref<UploadInstance>()
const uploadedFiles = ref<{ name: string; raw: File; uid?: string }[]>([])
const previewVisible = ref(false)
const previewFile = ref<{ name: string; raw: File; uid?: string } | null>(null)
const previewUrl = ref('')
const isImagePreview = computed(() => previewFile.value && previewFile.value.raw.type.startsWith('image/'))
const isPdfPreview = computed(() => previewFile.value && previewFile.value.raw.type === 'application/pdf')
const isExcelPreview = computed(() => previewFile.value && (
  previewFile.value.raw.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
  previewFile.value.raw.type === 'application/vnd.ms-excel'
))

const companyStore = useCompanyStore()

const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const handleEnter = () => {
  send()
}

const handleFileChange = (file: UploadFile) => {
  if (file.raw) {
    const allowedTypes = [
      'image/png', 'image/jpeg', 'image/jpg', 'image/bmp', 'image/gif', 'image/tiff', 'image/webp',
      'application/pdf',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
      'application/vnd.ms-excel' // .xls
    ];
    if (!allowedTypes.includes(file.raw.type)) {
      ElMessage.error('仅支持图片、PDF和Excel格式');
      return;
    }
    uploadedFiles.value.push({ name: file.raw.name, raw: file.raw, uid: String(file.uid) });
  }
}
const handleBeforeRemove = () => true;

const removeUploadedFile = (idx: number) => {
  uploadedFiles.value.splice(idx, 1);
}

const send = async () => {
  if (!canSend.value || loading.value) return;
  const userMessage = input.value.trim();
  input.value = '';

  let imageBase64 = '';
  let imageType = '';
  let fileName = '';
  let fileRaw: File | undefined = undefined;
  if (uploadedFiles.value.length > 0) {
    const file = uploadedFiles.value[0].raw;
    fileName = uploadedFiles.value[0].name;
    fileRaw = file;
    if (file.type.startsWith('image/')) {
      imageBase64 = await fileToBase64(file);
      imageType = 'image';
    } else if (file.type === 'application/pdf') {
      imageBase64 = URL.createObjectURL(file);
      imageType = 'pdf';
    }
  }

  messages.value.push({
    role: 'user',
    text: userMessage,
    image: imageBase64,
    imageType,
    fileName,
    fileRaw
  });
  await scrollToBottom();

  loading.value = true;
  try {
    const formData = new FormData();
    formData.append('message', userMessage);
    uploadedFiles.value.forEach(f => formData.append('fileList', f.raw));
    const response = await sendChatMessage(formData, companyStore.companyCode);
    let data = response && (response as any).data ? (response as any).data : response;
    // 兼容data为字符串数组（每个元素为JSON字符串）
    if (Array.isArray(data) && typeof data[0] === 'string') {
      data = data.map(str => {
        try {
          return JSON.parse(str);
        } catch {
          return [];
        }
      });
    }
    if (Array.isArray(data) && Array.isArray(data[0])) {
      for (const group of data) {
        const vouchers: VoucherResponseLocal[] = group.map((v: VoucherResponse) => ({
          voucherDate: v.voucherDate,
          summary: v.summary,
          businessNo: v.businessNo,
          entries: (v.entries || []).map((e: VoucherEntry, i: number) => ({
            ...e,
            orderNo: String(i + 1)
          }))
        }));
        messages.value.push({ role: 'ai', text: '', vouchers });
      }
    } else if (Array.isArray(data)) {
      const vouchers: VoucherResponseLocal[] = data.map((v: VoucherResponse) => ({
        voucherDate: v.voucherDate,
        summary: v.summary,
        businessNo: v.businessNo,
        entries: (v.entries || []).map((e: VoucherEntry, i: number) => ({
          ...e,
          orderNo: String(i + 1)
        }))
      }));
      messages.value.push({ role: 'ai', text: '', vouchers });
    } else if (data && data.entries) {
      const voucher: VoucherResponseLocal = {
        voucherDate: data.voucherDate,
        summary: data.summary,
        businessNo: data.businessNo,
        entries: (data.entries || []).map((e: VoucherEntry, i: number) => ({
          ...e,
          orderNo: String(i + 1)
        }))
      };
      messages.value.push({ role: 'ai', text: '', vouchers: [voucher] });
    } else {
      messages.value.push({ role: 'ai', text: JSON.stringify(data) });
    }
  } catch (error) {
    ElMessage.error('发送消息失败，请重试');
  } finally {
    loading.value = false;
    uploadedFiles.value = [];
    await scrollToBottom();
  }
};

function fileToBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}

const canSend = computed(() => {
  return input.value.trim().length > 0 || uploadedFiles.value.length > 0;
});

// 判断字符串是否为JSON
function isJson(str: string): boolean {
  if (!str) return false
  try {
    const obj = JSON.parse(str)
    return typeof obj === 'object' && obj !== null
  } catch {
    return false
  }
}
// 格式化JSON字符串
function formatJson(str: string): string {
  try {
    return JSON.stringify(JSON.parse(str), null, 2)
  } catch {
    return str
  }
}

const handlePreviewFile = (file: { name: string; raw: File; uid?: string }) => {
  previewFile.value = file
  if (file.raw.type.startsWith('image/')) {
    previewUrl.value = URL.createObjectURL(file.raw)
  } else if (file.raw.type === 'application/pdf') {
    previewUrl.value = URL.createObjectURL(file.raw)
  } else if (isExcelPreview.value) {
    // Excel 文件预览提示
    ElMessage.info('Excel文件暂不支持预览，请下载后查看')
    return
  } else {
    previewUrl.value = ''
  }
  previewVisible.value = true
}
</script>

<style scoped>
.chat-page {
  height: 100vh;
  width: 100vw;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
  height: 100%;
  position: relative;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  scroll-behavior: smooth;
}

.message {
  padding: 20px 0;
  animation: fadeIn 0.3s ease;
}

.message-content {
  display: flex;
  gap: 16px;
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.user .message-content {
  flex-direction: row-reverse;
  background: none !important;
  box-shadow: none !important;
  padding: 0 !important;
}

.ai .message-content {
  flex-direction: row;
  justify-content: flex-start;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user .avatar {
  background: #409EFF;
  color: white;
}

.ai .avatar {
  background: #67C23A;
  color: white;
}

.text {
  flex: 1;
  padding-top: 4px;
  max-width: 80%;
  background: none !important;
  padding: 0 !important;
}

.user .text {
  text-align: right;
}

.ai .text {
  text-align: left;
}

.role-name {
  font-weight: 600;
  margin-bottom: 4px;
  color: #303133;
}

.content {
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  padding: 12px 16px;
  border-radius: 12px;
  background: none !important;
  padding: 0 !important;
}

.user .content {
  color: #fff;
  background: none !important;
  padding: 0 !important;
}

.ai .content {
  color: #606266;
  background: #f0f9eb;
}

.welcome-message {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.welcome-message h2 {
  font-size: 2rem;
  font-weight: 600;
  margin: 0 0 12px;
  color: #303133;
}

.welcome-message p {
  font-size: 1.1rem;
  margin: 0;
}

.input-area {
  border-top: 1px solid #ebeef5;
  padding: 20px;
  background: #fff;
}

.input-wrapper {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  gap: 12px;
  position: relative;
}

.button-group {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

.chat-input {
  flex: 1;
}

.chat-input :deep(.el-textarea__inner) {
  padding: 12px 16px;
  border-radius: 12px;
  resize: none;
  font-size: 1rem;
  line-height: 1.5;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
}

.chat-input :deep(.el-textarea__inner:focus) {
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
  border-color: #409EFF;
}

.send-btn {
  width: 48px;
  height: 48px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.send-btn .el-icon {
  font-size: 20px;
}

.input-tips {
  text-align: center;
  color: #909399;
  font-size: 0.9rem;
  margin-top: 12px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .message-content {
    gap: 12px;
  }
  
  .avatar {
    width: 32px;
    height: 32px;
  }
  
  .welcome-message h2 {
    font-size: 1.6rem;
  }
  
  .welcome-message p {
    font-size: 1rem;
  }
  
  .input-area {
    padding: 16px;
  }
  
  .send-btn {
    width: 40px;
    height: 40px;
  }
  
  .send-btn .el-icon {
    font-size: 18px;
  }
}

.voucher-container {
  margin-bottom: 20px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.voucher-container:last-child {
  margin-bottom: 0;
}

.voucher-title {
  font-size: 16px;
  font-weight: 600;
  color: #1746a2;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e3eaff;
}

.voucher-table {
  margin-top: 8px;
  border-collapse: collapse;
  width: 100%;
}

.voucher-table th, .voucher-table td {
  border: 1px solid #b6c6e3;
  padding: 8px 12px;
  text-align: center;
  font-size: 14px;
}

.voucher-table th {
  background: #e3eaff;
  color: #1746a2;
  font-weight: 600;
}

.voucher-table td {
  background: #fff;
}

.voucher-table tr:hover td {
  background: #f0f4ff;
}

.user-message-bubble {
  background: #f8faf0;
  color: #222e3a;
  border-radius: 18px;
  padding: 18px 28px;
  font-family: inherit;
  font-size: 18px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
  text-align: left;
}

.upload-btn .upload-button {
  width: 48px;
  height: 48px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}
.upload-btn .el-icon {
  font-size: 22px;
}
.uploaded-files-list {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}
.file-card {
  display: flex;
  align-items: center;
  background: #f8faf0;
  border-radius: 12px;
  padding: 6px 16px 6px 8px;
  font-size: 15px;
  color: #222e3a;
  position: relative;
  min-width: 120px;
  box-shadow: 0 1px 4px rgba(80,120,200,0.04);
  cursor: pointer;
}
.file-card:hover {
  background: #f0f2f5;
}
.file-icon {
  font-size: 22px;
  color: #409EFF;
  margin-right: 8px;
}
.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.file-remove-btn {
  position: absolute;
  top: 2px;
  right: 2px;
  font-size: 16px;
  color: #f56c6c;
}

.chat-image {
  max-width: 220px;
  max-height: 220px;
  border-radius: 10px;
  margin-bottom: 6px;
  display: block;
}

.chat-pdf {
  width: 220px;
  height: 220px;
  border: none;
  margin-bottom: 6px;
  display: block;
  border-radius: 10px;
}

.preview-image {
  max-width: 100%;
  max-height: 60vh;
  display: block;
  margin: 0 auto;
}
.preview-pdf {
  width: 100%;
  height: 60vh;
  border: none;
}

.chat-file-badge {
  display: inline-flex;
  align-items: center;
  background: #f8faf0;
  border-radius: 8px;
  padding: 4px 12px 4px 6px;
  margin-bottom: 8px;
  margin-right: 8px;
  cursor: pointer;
  transition: background 0.2s;
}
.chat-file-badge:hover {
  background: #e3eaff;
}
.chat-file-icon {
  font-size: 20px;
  color: #409EFF;
  margin-right: 6px;
}

/* 添加 Excel 文件图标样式 */
.chat-file-badge[data-type="excel"] .chat-file-icon {
  color: #217346; /* Excel 绿色 */
}

.chat-file-name {
  font-size: 15px;
  color: #222e3a;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.voucher-date {
  font-size: 14px;
  color: #2563eb;
  margin-bottom: 8px;
  font-weight: 500;
}

.voucher-table th:first-child, .voucher-table td:first-child {
  background: #f0f9eb;
  color: #409EFF;
  font-weight: 600;
}
</style> 

