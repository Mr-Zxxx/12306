<template>
  <div class="buy-ticket-wrapper">
    <Space direction="vertical" :style="{ width: '100%' }" size="large">
      <Card>
        <template #title>
          <div class="title-wrapper">
            <h1>列表信息</h1>
          </div>
        </template>
        <div>
          <!-- 显示出发日期 -->
          <span class="important-text">{{ query.departureDate }}</span>
          <!-- 显示出发日期的星期几，通过 `getWeekNumber` 函数将数字转换为星期几 -->
          <span class="important-text">（{{ getWeekNumber(dayjs(query.departureDate).day()) }}）</span>
          <!-- 显示列车车次 -->
          <span class="important-text">{{ query.trainNumber }}次 </span>
          <!-- 显示当前列车的出发站 -->
          <span class="important-text">{{ state.currTrain?.departure }}站</span>
          <!-- 显示当前列车的出发时间 -->
          <span class="important-text">（{{ state.currTrain?.departureTime }}开）——</span>
          <!-- 显示当前列车的到达站 -->
          <span class="important-text">{{ state.currTrain?.arrival }}站</span>
          <!-- 显示当前列车的到达时间 -->
          <span class="important-text">（{{ state.currTrain?.duration }}到）</span>
        </div>
        <Divider dashed></Divider>
        <div class="seat-wrapper">
          <div v-for="item in state.currentSeat">
            <span>{{
              SEAT_CLASS_TYPE_LIST.find((seat) => seat.code === item.type)
                ?.label
            }}</span>
            （ <span class="price">￥{{ item?.price }}</span>）
            <span>{{ item.quantity >= 1 ? '有票' : '1张票' }}</span>
          </div>
        </div>
        <div class="tip">
          建议信息
        </div>
      </Card>
      <Card>
        <template #title>
          <div class="title-wrapper">
            <h1>乘客信息</h1>
          </div>
        </template>
        <div class="user-tip">
          <UserOutlined />
          <span>乘车人</span>
        </div>
        <div class="check-wrapper">
          <CheckboxGroup v-if="state.currPassengerList?.length" v-model:value="currPassenger">
            <Checkbox v-for="item in state.currPassengerList" :value="item.id">{{ item.realName }}</Checkbox>
          </CheckboxGroup>
          <Button v-else @click="router.push('/passenger')" type="link">去添加乘车人</Button>
        </div>
        <Divider></Divider>
        <Table :columns="columns" :data-source="(state.dataSource ?? []).filter((item) =>
          currPassenger?.includes(item.id)
        )
          " :locale="{ emptyText: '请先选择乘车人' }" :pagination="false">
          <template #ticketType="{ text, record }">
            <Select :style="styleWidth" :value="text" @select="(value) => handleChooseTicketType(record.id, value)">
              <SelectOption v-for="item in TICKET_TYPE_LIST" :value="item.value">{{ item.label }}</SelectOption>
            </Select>
          </template>
          <template #seatType="{ text, record }">
            <Select :style="styleWidth" @select="(value) => handleChooseSeat(record.id, value)">
              <SelectOption v-for="item in state.currentSeat" :value="item.type">
                {{
                  `${SEAT_CLASS_TYPE_LIST.find((seat) => seat.code === item.type)
                    ?.label
                  }(￥${item.price})`
                }}</SelectOption>
            </Select>
          </template>
          <template #realName="{ text }">
            <Input :value="text" disabled :style="styleWidth"></Input>
          </template>
          <template #idType="{ text }">
            <Select disabled :style="styleWidth" :value="text">
              <SelectOption v-for="item in ID_CARD_TYPE" :value="item.value">{{
                item.label
              }}</SelectOption>
            </Select>
          </template>
          <template #idCard="{ text }">
            <Input disabled :value="text" :style="styleWidth"></Input>
          </template>
          <template #edit="{ text, record }">
            <CloseCircleOutlined :style="{ color: '#fd6a09' }" @click="() => handleDelete(record.id)" />
          </template>
        </Table>
      </Card>
      <div>
        <span>提交订单表示已阅读并同意</span>
        <span>
          <a href="">《国铁集团铁路旅客运输规程》</a>
          <a href="">《服务条款》</a>
        </span>
      </div>
      <div :style="{ width: '100%', textAlign: 'center' }">
        <Space size="large">
          <Button @click="() => router.push('/ticketSearch')">上一步</Button>
          <Button class="submit-btn" @click="
            () => {
              handleSubmit()
            }
          ">提交订单</Button>
        </Space>
      </div>

    </Space>
    <Modal :visible="state.open" title="请核对以下信息" wrap-class-name="check-info-wrapper" width="40%"
      @cancel="state.open = false" :footer="null">
      <Space direction="vertical" :style="{ width: '100%' }">
        <div>
          <span class="important-text">{{ query.departureDate }}</span>
          <span class="important-text">（{{ getWeekNumber(dayjs(query.departureDate).day()) }}）</span>
          <span class="important-text">{{ query.trainNumber }}</span>
          <span class="small-text">次</span>
          <span class="important-text">{{ state.currTrain?.departure }}</span>
          <span class="small-text">站</span>
          <span class="important-text">（{{ state.currTrain?.departureTime }}开）——</span>
          <span class="important-text">{{ state.currTrain?.arrival }}</span>
          <span class="small-text">站</span>
          <span class="important-text">（{{ state.currTrain?.duration }}到）</span>
        </div>
        <Table :columns="checkColumns" :data-source="state.dataSource" :pagination="false">
          <template #seatType="{ text, record }">
            <span>
              {{
                SEAT_CLASS_TYPE_LIST.find((seat) => seat.code === text)?.label
              }}
            </span>
          </template>
          <template #discountType="{ text }">
            {{TICKET_TYPE_LIST.find((item) => item.value === text)?.label}}
          </template>
          <template #idType="{ text }">
            {{ID_CARD_TYPE.find((item) => item.value === text)?.label}}
          </template>
        </Table>
        <div v-if="
          state.isChooseSeat &&
          !(state.dataSource?.length > 5) &&
          state.dataSource?.length <= state.seatPosition.length
        ">
          <a href="">*如果本次列车剩余席位无法满足您的选座需求，系统将自动为您分配席位</a>
          <div class="seat-choose-wrapper">
            <div>
              <div class="tip">
                <IconFont type="icon-laba001"></IconFont>
                请选座
              </div>
              <div>
                已选座{{ state.currentSeatCode.length }}/{{
                  state.dataSource.length
                }}
              </div>
            </div>
            <div>
              <div v-for="(item, index) in state.dataSource?.slice(0, 2)">
                <div class="action-wrapper">
                  <div>窗</div>
                  <Divider type="vertical"></Divider>
                  <div>
                    <div class="seat-img" v-for="(item, index, length) in state.seatPosition
                      .slice(
                        0 + index * state.seatNumber,
                        (1 + index) * state.seatNumber
                      )
                      .slice(0, state.seatLeft)" @click="() => handleSelectSeat(item)" :class="{
                        cur: state.currentSeatCode?.indexOf(item) !== -1
                      }">
                      {{ item.slice(0, 1) }}
                    </div>
                    <Divider type="vertical"></Divider>
                  </div>
                  <div>过道</div>
                  <Divider type="vertical"></Divider>
                  <div>
                    <div class="seat-img" v-for="(item, index, length) in state.seatPosition
                      .slice(
                        0 + index * state.seatNumber,
                        (1 + index) * state.seatNumber
                      )
                      .slice(state.seatLeft, state.seatNumber)" @click="() => handleSelectSeat(item)" :class="{
                        cur: state.currentSeatCode?.indexOf(item) !== -1
                      }">
                      {{ item.slice(0, 1) }}
                    </div>
                    <Divider type="vertical"></Divider>
                  </div>
                  <div>窗</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else>
          <a href="">*系统将随机为您申请席位，暂不支持自选席位</a>
        </div>
        <Divider dashed></Divider>
        <div class="info-tip">
          本次列车，<span v-for="item in state.currentSeat">
            {{
              SEAT_CLASS_TYPE_LIST.find((seat) => seat.code === item.type)
                ?.label
            }}余票{{ item.quantity }},</span>
        </div>
        <Space size="large" :style="{
          width: '100%',
          textAlign: 'center',
          justifyContent: 'center'
        }">
          <Button @click="state.open = false">返回修改</Button>
          <Button :loading="state.loading" :style="{
            backgroundColor: '#ff8001',
            color: '#fff',
            border: 'none'
          }" @click="handleSubmitBuyTicket">确认</Button>
        </Space>
      </Space>
    </Modal>
  </div>
