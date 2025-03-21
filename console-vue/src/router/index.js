import { createRouter, createWebHistory } from 'vue-router'
import { message } from 'ant-design-vue'
import Login from '../views/login'
import TicketSearch from '../views/ticket-serach'
import Userinfo from '../views/user-info'
import Passenger from '../views/passenger'
import MyTicket from '../views/my-ticket'
import Order from '../views/order'
import AddPassanger from '../views/add-passenger'
import CheckOrder from '../views/check-order'
import BuyTicket from '../views/buy-ticket'
import AliPay from '../views/ali-pay'
import TikectList from '../views/order-list'
import PersonalTicket from '../views/personalTicket'
import PaySuccess from '../views/pay-success'
// admin
import AdminTrainList from '../views/admin-train-list'
import AdminOrderSerach from '../views/admin-order-search'
import AdminUser from '../views/admin-user'
import AdminFinance from '../views/admin-finance'
import Cookies from 'js-cookie'
import { mapGetters } from 'vuex';

// 路由
const routes = [
  {
    path: '/',
    name: 'index',
    meta: { requiresAuth: false },
    redirect: (to) => {
      // 该函数接收目标路由作为参数
      // 相对位置不以`/`开头
      // 或 { path: 'profile'}
      return '/ticketSearch'
    }
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    label: '车票查询',
    path: '/ticketSearch',
    name: 'ticketSearch',
    component: TicketSearch,
    icon: 'icon-chaxun',
    meta: { requiresAuth: false }
  },
  {
    label: '用户信息',
    path: '/userInfo',
    name: 'userInfo',
    component: Userinfo,
    icon: 'icon-ic_account',
    meta: { requiresAuth: true }
  },
  {
    label: '人员信息',
    path: '/passenger',
    name: 'passenger',
    component: Passenger,
    icon: 'icon-renyuanfenbu',
    meta: { requiresAuth: true }
  },
  {
    label: '我的车票',
    path: '/myTicket',
    name: 'myTicket',
    component: MyTicket,
    icon: 'icon-dingdan',
    meta: { requiresAuth: true }
  },
  {
    label: '我的订单',
    path: '/order',
    name: 'order',
    component: Order,
    icon: 'icon-dingdan',
    meta: { requiresAuth: true }
  },
  {
    label: '添加乘车人',
    path: '/addPassenger',
    name: 'addPassenger',
    component: AddPassanger,
    icon: 'icon-icon-adduser',
    meta: { requiresAuth: true }
  },
  {
    label: '结算',
    path: '/checkOrder',
    name: 'checkOrder',
    component: CheckOrder,
    icon: 'icon-goumai',
    meta: { requiresAuth: true }
  },
  {
    label: '购买车票',
    path: '/buyTicket',
    name: 'buyTicket',
    component: BuyTicket,
    icon: 'icon-goumai',
    meta: { requiresAuth: true }
  },
  {
    label: '支付宝支付',
    path: '/aliPay',
    name: 'aliPay',
    component: AliPay,
    icon: 'icon-zhifubao0',
    meta: { requiresAuth: true }
  },
  {
    label: '订单列表',
    path: '/ticketList',
    name: 'ticketList',
    component: TikectList,
    icon: 'icon-zhifubao0',
    meta: { requiresAuth: true }
  },
  {
    label: '本人车票',
    path: '/personalTicket',
    name: 'personalTicket',
    component: PersonalTicket,
    icon: 'icon-dingdan',
    meta: { requiresAuth: false }
  },
  {
    label: '支付成功',
    path: '/paySuccess',
    name: 'paySuccess',
    component: PaySuccess,
    icon: 'icon-zhifuchenggong',
    meta: { requiresAuth: false }
  },
  {
    label: '车次查询',
    path: '/adminTrainList',
    name: 'adminTrainList',
    component: AdminTrainList,
    icon: 'icon-zhifuchenggong',
    meta: { requiresAuth: false }
  },
  {
    label: '用户管理',
    path: '/adminUser',
    name: 'adminUser',
    component: AdminUser,
    icon: 'icon-icon-adduser',
    meta: { requiresAuth: false }
  },
  {
    label: '订单管理',
    path: '/adminOrderSerach',
    name: 'adminOrderSerach',
    component: AdminOrderSerach,
    icon: 'icon-dingdan',
    meta: { requiresAuth: false }
  },
  {
    label: '财务管理',
    path: '/adminFinance',
    name: 'adminFinance',
    component: AdminFinance,
    icon: 'icon-zhifuchenggong',
    meta: { requiresAuth: false }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})


/**
 * 路由全局前置守卫，用于在每次路由跳转前执行一些逻辑。
 * 该函数会检查目标路由是否需要认证，以及用户是否已登录。
 * 如果用户未登录且目标路由需要认证，则重定向到登录页面。
 *
 * @param {Object} to - 目标路由对象，包含目标路由的信息。
 * @param {Object} from - 来源路由对象，包含来源路由的信息。
 * @returns {Object|undefined} - 如果用户未登录且目标路由需要认证，则返回一个重定向对象，否则返回undefined。
 */
router.beforeEach(async (to, from) => {
  const isAuthenticated = localStorage.getItem('isAuthenticated');
  // 检查目标路由是否需要认证，并且目标路由不是登录页面
  if (
    to.meta?.requiresAuth &&
    to.name !== 'login' &&
    // 检查用户是否未登录（即没有username或token）
    (!Cookies.get('username') || !Cookies.get('token')) &&
    !isAuthenticated
  ) {
    // 显示错误消息，提示用户未登录或登录已过期
    message.error('用户未登录或已过期！')
    // 重定向到登录页面
    return {
      name: 'login'
    }
  }else {
    
  }
})

export { routes }

export default router
