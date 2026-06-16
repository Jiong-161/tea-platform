import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// 后台管理端 Vite 配置 — 端口 5174，区分于用户端 5173
export default defineConfig({
  plugins: [
    vue(),
    // ====== SPA history 模式刷新修复 ======
    // 原理同上：F5 刷新页面时，将浏览器请求改写为 /index.html，
    // 避免被代理误转发到后端导致 500
    {
      name: 'spa-history-fallback',
      configureServer(server) {
        server.middlewares.use((req, res, next) => {
          const accept = req.headers.accept || ''
          if (accept.includes('text/html') && !req.url.includes('.')) {
            req.url = '/index.html'
          }
          next()
        })
      }
    }
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5174,
    open: true,
    // 代理到后端网关，解决跨域
    proxy: {
      '/user':    { target: 'http://localhost:10010', changeOrigin: true },
      '/content': { target: 'http://localhost:10010', changeOrigin: true },
      '/exhibition': { target: 'http://localhost:10010', changeOrigin: true },
      '/product': { target: 'http://localhost:10010', changeOrigin: true },
      '/admin':   { target: 'http://localhost:10010', changeOrigin: true },
      '/home':    { target: 'http://localhost:10010', changeOrigin: true }
    }
  }
})