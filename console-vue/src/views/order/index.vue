<template>
  <div v-html="state.html"></div>
  <Space :style="{ width: '100%' }" direction="vertical">
    <Card>
      <div class="tip-wrapper">
        <span class="i-lock"></span>
        席位已锁定，请在提示时间内完成支付时间，完成网上购票。支付剩余时间：
        <span class="time">
          {{
            `${dayjs.duration(state.count).minutes()}分${dayjs
              .duration(state.count)
              .seconds()}秒`
          }}
        </span>
      </div>
    </Card>
    <Card title="订单信息">
      <!-- 使用 Space 组件创建一个垂直布局的容器，宽度为 100% -->
      <Space :style="{ width: '100%' }" direction="vertical">
        <!-- 显示订单的基本信息，包括日期、车次、出发站、到达站等 -->
        <div>
          <span class="important-text">{{ state.currentInfo?.ridingDate }}</span>
          <span class="important-text">（{{ getWeekNumber(dayjs(state.currentInfo?.ridingDate ?? new Date()).day())}}）</span>
          <span class="important-text">{{ state.currentInfo?.trainNumber }}</span>
          <span class="small-text">次</span>
          <span class="important-text">{{ state.currentInfo?.departure }}</span>
          <span class="small-text">站</span>
          <span class="important-text">（{{ state.currentInfo?.departureTime }}开）——</span>
          <span class="important-text">{{ state.currentInfo?.arrival }}</span>
          <span class="small-text">站</span>
          <span class="important-text">（{{ state.currentInfo?.arrivalTime }}到）</span>
        </div>

        <!-- 使用 Table 组件显示乘客详细信息，包括乘客 ID、证件类型、票类型、座位类型和金额 -->
        <Table :columns="columns" :pagination="false" :dataSource="state.currentInfo?.passengerDetails">
          <template #id="{ text, record, index }"> {{ index + 1 }}</template>
          <template #idType="{ text, record }">
            {{ID_CARD_TYPE.find((item) => item.value === text)?.label}}
          </template>
          <template #ticketType="{ text, record }">
            {{TICKET_TYPE_LIST.find((item) => item.value === text)?.label}}
          </template>
          <template #seatType="{ text, record }">
            {{SEAT_CLASS_TYPE_LIST.find((item) => item.code === text)?.label}}
          </template>
          <template #amount="{ text, record }">
            <span :style="{ color: 'rgb(252, 131, 2)' }">￥{{ text / 100 }}</span>
          </template>
        </Table>
        <!-- 显示总票价，并右对齐 -->
        <div :style="{ width: '100%', textAlign: 'end', padding: '10px' }">
          总票价：<span :style="{ color: '#ff8001' }">￥{{ totalAmount }}元</span>
        </div>
        <!-- 添加一个分割线 -->
        <Divider></Divider>
        <!-- 底部操作按钮区域，包含取消订单和网上支付按钮 -->
        <div :style="{ width: '100%', display: 'flex', justifyContent: 'center' }">
          <Space size="large">
            <!-- 取消订单按钮，点击后调用 fetchOrderCancel 函数，成功后跳转到票务搜索页面 -->
            <Button @click="() => {
              fetchOrderCancel({ orderSn: query?.sn }).then((res) => {
                if (res.success) {
                  message.success('订单取消成功')
                  router.push('/ticketSearch')
                } else {
                  message.error(res.message)
                }
              })
            }">取消订单</Button>
            <!-- 网上支付按钮，点击后打开支付弹窗 -->
            <Button :style="{ backgroundColor: '#ff8001', color: '#fff', border: 'none' }"
              @click="state.open = true">网上支付</Button>
          </Space>
        </div>
      </Space>
    </Card>
    <div>
    </div>
  </Space>
  <!-- 支付弹窗 -->
  <Modal :visible="state.open" title="请付款" @cancel="state.open = false" width="40%" :footer="null">
    <Spin :spinning="state.loading">
      <div>
        >>应付金额：<span :style="{ fontSize: '20px', color: ' #dc2408', fontWeight: 'bold' }">{{ totalAmount }}
        </span>
        元
      </div>
      <Divider dashed></Divider>
      <div :style="{ overflow: 'hidden' }">
        <div v-for="item in BANK_LIST" :key="item" class="bank3">
          <div class="bank3_5" @click="() => handlePay(item.value)">
            <img :src="item.img" :alt="item.name" />
          </div>
        </div>
      </div>
    </Spin>
  </Modal>
  <!-- 支付完成弹窗 -->
  <Modal :visible="state.isPayingOpen" title="网上支付提示" :footer="null" style="top: 30%">
    <Row :gutter="[24, 6]">
      <Col :span="6" :style="{
        display: 'flex',
        textAlign: 'end',
        alignItems: 'center',
        justifyContent: 'end'
      }">
      <Spin size="large" :spinning="true"></Spin>
      </Col>
      <Col :span="14">
      <div :style="{ fontSize: '12px' }">
        支付完成后，请不要关闭此支付验证窗口
      </div>
      <div :style="{ fontSize: '12px' }">
        支付完成后请更具您支付的情况点击下面按钮。
      </div>
      </Col>
    </Row>
    <Space :style="{ display: 'flex', justifyContent: 'center', marginTop: '20px' }">
      <Button type="default" @click="() => router.push('/ticketList')">支付遇到问题</Button>
      <Button type="primary" @click="() => router.push('/ticketList')">支付完成</Button>
    </Space>
  </Modal>
</template>

