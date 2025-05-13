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
                {{ msg.text }}
              </div>
            </div>
          </div>
        </div>
        <div v-if="!messages.length" class="welcome-message">
          <h2>欢迎使用 AI 助手</h2>
          <p>开始对话吧，我会尽力帮助你</p>
        </div>
      </div>
      
      <div class="input-area">
        <div v-if="uploadedFiles.length" class="uploaded-files">
          <div class="uploaded-files-header">
            <span>已上传文件</span>
            <el-button type="primary" link @click="clearFiles">
              <el-icon><Delete /></el-icon>
              清空
            </el-button>
          </div>
          <div class="uploaded-files-list">
            <div v-for="(file, idx) in uploadedFiles" :key="idx" class="file-card">
              <div class="file-info">
                <el-icon class="file-icon"><Document /></el-icon>
                <div class="file-details">
                  <span class="file-name">{{ file.name }}</span>
                  <span class="file-size">{{ file.size }}</span>
                </div>
              </div>
              <div class="file-actions">
                <el-button 
                  type="primary" 
                  link 
                  @click="handlePreviewFile(file)"
                  :disabled="!isPreviewable(file.type)"
                >
                  <el-icon><View /></el-icon>
                </el-button>
                <el-button 
                  type="danger" 
                  link 
                  @click="removeUploadedFile(idx)"
                >
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
            </div>
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
              multiple
              class="upload-btn"
            >
              <el-button 
                type="primary" 
                class="upload-button"
                :loading="uploading"
              >
                <el-icon><Plus /></el-icon>
              </el-button>
            </el-upload>
            <el-button 
              type="primary" 
              :disabled="!canSend" 
              @click="send"
              class="send-btn"
              :loading="loading"
            >
              <el-icon><Position /></el-icon>
            </el-button>
          </div>
        </div>
        <div class="input-tips">AI 助手可能会出错，请验证重要信息</div>
      </div>

      <el-dialog
        v-model="previewVisible"
        :title="currentPreviewFile?.name"
        width="80%"
        class="preview-dialog"
      >
        <div class="preview-content">
          <img v-if="isImage" :src="previewUrl" class="preview-image" />
          <iframe v-else-if="isPdf" :src="previewUrl" class="preview-pdf"></iframe>
          <div v-else class="preview-unsupported">
            该文件类型暂不支持预览
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, computed, onUnmounted } from 'vue'
import { User, Service, Position, Plus, Document, View, Delete, Close } from '@element-plus/icons-vue'
import type { UploadFile, UploadInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import { sendChatMessage } from '../api/chat'
import type { ChatResponse } from '../api/chat'

interface FileInfo {
  name: string
  size: string
  url: string
  type: string
  raw: File
}

interface ChatMessage {
  role: 'user' | 'ai'
  text: string
  files?: FileInfo[]
}

const input = ref('')
const messages = ref<ChatMessage[]>([])
const loading = ref(false)
const uploading = ref(false)
const messagesRef = ref<HTMLElement>()
const uploadRef = ref<UploadInstance>()
const uploadedFiles = ref<FileInfo[]>([])

// 文件预览相关
const previewVisible = ref(false)
const currentPreviewFile = ref<FileInfo | null>(null)
const previewUrl = computed(() => currentPreviewFile.value?.url || '')

const isImage = computed(() => {
  if (!currentPreviewFile.value) return false
  return currentPreviewFile.value.type.startsWith('image/')
})

const isPdf = computed(() => {
  if (!currentPreviewFile.value) return false
  return currentPreviewFile.value.type === 'application/pdf'
})

const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const handleEnter = (e: KeyboardEvent) => {
  send()
}

const send = async () => {
  if (!canSend.value || loading.value) return
  
  const userMessage = input.value.trim()
  input.value = ''
  
  // 创建消息对象，包含文本和文件
  const message: ChatMessage = {
    role: 'user',
    text: userMessage,
    files: uploadedFiles.value
  }
  
  // 添加用户消息
  messages.value.push(message)
  await scrollToBottom()
  
  loading.value = true
  
  try {
    // 调用后端 API
    const response = await sendChatMessage(
      userMessage, 
      uploadedFiles.value.map(f => f.raw).filter((f): f is File => f !== undefined)
    )
    
    // 添加 AI 回复
    messages.value.push({
      role: 'ai',
      text: response.message
    })
  } catch (error) {
    console.error('Error sending message:', error)
    ElMessage.error('发送消息失败，请重试')
  } finally {
    loading.value = false
    // 清空已上传文件
    clearFiles()
    await scrollToBottom()
  }
}

// 处理文件选择
const handleFileChange = (file: UploadFile) => {
  if (file.raw) {
    // 检查文件大小（限制为10MB）
    if (file.raw.size > 10 * 1024 * 1024) {
      ElMessage.error('文件大小不能超过10MB')
      return
    }
    
    const fileUrl = URL.createObjectURL(file.raw)
    uploadedFiles.value.push({
      name: file.raw.name,
      size: formatFileSize(file.raw.size),
      url: fileUrl,
      type: file.raw.type,
      raw: file.raw
    })
  }
}

// 处理文件移除
const handleBeforeRemove = (file: UploadFile) => {
  return true
}

// 预览文件
const handlePreviewFile = (file: FileInfo) => {
  currentPreviewFile.value = file
  previewVisible.value = true
}

// 移除已上传文件
const removeUploadedFile = (index: number) => {
  URL.revokeObjectURL(uploadedFiles.value[index].url)
  uploadedFiles.value.splice(index, 1)
}

// 清空所有文件
const clearFiles = () => {
  uploadedFiles.value.forEach(file => {
    URL.revokeObjectURL(file.url)
  })
  uploadedFiles.value = []
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 判断是否可以发送消息
const canSend = computed(() => {
  return input.value.trim() || uploadedFiles.value.length > 0
})

// 移除消息中的文件
const removeMessageFile = (message: ChatMessage, fileIndex: number) => {
  if (message.files) {
    message.files.splice(fileIndex, 1)
    if (message.files.length === 0) {
      delete message.files
    }
  }
}

// 判断文件是否可预览
const isPreviewable = (type: string) => {
  return type.startsWith('image/') || type === 'application/pdf'
}

onMounted(() => {
  scrollToBottom()
})

// 组件卸载时清理
onUnmounted(() => {
  clearFiles()
})
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
}

.user .content {
  color: #fff;
  background: #409EFF;
  display: inline-block;
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

.upload-btn {
  display: inline-block;
}

.upload-button {
  width: 48px;
  height: 48px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.upload-button .el-icon {
  font-size: 20px;
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

.uploaded-files {
  max-width: 800px;
  margin: 0 auto 12px;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 12px;
}

.uploaded-files-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 0.9rem;
  color: #606266;
}

.uploaded-files-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.file-card:hover {
  background: #f0f2f5;
  border-color: #dcdfe6;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.file-icon {
  font-size: 24px;
  color: #909399;
  flex-shrink: 0;
}

.file-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.file-name {
  font-size: 0.95rem;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  font-size: 0.8rem;
  color: #909399;
}

.file-actions {
  display: flex;
  gap: 8px;
  margin-left: 12px;
  flex-shrink: 0;
}

.file-actions .el-button {
  padding: 4px;
}

.file-actions .el-button.is-disabled {
  opacity: 0.5;
}

.preview-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.preview-content {
  height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.preview-pdf {
  width: 100%;
  height: 100%;
  border: none;
}

.preview-unsupported {
  color: #909399;
  font-size: 1.1rem;
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
  
  .upload-button,
  .send-btn {
    width: 40px;
    height: 40px;
  }
  
  .upload-button .el-icon,
  .send-btn .el-icon {
    font-size: 18px;
  }
  
  .file-card {
    padding: 8px;
  }
  
  .file-icon {
    font-size: 20px;
  }
  
  .file-name {
    font-size: 0.9rem;
  }
  
  .file-size {
    font-size: 0.75rem;
  }
  
  .file-actions {
    gap: 4px;
  }
  
  .file-actions .el-button {
    padding: 2px;
  }
  
  .uploaded-files {
    padding: 0 8px;
  }
}
</style> 

