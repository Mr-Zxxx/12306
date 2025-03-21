<script setup>
import {
  Form,
  FormItem,
  Input,
  InputPassword,
  Button,
  message,
  Select,
  Modal,
  Switch,
  Radio
} from 'ant-design-vue'
import { reactive, ref, unref } from 'vue'
import { fetchLogin, fetchRegister } from '../../service'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import Cookies from 'js-cookie'
import { useStore } from 'vuex';
const useForm = Form.useForm

const userOrAdmin = ref(1);
const formState = reactive({
  usernameOrMailOrPhone: '',
  password: '',
  code: ''
})
const state = reactive({
  open: false
})
const rulesRef = reactive({
  usernameOrMailOrPhone: [
    {
      required: true,
      message: '请输入用户名或者邮件或者手机号'
    }
  ],
  password: [
    {
      required: true,
      message: '请输入密码'
    }
  ]
})
const { validate, validateInfos } = useForm(formState, rulesRef)

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  realName: '',
  idType: 0,
  idCard: '',
  phone: '',
  mail: ''
})
// 注册表单验证规则
const registerRules = reactive({
  username: [
    {
      required: true,
      message: '请输入用户名'
    }
  ],
  password: [
    {
      required: true,
      message: '请输入密码'
    }
  ],
  realName: [
    {
      required: true,
      message: '请输入姓名'
    }
  ],
  idType: [
    {
      required: true,
      message: '请选择证件类型'
    }
  ],
  idCard: [
    {
      required: true,
      message: '请输入身份证号码',
    },
    {
      pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/,
      message: '身份证号码格式不正确',
    }
  ],
  phone: [
    {
      required: true,
      message: '请输入手机号',
    },
    {
      pattern: /^1(3|5|6|7|8)[0-9]{9}$/,
      message: '格式不正确',
    }
  ],
  mail: [
    {
      required: true,
      message: '请输入邮箱地址',
    },
    {
      pattern: /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$/,
      message: '邮箱格式不正确',
    }
  ]
})

const { validate: registerValidate, validateInfos: registerValidateInfos } =
  useForm(registerForm, registerRules)

let currentAction = ref('login')

const router = useRouter()
const store = useStore()
// 使用store.dispatch方法调用loginAction，传入用户信息
const handleLogin = async (user) => {
  await store.dispatch('login', user);
}
/**
 * 处理登录逻辑的函数
**/
const handleFinish = () => {
  // 检查当前域名是否包含'12306'
  if (location.host.indexOf('12306') !== -1) {
    // 验证成功后打开某个状态
    validate()
      .then(() => {
        state.open = true
      })
      .catch((err) => console.log(err))
    return
  }
  // 非特定站点，执行登录逻辑
  validate().then(() => {
    // 用户身份信息
    const user = {
      name: formState.usernameOrMailOrPhone,
      role: userOrAdmin.value
    };
    handleLogin(user)
    sessionStorage.setItem('store', JSON.stringify(store.state))
    fetchLogin({
      ...formState
    }).then((res) => {
      if (res.success) {
        // 登录成功，设置Cookie并跳转页面
        Cookies.set('token', res.data?.accessToken)
        Cookies.set('username', res.data?.username)
        Cookies.set('userId', res.data?.userId)
        router.push('/ticketSearch')
      } else {
        // 登录失败，显示错误信息
        message.error(res.message)
      }
    })
  })
}

const registerSubmit = () => {
  if (location.host.indexOf('12306') !== -1) {
    message.info('请获取验证码登录')
    currentAction.value = 'login'
    return
  }
  registerValidate()
    .then(() => {
      fetchRegister(registerForm).then((res) => {
        if (res.success) {
          message.success('注册成功')
          currentAction.value = 'login'
          formState.usernameOrMailOrPhone = res.data?.username
          formState.password = ''
        } else {
          message.error(res.message)
        }
      })
    })
    .catch((err) => console.log(err))
}


