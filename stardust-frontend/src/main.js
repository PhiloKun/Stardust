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
