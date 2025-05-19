import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  
  // 设置打包后部署的基础路径（根路径为 '/'，子路径如 '/myapp/'）
  base: '/',
  
  plugins: [vue()],

  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@components': path.resolve(__dirname, './src/components'),
      '@views': path.resolve(__dirname, './src/views'),
      '@utils': path.resolve(__dirname, './src/utils'),
    }
  },

  server: {
    port: 3001,             // 开发服务器端口
    open: true,             // 启动后自动打开浏览器
    cors: true,             // 是否开启跨域
    
    proxy: {
      '/api': 'http://localhost:8082'
    },
  },
  build: {
    outDir: 'dist',            // 输出目录
    sourcemap: false,          // 关闭 source map，加快打包
    minify: 'esbuild',         // 使用 esbuild 进行压缩（速度快）
    chunkSizeWarningLimit: 1000, // 警告门槛，默认 500KB
  },

  // css: {
  //   preprocessorOptions: {
  //     scss: {
  //       // 自动导入全局 SCSS（如果你有 src/styles/global.scss 文件）
  //       additionalData: `@import "@/styles/global.scss";`
  //     }
  //   }
  // }
})