</template>

<script setup>
import {
  Card,
  Divider,
  Space,
  Checkbox,
  CheckboxGroup,
  Table,
  Select,
  SelectOption,
  Input,
  Button,
  Modal,
  message
} from 'ant-design-vue'
import { useRoute } from 'vue-router'
import {
  fetchTicketSearch,
  fetchPassengerList,
  fetchBuyTicket
} from '@/service'
import { onMounted, reactive, toRaw, watch, ref } from 'vue'
import { getWeekNumber } from '@/utils'
import { UserOutlined, CloseCircleOutlined } from '@ant-design/icons-vue'
import { TICKET_TYPE_LIST, ID_CARD_TYPE } from '@/constants'
import IconFont from '@/components/icon-font'
import dayjs from 'dayjs'
import { SEAT_CLASS_TYPE_LIST } from '@/constants'
import Cookie from 'js-cookie'
import router from '@/router'
const styleWidth = { width: '150px' }
// 获取当前日期
const { query } = useRoute()
const username = Cookie.get('username')
const state = reactive({
  currTrain: null,
  currPassengerList: [],
  dataSource: [],
  rawDataSource: [],
  currentSeat: [],
  open: false,
  currentSeatCode: [],
  seatPosition: [],
  isChooseSeat: true,
  seatLeft: 3,
  seatNumber: 5,
  loading: false,
  departureDate: query.departureDate,
})
const currPassenger = ref([])

