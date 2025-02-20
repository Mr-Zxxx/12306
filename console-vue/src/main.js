import { createApp } from 'vue' //创建应用
import App from './App.vue'     //导入根组件
import router from './router'   //页面路由
import Antd from 'ant-design-vue'   //导入ui库
import 'ant-design-vue/dist/antd.less'
import './global.less'
import dayjs from 'dayjs'       //日期类
import 'dayjs/locale/zh-cn'
dayjs.locale('zh-cn')

const app = createApp(App)
app.use(router).use(Antd).mount('#app')
