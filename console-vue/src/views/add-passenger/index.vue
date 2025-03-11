<template>
  <Card>
    <Card :bordered="false">
      <Form :label-col="{ span: 10 }" :wrapper-col="{ span: 4 }">
        <TypographyTitle :level="5">基本信息</TypographyTitle>
        <FormItem label="证件类型" v-bind="validateInfos.idType">
          <Select  v-model:value="formData.idType">
            <SelectOption :value="0">身份证</SelectOption>
          </Select>
        </FormItem>
        <FormItem label="姓名" v-bind="validateInfos.realName">
          <Input  v-model:value="formData.realName"></Input>
        </FormItem>
        <FormItem  label="证件号码" v-bind="validateInfos.idCard">
          <Input  v-model:value="formData.idCard"></Input>
        </FormItem>
        <TypographyTitle :level="5">联系方式</TypographyTitle>
        <FormItem label="手机号" v-bind="validateInfos.phone">
          <Input v-model:value="formData.phone"></Input>
        </FormItem>
        <TypographyTitle :level="5">附加信息</TypographyTitle>
        <FormItem label="优惠(待)类型" v-bind="validateInfos.discountType">
          <Select  v-model:value="formData.discountType">
            <!-- <SelectOption :value="0">无优惠</SelectOption>
            <SelectOption :value="1">学生优惠</SelectOption> -->
            <SelectOption v-for="item in DISCOUNTS_TYPE" :value="item.value">
              {{ item.label }}</SelectOption
            >
          </Select>
        </FormItem>
        <Row align="center">
          <Space>
            <Button @click="() => router.push('/passenger')">取消</Button>
            <Button :loading="loading" type="primary" @click="onSubmit"
              >保存</Button
            ></Space
          >
        </Row>
      </Form>
    </Card>
  </Card>
</template>

<script setup>
import {
  Card,
  Alert,
  Form,
  TypographyTitle,
  FormItem,
  Select,
  Input,
  Button,
  Space,
  Row,
  SelectOption,
  message
} from 'ant-design-vue'

import { reactive, toRaw, ref, onMounted } from 'vue'
import {
  fetchAddPassenger,
  fetchPassengerList,
  fetchEditPassenger
} from '@/service'
import { DISCOUNTS_TYPE } from '@/constants'
import { useRouter, useRoute } from 'vue-router'
import Cookie from 'js-cookie'
const useForm = Form.useForm
const router = useRouter()
const { query } = useRoute()
const username = Cookie.get('username')

const disabled = useRoute().query?.type === 'edit'

let formData = ref({
  realName: '',
  idType: undefined,
  discountType: undefined,
  phone: '',
  idCard: ''
})

const loading = ref(false)

const rulesRef = reactive({
  username: [
    {
      required: true,
      message: '请完善信息'
    }
  ],
  realName: [
    {
      required: true,
      message: '请完善信息'
    }
  ],
  idType: [
    {
      required: true,
      message: '请完善信息'
    }
  ],
  discountType: [
    {
      required: true,
      message: '请完善信息'
    }
  ],
  phone: [
    {
      required: true,
      message: '格式不正确，请完善',
      pattern: /^1(3|5|6|7|8)[0-9]{9}$/
    }
  ]
})

// 表单校验
const { resetFields, validate, validateInfos } = useForm(formData, rulesRef)

// 初始化数据
/**
 * 在组件挂载时执行的函数。
 * 如果查询参数中的类型为 'edit'，则根据用户名获取乘客列表，
 * 并在列表中找到与查询参数中的 ID 匹配的用户信息，将其合并到表单数据中。
 */
onMounted(() => {
  // 检查查询参数中的类型是否为 'edit'
  query.type === 'edit' &&
    // 根据用户名获取乘客列表
    fetchPassengerList({ username }).then((res) => {
      // 如果返回的数据不为空
      if (res.data?.length) {
        // 在乘客列表中找到与查询参数中的 ID 匹配的用户信息
        const userInfo = res.data.find((item) => item.id == query.id)
        // 将找到的用户信息合并到表单数据中
        formData.value = { ...formData.value, ...userInfo }
      }
    })
})


/**
 * 处理表单提交的函数
 * 
 * 该函数首先会打印查询参数 `query`，然后调用 `validate` 函数进行表单验证。
 * 如果验证通过，会根据 `query.type` 的值决定是编辑还是创建乘车人信息。
 * 如果是编辑操作，会调用 `fetchEditPassenger` 函数，并根据返回结果提示用户操作是否成功。
 * 如果是创建操作，会调用 `fetchAddPassenger` 函数，并根据返回结果提示用户操作是否成功。
 * 无论哪种操作，成功后都会跳转到 `/passenger` 页面，失败则会显示错误信息。
 * 
 * @returns {void} 无返回值
 */
const onSubmit = () => {
  console.log(query)
  
  // 表单验证
  validate()
    .then(() => {
      loading.value = true
      
      // 根据操作类型（编辑或创建）准备请求参数
      let params = { username, ...toRaw(formData.value) }
      if (query.type === 'edit') {
        const { id, phone, realName } = formData.value
        params = { id, phone, username, realName}
        
        // 编辑乘车人信息
        return fetchEditPassenger(params).then((res) => {
          if (res.success) {
            message.success(
              `乘车人${query.type === 'edit' ? '修改' : '创建'}成功`
            )
            router.push('/passenger')
          } else {
            message.error(res.message)
            loading.value = false
          }
        })
      }
      
      // 创建乘车人信息
      fetchAddPassenger(params)
        .then((res) => {
          loading.value = true
          if (res.success) {
            message.success(
              `乘车人${query.type === 'edit' ? '修改' : '创建'}成功`
            )
            router.push('/passenger')
          } else {
            message.error(res.message)
            loading.value = false
          }
        })
        .catch((error) => console.log(error))
    })
    .catch((err) => console.log(err))
}
</script>

<style lang="scss" scoped></style>