/**
 * 在组件挂载时执行的函数，用于初始化数据。
 * 该函数主要完成以下任务：
 * 1. 根据查询条件获取车票搜索结果，并设置当前车次和座位信息。
 * 2. 获取当前用户的乘客列表，并设置乘客列表数据源。
 */
onMounted(() => {
  // 根据查询条件获取车票搜索结果
  fetchTicketSearch({
    fromStation: query.fromStation,
    toStation: query.toStation,
    departureDate: query.departureDate
  }).then((res) => {
    if (res.success) {
      // 从搜索结果中找到当前车次
      const currTrain = res.data.trainList.find(
        (item) => item.trainNumber === query.trainNumber
      )
      // 设置当前车次的座位信息和车次信息
      state.currentSeat = currTrain.seatClassList
      state.currTrain = currTrain
    }
  })

  // 获取当前用户的乘客列表
  fetchPassengerList({ username }).then((res) => {
    if (res.success) {
      // 设置当前乘客列表
      state.currPassengerList = res.data ?? []
      // 将乘客列表映射为带有序号的数据源
      state.dataSource = res.data?.map((item, index) => ({
        ...item,
        keyNumber: index + 1
      }))
      // 设置原始数据源，用于后续操作
      state.rawDataSource = res.data?.map((item, index) => ({
        ...item,
        keyNumber: index + 1
      }))
    }
  })
})

watch(
  () => state.dataSource,
  (newValue) => {
    let seatType = newValue?.length && newValue[0]?.seatType
    let seatPosition = ['A', 'B', 'C', 'D', 'E']
    let seatLeft = 3
    if (
      seatType ===
      SEAT_CLASS_TYPE_LIST.find((item) => item.label === '商务座')?.code
    ) {
      seatPosition = ['A', 'C', 'F']
      seatLeft = 2
    }
    if (
      seatType ===
      SEAT_CLASS_TYPE_LIST.find((item) => item.label === '一等座')?.code
    ) {
      seatPosition = ['A', 'C', 'D', 'F']
      seatLeft = 2
    }
    if (
      seatType ===
      SEAT_CLASS_TYPE_LIST.find((item) => item.label === '二等座')?.code
    ) {
      seatPosition = ['A', 'B', 'C', 'D', 'F']
      seatLeft = 3
    }
    console.log('newValue.length:::', newValue?.length)
    console.log('seatPosition?.length :::', seatPosition?.length)
    let seatList = new Array(
      (newValue?.length ?? 1) * (seatPosition?.length ?? 1)
    ).fill('')

    seatList = seatList?.map((item, index) => {
      if (index < seatPosition?.length - 1) {
        return `${seatPosition[index]}0`
      } else {
        return `${seatPosition[index % seatPosition.length]}${Math.floor(
          index / seatPosition?.length
        )}`
      }
    })
    state.seatPosition = seatList
    state.seatLeft = seatLeft
    state.seatNumber = seatPosition?.length
  },
  {
    deep: true
  }
)

