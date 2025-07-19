import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { VantResolver } from '@vant/auto-import-resolver';
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  server: {
    port: 7000,
    proxy: {
      '/api': {
        target: 'http://106.75.156.253:7070', // 后端服务地址
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, '')
      }
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  plugins: [vue(),
  AutoImport({
    resolvers: [VantResolver()],
  }),
  Components({
    resolvers: [VantResolver()],
  }),],
})
