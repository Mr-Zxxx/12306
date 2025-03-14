import http from './axios'

// 登录
const fetchLogin = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/api/user-service/v1/login',
    data: body
  })
  http.defaults.headers.common['Authorization'] = data.data?.accessToken
  return data
}

// 注册
const fetchRegister = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/api/user-service/register',
    data: body
  })
  return data
}

// 车票信息
const fetchTicketSearch = async (params) => {
  const { data } = await http({
    method: 'GET',
    url: '/api/ticket-service/ticket/query',
    params
  })
  return data
}

// 站点信息
const fetchRegionStation = async (params) => {
  const { data } = await http({
    method: 'GET',
    url: '/api/ticket-service/region-station/query',
    params
  })
  return data
}

// 乘客信息
const fetchPassengerList = async (params) => {
  const { data } = await http({
    method: 'GET',
    url: '/api/user-service/passenger/query',
    params
  })
  return data
}

// 删除乘客信息
const fetchDeletePassenger = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/api/user-service/passenger/remove',
    data: body
  })
  return data
}

// 新增乘客信息
const fetchAddPassenger = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/api/user-service/passenger/save',
    data: body
  })
  return data
}

// 修改乘客信息
const fetchEditPassenger = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/api/user-service/passenger/update',
    data: body
  })
  return data
}

// 退出登录
const fetchLogout = async (body) => {
  const { data } = await http({
    method: 'GET',
    url: '/api/user-service/logout',
    data: body
  })
  http.defaults.headers.common['Authorization'] = null
  return data
}

// 购买车票
const fetchBuyTicket = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/api/ticket-service/ticket/purchase/v2',
    data: body
  })
  return data
}

// 订单详情
const fetchOrderBySn = async (params) => {
  const { data } = await http({
    method: 'GET',
    url: '/api/order-service/order/ticket/query',
    params
  })
  return data
}

// 支付
const fetchPay = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/api/pay-service/pay/create',
    data: body
  })
  return data
}

// 站点信息
const fetchStationAll = async () => {
  const { data } = await http({
    method: 'GET',
    url: '/api/ticket-service/station/all'
  })
  return data
}

// 用户信息
const fetchUserInfo = async (params) => {
  const { data } = await http({
    method: 'GET',
    url: '/api/user-service/query',
    params
  })
  return data
}

// 车次站点
const fetchTrainStation = async (params) => {
  const { data } = await http({
    method: 'GET',
    url: '/api/ticket-service/train-station/query',
    params
  })
  return data
}

// 订单列表
const fetchTicketList = async (params) => {
  const { data } = await http({
    method: 'GET',
    url: '/api/order-service/order/ticket/page',
    params
  })
  return data
}

// 取消订单
const fetchOrderCancel = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/api/ticket-service/ticket/cancel',
    data: body
  })
  return data
}

// 修改用户信息
const fetchUserUpdate = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/api/user-service/update',
    data: body
  })
  return data
}

// 订单状态
const fetchOrderStatus = async (params) => {
  const { data } = await http({
    method: 'GET',
    url: '/api/pay-service/pay/query/order-sn',
    params
  })
  return data
}

// 我的车票
const fetchMyTicket = async (params) => {
  const { data } = await http({
    method: 'GET',
    url: '/api/order-service/order/ticket/self/page',
    params
  })
  return data
}

// 退票
const fetchRefundTicket = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/api/ticket-service/ticket/refund',
    data: body
  })
}

// 管理员登录
const fetchAdminLogin = async (body) => {
  const { data } = await http({
    method: 'POST',
    url: '/admin/admin-service/login',
    data: body
  })
  http.defaults.headers.common['Authorization'] = data.data?.accessToken
  return data
}

// 用户信息列表
const fetchUserInfoList = async(body)=>{
  const {data} = await http({
    method:'POST',
    url:'api/user-service/queryList',
    data:body
  })
  return data
}

// 订单信息列表
const fetchOrderInfoList = async(body)=>{
  const {data} = await http({
    method:'POST',
    url:'api/order-service/order/ticket/queryList',
    data:body
  })
  return data
}

export {
  fetchLogin,
  fetchRegister,
  fetchTicketSearch,
  fetchRegionStation,
  fetchPassengerList,
  fetchDeletePassenger,
  fetchAddPassenger,
  fetchEditPassenger,
  fetchLogout,
  fetchBuyTicket,
  fetchOrderBySn,
  fetchPay,
  fetchStationAll,
  fetchUserInfo,
  fetchTrainStation,
  fetchTicketList,
  fetchOrderCancel,
  fetchOrderStatus,
  fetchUserUpdate,
  fetchMyTicket,
  fetchRefundTicket,
  fetchAdminLogin,
  fetchUserInfoList,
  fetchOrderInfoList
}