</script>
<template>
  <div class="login-wrapper">
    <div class="login-reg-panel">
      <div class="login-info-box">
        <h2>已有账号？</h2>
        <h3>欢迎登录账号！</h3>
        <button @click="() => (currentAction = 'login')">登录</button>
      </div>
      <div class="register-info-box">
        <h2>没有账号？</h2>
        <h3>欢迎注册账号！</h3>
        <button @click="() => (currentAction = 'register')">注册</button>
      </div>
      <div class="white-panel" :class="{ 'white-panel-left': currentAction === 'register' }">
        <!-- 登录表单 -->
        <div class="login-show" v-if="currentAction === 'login'">
          <h1 class="title">登录</h1>
          <Form name="basic" autocomplete="off">
            <FormItem v-bind="validateInfos.usernameOrMailOrPhone">
              <Input size="large" v-model:value="formState.usernameOrMailOrPhone" placeholder="用户名">
              <template #prefix>
                <UserOutlined style="color: rgba(0, 0, 0, 0.25)" />
              </template></Input>
            </FormItem>
            <FormItem v-bind="validateInfos.password">
              <InputPassword size="large" v-model:value="formState.password" placeholder="密码">
                <template #prefix>
                  <LockOutlined style="color: rgba(0, 0, 0, 0.25)" />
                </template>
              </InputPassword>
            </FormItem>
            <FormItem>
              <div class="action-btn">
                <a href="">忘记密码？</a>
                <!-- 登录按钮 -->
                <Button type="primary" :style="{ backgroundColor: '#202020', border: 'none' }"
                  @click="handleFinish">登录</Button>
              </div>
              <br />
              <!-- 用户登录/管理员登录 -->
              <div>
                <a-radio-group v-model:value="userOrAdmin">
                  <a-radio :value=1>用户登录</a-radio>
                  <a-radio :value=2>管理员登录</a-radio>
                </a-radio-group>
              </div>
            </FormItem>
          </Form>
        </div>
        <!-- 注册表单 -->
        <div class="register-show" v-else>
          <h1 class="title">注册</h1>
          <Form name="basic" autocomplete="off" :label-col="{ span: 6 }">
            <FormItem label="用户名" v-bind="registerValidateInfos.username">
              <Input v-model:value="registerForm.username" placeholder="请输入用户名">
              </Input>
            </FormItem>
            <FormItem label="密码" v-bind="registerValidateInfos.password">
              <InputPassword v-model:value="registerForm.password" placeholder="密码">
              </InputPassword>
            </FormItem>

            <FormItem label="姓名" v-bind="registerValidateInfos.realName">
              <Input v-model:value="registerForm.realName" placeholder="请输入姓名">
              </Input>
            </FormItem>
            <FormItem label="证件类型" v-bind="registerValidateInfos.idType">
              <Select
                :options="[{ value: 0, label: '中国居民身份证' }, { value: 1, label: '港澳通行证' }, { value: 2, label: '护照' }]"
                v-model:value="registerForm.idType" placeholder="请选择证件类型"></Select>
            </FormItem>
            <FormItem label="证件号码" v-bind="registerValidateInfos.idCard">
              <Input v-model:value="registerForm.idCard" placeholder="请输入证件号码">
              </Input>
            </FormItem>
            <FormItem label="手机号码" v-bind="registerValidateInfos.phone">
              <Input v-model:value="registerForm.phone" placeholder="请输入手机号码">
              </Input>
            </FormItem>
            <FormItem label="邮件" v-bind="registerValidateInfos.mail">
              <!-- 邮箱输入框 -->
              <Input v-model:value="registerForm.mail" type="email" placeholder="请输入邮箱账号">
              </Input>
            </FormItem>
            <FormItem>
              <div class="action-btn">
                <a></a>
                <Button type="primary" @click="registerSubmit"
                  :style="{ backgroundColor: '#202020', border: 'none' }">注册</Button>
              </div>
            </FormItem>
          </Form>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.login-wrapper {
  width: 100%;
  height: 100%;
  // background-color: #ffffff44;
  //背景图片
  background-clip: border-box;
  // background-image: url('~@/assets/background.jpg');
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.7), rgba(255, 255, 255, 0.7)),
    /* 黑色半透明蒙版 */
    url("~@/assets/background.jpg");
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;

  .login-reg-panel {
    border-radius: 10px;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    width: 40%;
    right: 0;
    margin: auto;
    min-width: 800px;
    height: 600px;
    background-color: #252b30d3;

    .white-panel {
      border-radius: 10px;
      background-color: rgba(255, 255, 255, 1);
      height: 600px;
      position: absolute;
      width: 50%;
      right: calc(50% - 0px);
      transition: 0.3s ease-in-out;
      z-index: 0;
      box-sizing: border-box;
      /* 边框颜色（高级灰） */
      border: 1px solid rgba(30, 30, 30, 0.9);
      /* 颜色稍微深一点的高级灰 */
      /* 边框样式 */
      box-shadow: inset 0 0 0 1px rgb(40, 40, 40);
      /* 内阴影增加层次感 */
      border-radius: 10px;

      /* 圆角边框 */
      .login-show,
      .register-show {
        height: 100%;
        display: flex;
        flex-direction: column;
        // justify-content: space-around;
        transition: 0.3s ease-in-out;
        color: #242424;
        text-align: left;
        padding: 30px;

        .title {
          font-size: 24px;
          font-weight: bolder;
          padding: 20px 0;
          color: #202020;
        }

        .action-btn {
          display: flex;
          width: 100%;

          a {
            display: block;
            line-height: 32px;
          }

          justify-content: space-between;
        }
      }
    }

    .white-panel-left {
      transition: 0.3s ease-in-out;
      right: calc(0px + 0px);
    }

    .login-info-box {
      display: flex;
      flex-direction: column;
      width: 30%;
      padding: 0 50px;
      top: 20%;
      left: 0;
      position: absolute;
      text-align: left;
      justify-content: center;
      font-family: 'Mukta', sans-serif;
      color: #b8b8b8;

      h2 {
        font-size: 24px;
        color: #b8b8b8;
        font-weight: bolder;
        font-weight: bolder;
        margin-bottom: 40px;
      }

      h3 {
        font-size: 20px;
        color: #b8b8b8;
        margin-bottom: 40px;
      }

      button {
        cursor: pointer;
        width: 100%;
        background-color: transparent;
        box-shadow: none;
        border: 1px solid #b8b8b8;
        border-radius: 2px;
        height: 25px;
      }
    }

    .register-info-box {
      width: 30%;
      padding: 0 50px;
      top: 20%;
      right: 0;
      position: absolute;
      text-align: left;
      font-family: 'Mukta', sans-serif;
      color: #b8b8b8;

      h2 {
        font-size: 24px;
        color: #b8b8b8;
        font-weight: bolder;
        font-weight: bolder;
        margin-bottom: 40px;
      }

      h3 {
        font-size: 20px;
        color: #b8b8b8;
        margin-bottom: 40px;
      }

      button {
        cursor: pointer;
        width: 100%;
        background-color: transparent;
        box-shadow: none;
        border: 1px solid #b8b8b8;
        border-radius: 2px;
        height: 25px;
      }
    }
  }
}

::v-deep {
  .ant-modal-header {
    background-color: #3b3b3b !important;
  }

  .ant-form-item-label {
    label {
      color: #202020;
    }
  }
}
</style>