<script setup>
import {
  Card,
  Space,
  Table,
  Divider,
  Button,
  Modal,
  message,
  Spin,
  Row,
  Col
} from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  fetchOrderBySn,
  fetchPay,
  fetchOrderCancel,
  fetchOrderStatus
} from '@/service'
import { useRoute, useRouter } from 'vue-router'
import { onMounted, reactive, computed, watch, onUnmounted } from 'vue'
import {
  TICKET_TYPE_LIST,
  ID_CARD_TYPE,
  BANK_LIST,
  SEAT_CLASS_TYPE_LIST
} from '@/constants'
import { getWeekNumber } from '@/utils'
let timer = undefined

const { query } = useRoute()
const router = useRouter()
const state = reactive({
  count: 600000,
  currentInfo: null,
  open: false,
  html: '',
  loading: false,
  isInitiatePayment: false,
  isPaying: false,
  isPayingOpen: false
})
const columns = [
  { title: '序号', dataIndex: 'id', slots: { customRender: 'id' } },
  { title: '姓名', dataIndex: 'realName' },
  { title: '证件类型', dataIndex: 'idType', slots: { customRender: 'idType' } },
  { title: '证件号码', dataIndex: 'idCard' },
  {
    title: '票种',
    dataIndex: 'ticketType',
    slots: { customRender: 'ticketType' }
  },
  {
    title: '席别',
    dataIndex: 'seatType',
    slots: { customRender: 'seatType' }
  },
  { title: '车厢', dataIndex: 'carriageNumber' },
  { title: '席位号', dataIndex: 'seatNumber' },
  { title: '票价(元)', dataIndex: 'amount', slots: { customRender: 'amount' } }
]

/**
 * onMounted 是 Vue 3 的生命周期钩子函数，在组件挂载完成后执行。
 * 该函数主要用于在组件挂载后启动一个定时器，定时更新状态并获取订单状态。
 * 同时，它还会计算剩余时间的分钟数，并获取订单信息。
 */
onMounted(() => {
  // 启动一个每秒执行一次的定时器
  timer = setInterval(() => {
    // 每秒钟减少 state.count 的值 1000 毫秒
    state.count -= 1000
    // 调用 getOrderStatus 函数获取订单状态
    getOrderStatus()
  }, 1000)

  // 使用 dayjs 计算 state.count 的分钟数
  dayjs.duration(state.count).minutes()

  // 调用 getOrder 函数获取订单信息
  getOrder()
})
onUnmounted(() => {
  clearInterval(timer)
})

const getOrder = () => {
  fetchOrderBySn({ orderSn: query?.sn }).then((res) => {
    if (res.success) {
      state.currentInfo = res.data
    }
  })
}

watch(
  () => state.isPaying,
  (newV) => {
    state.isPayingOpen = newV
  }
)
const totalAmount = computed(() => {
  let amount = 0
  state.currentInfo?.passengerDetails?.map((item) => {
    amount += item?.amount ?? 0
  })
  return amount / 100
})

const handlePay = (channel) => {
  if (channel !== 0) {
    return message.error('该支付方式暂未对接，请稍候...')
  }
  state.isInitiatePayment = true
  state.open = false
  state.isPayingOpen = true
  const body = {
    channel: 0,
    tradeType: 0,
    orderSn: query.sn,
    totalAmount: totalAmount.value,
    outOrderSn: query.orderSn,
    subject: `${state.currentInfo.departure}-${state.currentInfo.arrival}`
  }
  fetchPay(body).then((res) => {
    state.html = res.data?.body
    state.loading = true
    setTimeout(() => {
      state.loading = false
      window.open(`/aliPay?body=${encodeURIComponent(res.data?.body)}`)
    }, 500)
  })
}

const getOrderStatus = () => {
  state.isInitiatePayment &&
    fetchOrderStatus({ orderSn: query?.sn })
      .then((res) => {
        state.isPaying = res.data.status === 0
        res.data.status === 20 &&
          router.push(`/paySuccess?orderSn=${res.data.orderSn}`)
      })
      .catch((error) => {
        console.log('error:::', error)
      })
}
</script>

<style lang="scss" scoped>
.tip-wrapper {
  display: flex;
  align-items: center;

  .i-lock {
    display: inline-block;
    width: 42px;
    height: 42px;
    background: url(https://kyfw.12306.cn/otn/resources/images/bg02.png) repeat-x;
    background-position: 0 -250px;
    margin-right: 20px;
  }

  .time {
    color: #fd6a09;
    font-weight: bolder;
  }
}

.tips-txt {
  background: #fffbe5;
  border: 1px solid #fbd800;
  padding: 5px;
  border-radius: 5px;
  -moz-border-radius: 5px;
  -ms-border-radius: 5px;
  -o-border-radius: 5px;
  -webkit-border-radius: 5px;
}

.important-text {
  font-size: 16px;
  font-weight: bolder;
}

.bank3 {
  float: left;
  width: 170px;
  padding: 8px 0;
  margin-left: 16px;

  .bank3_5 {
    float: left;
    width: 160px;
    cursor: pointer;
  }
}

.cyx-hd {
  position: relative;
  line-height: 20px;
  padding: 5px 5px 5px 65px;
  background-image: linear-gradient(45deg, #fef9ff 0%, #fff 100%);
  border: 1px solid #bfd7e3;

  .cyx-hd-label {
    position: absolute;
    width: 40px;
    top: 50%;
    left: 18px;
    margin-top: -8px;
    height: 16px;
    line-height: 16px;
    display: flex;
    justify-content: space-around;
    align-items: center;

    .icon-cyx {
      float: left;
      width: 16px;
      height: 16px;
      background-image: url(https://kyfw.12306.cn/otn/resources/images/icon-cyx.png);
    }
  }
}

::v-deep {
  .ant-table-thead {
    .ant-table-cell {
      background: #f6f6f6;
    }
  }
}
</style>