watch([() => currPassenger.value], (newValue) => {
  const list = newValue.map((item) => toRaw(item))?.flat()
  const newDataSource = state.rawDataSource.filter((item) => {
    return list.includes(item.id)
  })
  state.dataSource = newDataSource
})

watch(
  () => state.dataSource,
  (newValue) => {
    let isChooseSeat = true
    let seatType = newValue?.length && newValue[0].seatType
    newValue?.length &&
      newValue.map((item) => {
        if (item.seatType !== seatType) {
          isChooseSeat = false
        }
      })
    state.isChooseSeat = isChooseSeat
  },
  { deep: true }
)

const columns = [
  {
    title: '序号',
    dataIndex: 'keyNumber'
  },
  {
    title: '票种',
    dataIndex: 'discountType',
    slots: { customRender: 'ticketType' }
  },
  {
    title: '席别',
    dataIndex: '',
    slots: { customRender: 'seatType' }
  },
  {
    title: '姓名',
    dataIndex: 'realName',
    slots: { customRender: 'realName' }
  },
  {
    title: '证件类型',
    dataIndex: 'idType',
    slots: { customRender: 'idType' }
  },
  {
    title: '证件号码',
    dataIndex: 'idCard',
    slots: { customRender: 'idCard' }
  },
  {
    title: '操作',
    dataIndex: '',
    slots: { customRender: 'edit' }
  }
]

const handleDelete = (id) => {
  state.dataSource = (state.dataSource ?? []).filter((item) => {
    return item.id !== id
  })
  currPassenger.value = currPassenger.value.filter((item) => {
    return item !== id
  })
}

const handleChooseSeat = (id, value) => {
  let cIndex
  state.dataSource.find((item, index) => {
    if (item.id === id) {
      cIndex = index
      return true
    }
  })
  state.dataSource[cIndex]['seatType'] = value
}

const handleChooseTicketType = (id, value) => {
  let cIndex
  state.dataSource.find((item, index) => {
    if (item.id === id) {
      cIndex = index
      return true
    }
  })
  state.dataSource[cIndex]['idType'] = value
}

const handleSubmit = () => {
  const canNotSubmit = state.dataSource.some(
    (item) => item.seatType === null || item.seatType === undefined
  )
  if (canNotSubmit || state.dataSource?.length === 0) {
    return message.error('请补全信息')
  }
  state.open = true
}

const checkColumns = [
  {
    title: '序号',
    dataIndex: 'keyNumber'
  },
  {
    title: '席别',
    dataIndex: 'seatType',
    slots: { customRender: 'seatType' }
  },
  {
    title: '票种',
    dataIndex: 'discountType',
    slots: { customRender: 'discountType' }
  },
  {
    title: '姓名',
    dataIndex: 'realName'
  },
  {
    title: '证件类型',
    dataIndex: 'idType',
    slots: { customRender: 'idType' }
  },
  {
    title: '证件号码',
    dataIndex: 'idCard'
  }
]

const handleSelectSeat = (code) => {
  if (state.currentSeatCode?.indexOf(code) !== -1) {
    state.currentSeatCode = state.currentSeatCode?.filter(
      (item) => item !== code
    )
  } else {
    if (state.currentSeatCode.length === state.dataSource.length) {
      state.currentSeatCode[state.currentSeatCode.length - 1] = code
    } else {
      state.currentSeatCode.push(code)
    }
  }
}

/**
 * 处理提交购票请求的函数
 * 
 * 该函数负责收集用户选择的乘客信息、座位信息以及车次信息，
 * 并将其作为参数发送购票请求。根据请求结果，进行相应的页面跳转或错误提示。
 * 
 * 流程：
 * 1. 构建请求参数，包括车次ID、乘客信息、选择的座位、出发地和目的地。
 * 2. 发送购票请求，并处理响应结果。
 * 3. 如果购票成功，跳转至订单页面；如果失败，显示错误信息。
 * 
 * @returns {void} 无返回值
 */
