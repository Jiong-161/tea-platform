import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// Vite 配置文档：https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // ====== SPA history 模式刷新修复 ======
    // 核心问题：F5 刷新 /products 时，Vite 代理把 "/products" 前缀匹配到 "/product"，
    //          将浏览器页面请求转发到后端 → 后端无此路由 → 500 异常
    // 修复方案：中间件拦截浏览器页面请求（Accept: text/html），
    //          改写为 /index.html，交 Vue Router 处理，避免被代理误转发
    {
      name: 'spa-history-fallback',
      configureServer(server) {
        server.middlewares.use((req, res, next) => {
          const accept = req.headers.accept || ''
          // 浏览器直接访问（F5刷新 / 地址栏输入）→ Accept 包含 text/html
          // API 请求（axios/fetch）→ Accept 不含 text/html
          // 静态资源（.js / .css / .png …）→ 包含 '.' 跳过
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
      '@': resolve(__dirname, 'src')  // @ 指向 src 目录
    }
  },
  server: {
    port: 5173,            // 前端开发服务器端口
    open: true,            // 启动时自动打开浏览器

    // ====== 【临时】局域网访问配置（方案 A）======
    // 用途：开发阶段让手机/平板等局域网设备访问本机前端页面进行调试
    // 使用方式：其他设备浏览器打开 http://本机IP:5173/
    // 移除方式：删除 host 和 strictPort 两行即可恢复仅 localhost 访问
    // 注意：
    //   1. 仅限开发调试使用，不适合演示/生产环境
    //   2. 需确保 Windows 防火墙放行 5173 端口
    //   3. 手机/电脑需与开发机在同一局域网（同一 WiFi/路由器下）
    //   4. 后端服务（Gateway + 微服务 + Nacos + Redis）必须已启动
    // 相关方案对比见 UI界面优化方案.md 或咨询开发文档
    host: '0.0.0.0',       // 监听所有网卡，允许局域网设备访问
    strictPort: true,       // 端口被占用时不自动递增，避免端口不确定

    // 【关键】配置代理：将前端 API 请求转发到后端网关，解决跨域问题
    // 浏览器访问 localhost:5173 时，所有 /xxx 开头的 API 请求都会被转发到 localhost:10010
    //  注意：SPA 页面路由刷新不会走到这里（已被上方 spa-history-fallback 中间件拦截）
    proxy: {
      '/user': {
        target: 'http://localhost:10010',
        changeOrigin: true
      },
      '/content': {
        target: 'http://localhost:10010',
        changeOrigin: true
      },
      '/exhibition': {
        target: 'http://localhost:10010',
        changeOrigin: true
      },
      '/product': {
        target: 'http://localhost:10010',
        changeOrigin: true
      },
      '/home': {
        target: 'http://localhost:10010',
        changeOrigin: true
      },
      '/admin': {
        target: 'http://localhost:10010',
        changeOrigin: true
      }
    }
  }
})