<template>
  <div :style="{
    position: 'relative',
    minWidth: !state.collapse ? '200px' : '80px',
    height: 'calc(100vh - 64px)'
  }">
    <!-- 侧边栏 -->
    <Sider :collapsed="state.collapse" collapsible :trigger="null" @collapse="handleCollapse">
      <Menu v-model:selectedKeys="selectedKeys" mode="inline" v-for="item in menuItems">
        <Item :key=item>
          <RouterLink :to=item.link>{{ item.name }}</RouterLink>
        </Item>
      </Menu>
    </Sider>
    <!-- 侧边栏底部 -->
    <div class="sider-footer" :style="{
      width: !state.collapse ? '200px' : '80px',
    }">
      <Tooltip title="折起">
        <div :class="state.collapse && 'collaspe-icon'" @click="() => (state.collapse = !state.collapse)"
          class="icon-wrapper">
          <IconFont type="icon-zhedie" />
        </div>
      </Tooltip>
      <Divider v-if="!state.collapse" type="vertical" />
      <Tooltip title="退出登录">
        <div @click="logout" v-if="!state.collapse" class="icon-wrapper">
          <IconFont type="icon-tuichudenglu" />
        </div>
      </Tooltip>
    </div>
  </div>
</template>

<script setup>
import { defineProps, reactive, computed, ref, onMounted } from 'vue'
import IconFont from '@/components/icon-font'
import { Layout, Menu, Divider, message, Tooltip } from 'ant-design-vue'
import { fetchLogout } from '@/service'
import { RouterLink } from 'vue-router'
import Cookie from 'js-cookie'
import { useStore } from 'vuex';
const collapsed = ref(false);
const selectedKeys = ref(['1']);

const { SubMenu, Item } = Menu
const { Sider } = Layout
const props = defineProps({
  isLogin: Boolean
})
const state = reactive({
  collapse: false
})
const store = useStore();
// 获取登录状态
const isAuthenticated = computed(() => store.getters.isAuthenticated);
const isAdmin = computed(() => store.getters.isAdmin);


const userSider = [
  {
    name: '车票查询',
    link: '/ticketSearch',
    key: 'ticketSearch'
  },
  {
    name: '个人信息',
    link: '/userInfo',
    key: 'userInfo'
  },
  {
    name: '车票订单',
    link: '/ticketList',
    key: 'ticketList'
  },
  {
    name: '乘车人管理',
    link: '/passenger',
    key: 'passenger'
  },
  // {
  //   name: '本人车票',
  //   link: '/personalTicket',
  //   key: 'personalTicket'
  // }
]
const adminSider = [
  {
    name: '车次查询',
    link: '/adminTrainList',
    key: 'adminTrainList'
  },
  {
    name: '用户管理',
    link: '/adminUser',
    key: 'adminUser'
  },
  {
    name: '订单查询',
    link: '/adminOrderSerach',
    key: 'adminOrderSerach'
  },
  {
    name: '财务统计',
    link: '/adminFinance',
    key: 'adminFinance'
  }
]

const menuItems = computed(() => {
  if (isAdmin.value) {
    return adminSider
  } else {
    return userSider
  }
})
// 侧边栏折叠
const handleCollapse = () => {
  state.collapse = !state.collapse
}
const logout = () => {
  const token = Cookie.get('token')
  fetchLogout({ accessToken: token }).then((res) => {
    if (res.success) {
      message.success('退出成功')
      location.href = 'login'
      Cookie.remove('token')
      Cookie.remove('username')
    }
  })
}
const created = (store) => {
  // 在页面加载时读取sessionStorage里的状态信息
  if (sessionStorage.getItem('store')) {
    store.replaceState(
      Object.assign(
        {},
        JSON.parse(sessionStorage.getItem('store'))
      )
    )
  }
}

const debounce = (func, wait) => {
  let timeout;
  return function (...args) {
    const context = this;
    clearTimeout(timeout);
    timeout = setTimeout(() => {
      func.apply(context, args);
    }, wait);
  };
}

onMounted(() => {
  debounce(created(store),500)
  // 在页面加载时读取sessionStorage里的状态信息
})

</script>

<style lang="scss" scoped>
.ant-layout-sider {
  position: fixed;
  left: 0;
  top: 64px;
  height: calc(100vh - 64px);
  background-color: #e6e9ee;
  transition: none;
}

.sider-footer {
  position: fixed;
  z-index: 100;
  bottom: 0;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: space-around;
  padding: 18px;
  align-items: center;

  .icon-wrapper {
    cursor: pointer;
  }

  .collaspe-icon {
    transform: rotate(180deg);
  }
}

::v-deep {
  .ant-menu {
    background-color: #e6e9ee;
    color: #686868;
  }

  .ant-menu-sub.ant-menu-inline {
    background: #e6e9ee;
  }

  .ant-menu:not(.ant-menu-horizontal) .ant-menu-item-selected {
    background: #f2f3f7;
  }

  .ant-menu-submenu-selected {
    color: #686868;
  }

  .ant-menu-item-selected {
    color: #686868;
  }

  .ant-menu-item-active {
    color: #686868;
  }

  .ant-menu-submenu-active {
    color: #686868;
  }

  .ant-menu-light .ant-menu-item:hover {
    color: #686868;
  }

  .ant-menu-submenu:hover {
    color: #686868;
  }

  .ant-menu-inline .ant-menu-item::after {
    border-right: none;
  }

  .ant-menu-light .ant-menu-submenu-title:hover {
    color: #686868;
  }

  .ant-menu-submenu:hover>.ant-menu-submenu-title>.ant-menu-submenu-arrow {
    color: #686868;
  }

  .ant-menu-submenu-arrow {
    color: #686868;
  }

  .ant-menu-title-content {
    user-select: none;
  }

  .ant-menu-item-group-title {
    user-select: none;
  }
}
</style>