const handleSubmitBuyTicket = () => {
  // 初始化请求参数，包含车次ID
  let params = { trainId: query?.trainId }

  // 遍历乘客数据，提取乘客ID和座位类型
  const passengers = state.dataSource.map((item) => ({
    passengerId: item.id,
    seatType: item.seatType
  }))

  // 合并所有请求参数，包括乘客信息、选择的座位、出发地和目的地
  params = {
    ...params,
    passengers,
    chooseSeats: toRaw(state.currentSeatCode),
    departure: state.currTrain?.departure,
    arrival: state.currTrain?.arrival,
    ridingDate: state.departureDate
  }

  // 设置加载状态为true，表示正在处理请求
  state.loading = true

  // 发送购票请求
  fetchBuyTicket(params)
    .then((res) => {
      if (res.success) {
        // 购票成功，显示成功消息并跳转至订单页面
        //ant-design-ui库函数，用于显示消息提示
        message.success('下单成功，正在跳转至订单')
        setTimeout(() => {
          router.push(`/order?sn=${res.data.orderSn}`)
        }, 500)
      } else {
        // 购票失败，显示错误消息
        message.error(res.message)
      }
      // 请求处理完毕，设置加载状态为false
      state.loading = false
    })
    .catch((error) => {
      // 请求发生错误，设置加载状态为false并打印错误信息
      state.loading = false
      console.log(error)
    })
}
</script>

<style lang="scss" scoped>
.buy-ticket-wrapper {
  .title-wrapper {
    display: flex;
    height: 20px;
    line-height: 20px;

    h1 {
      font-size: 16px;
      margin: 0;
      color: #fff;
    }

    h2 {
      font-size: 14px;
      margin: 0;
      color: #fff;
    }
  }

  .important-text {
    font-size: 16px;
    font-weight: bolder;
    // margin-right: 5px;
  }

  .seat-wrapper {
    display: flex;
    margin-bottom: 10px;

    >div {
      margin-right: 10px;

      .price {
        color: #f57531;
      }
    }
  }

  .tip {
    color: #1e71bd;
  }

  .user-tip {
    .anticon-user {
      color: #1e71bd;
      font-size: 20px;
      margin-right: 5px;
    }
  }

  .check-wrapper {
    padding: 5px 10px;
  }

  .submit-btn {
    background-color: #fd6a09;
    color: #fff;
  }

  .tips-txt {
    background: #fffbe5;
    border: 1px solid #fbd800;
    padding: 5px;
  }
}

::v-deep {
  .ant-card-head {
    background-color: #1e71bd;
  }

  .ant-card-body {
    background-color: #ebedf6;
    padding: 10px 15px;
  }

  .ant-divider-dashed {
    border-color: #d1d3da;
  }

  .ant-divider-horizontal {
    margin: 10px 0;
  }

  .ant-table-thead {
    .ant-table-cell {
      background: #f3f3f3 !important;
      background-color: #f3f3f3 !important;
    }

    >tr>th {
      padding: 8px;
    }
  }

  .check-info-wrapper {
    .ant-modal-header {
      background-color: #2386c8;
    }
  }
}

.check-info-wrapper {
  .important-text {
    font-size: 16px;
    font-weight: bolder;
  }

  .info-tip {
    color: #909090;

    span {
      font-size: 18px;
      color: #db0000;
      font-weight: bolder;
    }
  }
}

.seat-choose-wrapper {
  display: flex;
  width: 100%;
  background-color: #f3f3f3;
  padding: 20px;

  .tip {
    color: #ff8001;
    margin-right: 40px;
  }

  .action-wrapper {
    display: flex;
    align-items: center;

    padding: 20px;

    .seat-img {
      display: inline-block;
      text-align: center;
      width: 30px;
      height: 28px;
      line-height: 25px;
      background: url(https://kyfw.12306.cn/otn/resources/images/bg017.png) no-repeat;
      color: #fff;
      margin: 0 5px;
      cursor: pointer;
      background-position: -80px 0;
    }

    .cur {
      background-position: -40px 0;
    }
  }
}
</style>
