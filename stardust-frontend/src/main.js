import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { ConfigProvider } from "vant";
// 引入全局主题样式
import "./assets/theme.css";

const app = createApp(App);
app.use(router);
app.use(ConfigProvider);
app.mount("#app");

// 阻止双击缩放
let lastTouchEnd = 0;
document.addEventListener('touchend', function (event) {
  const now = Date.now();
  if (now - lastTouchEnd <= 300) {
    event.preventDefault();
  }
  lastTouchEnd = now;
}, { passive: false });
// 阻止双指缩放
document.addEventListener('gesturestart', function (event) {
  event.preventDefault();
});
