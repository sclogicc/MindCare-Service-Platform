import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  build: {
    // 直接输出到 Spring Boot 静态资源目录。
    // 这样前端打包后，只启动后端就可以在 8080 访问整个系统。
    outDir: '../src/main/resources/static',
    emptyOutDir: true
  },
  server: {
    port: 5173,
    proxy: {
      '/login': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/appointments': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/counselors': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/schedules': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/consultationRecords': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/feedbacks': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/reports': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/upload': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
