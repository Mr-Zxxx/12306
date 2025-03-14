<template>
    <div>
        <h3>&nbsp;请输入订单相关信息：</h3>
        <a-form ref="formRef" name="advanced_search" class="ant-advanced-search-form" :model="formState"
            @finish="onFinish">
            <!-- 表单 -->
            <a-row :gutter="0" :span="0" class="search-form-wrapper">
                <div class="search-form">
                    <template v-for="(field, index) in fields" :key="index">
                        <a-form-item :name="field.name" :label="field.label">
                            <a-input v-model:value="formState[index]" :placeholder="field.placeholder"></a-input>
                        </a-form-item>
                    </template>
                    <div style=" display: flex; align-items: center;">下单日期:</div>
                    <a-date-picker v-model:value="orderDate" />
                    <!-- 提交查询信息 -->
                    <a-button type="primary" html-type="submit" @click="onFinish">搜索</a-button>
                    <a-button @click="() => formRef.resetFields()">重置</a-button>
                </div>
            </a-row>
        </a-form>
        <h3>&nbsp;搜索结果如下：</h3>
        <div>
            <a-table :columns="columns" :data-source="data">
                <!-- 表头自定义 -->
                <template #headerCell="{ column }">
                    <template v-if="column.key === 'realName'">
                        <span>
                            姓名
                        </span>
                    </template>
                </template>
                <!-- 表格自定义 -->
                <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'name'">
                        <a>
                            {{ record.name }}
                        </a>
                    </template>
                    <template v-else-if="column.key === 'status'">
                        <span>
                            <a-tag :color="getStatusColor(record.status)">
                                {{ getStatusText(record.status) }}
                            </a-tag>
                        </span>
                    </template>
                    <template v-else-if="column.key === 'amount'">
                        <span>
                            {{ '￥'+record.amount/100 }}
                        </span>
                    </template>
                    <!-- 操作列 -->
                    <template v-else-if="column.key === 'action'">
                        <span>
                            <a>改签</a>
                            <a-divider type="vertical" />
                            <a>退票</a>
                            <a-divider type="vertical" />
                            <a class="ant-dropdown-link">
                                查看详细
                                <down-outlined />
                            </a>
                        </span>
                    </template>
                </template>
            </a-table>
        </div>
    </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue';
import { fetchOrderInfoList } from '@/service';
const expand = ref(false);
const formRef = ref();
const formState = reactive({});
const orderDate = ref();
const date = new Date();
// 输入框字段
const fields = [
    {
        label: '订单号',
        name: 'orderSn',
        placeholder: '订单号',
        rules: [{ required: true, message: '请输入内容' }],
    },
    {
        label: '姓名',
        name: 'name',
        placeholder: '姓名',
        rules: [{ required: true, message: '请输入内容' }],
    },
    {
        label: '身份证号',
        name: 'idCard',
        placeholder: '身份证号码',
        rules: [{ required: true, message: '请输入内容' }],
    }]
// 表格字段
const columns = [
    {
        title: '订单号',
        dataIndex: 'orderSn',
        key: 'orderSn',
    },
    {
        name: 'Name',
        dataIndex: 'realName',
        key: 'realName',
    },
    {
        title: '身份证号',
        dataIndex: 'idCard',
        key: 'idCard',
    },
    {
        title: '车次',
        dataIndex: 'trainNumber',
        key: 'trainNumber',
    },
    {
        title: '始发站',
        dataIndex: 'departure',
        key: 'departure',
    },
    {
        title: '到达站',
        dataIndex: 'arrival',
        key: 'arrival',
    },
    {
        title: '出发时间',
        dataIndex: 'departureTime',
        key: 'departureTime',
    },
    {
        title: '下单时间',
        dataIndex: 'orderTime',
        key: 'orderTime',
    },
    {
        title: '订单状态',
        dataIndex: 'status',
        key: 'status',
    },
    {
        title: '订单金额',
        dataIndex: 'amount',
        key: 'amount',
    },
    {
        title: '操作',
        key: 'action',
    },
];
// 表格数据对象数组
let data = ref()
// 获取订单状态
const getStatusText = (status) => {
    switch (status) {
        case 0:
            return '待支付';
        case 10:
            return '已支付';
        case 20:
            return '已进站';
        case 30:
            return '已取消';
        case 40:
            return '已退票';
        case 50:
            return '已改签';
        default:
            return '/';
    }
}
// 获取状态颜色
const getStatusColor = (status) => {
    switch (status) {
        case 0:
            return 'orange';
        case 10:
            return 'blue';
        case 20:
            return 'green';
        case 30:
            return 'red';
        case 40:
            return 'gray';
        case 50:
            return 'purple';
        default:
            return '';
    }
}
const onFinish = (values) => {
    // 构建查询条件
    const requstBody = {
        orderSn: formState[0],
        name: formState[1],
        idCard: formState[2],
        orderDate: formState[3],
    }
    fetchOrderInfoList(requstBody).then(res => {
        console.log(res);
        data = res.data;
    })
    // 打印表单提交时收集到的值
    console.log('', values);
    // 打印当前表单的状态
    console.log('formState: ', formState);
};

onMounted(() => {

    const requstBody = {
        orderSn: formState[0],
        name: formState[1],
        idCard: formState[2],
        orderDate: formState[3],
    }
    fetchOrderInfoList(requstBody).then(res => {
        console.log('页面初始化赋值');
        data.value = res.data;
        for (let i = 0; i < data.length; i++) {
            data[i] = { ...data[i], key: i }
        }
        console.log(data);
    })
});

</script>

<style scoped>
#components-form-demo-advanced-search .ant-form {
    max-width: none;
}

.search-form {
    width: 100%;
    padding: 20px 20px 0;
    display: flex;
    /* gap: 20px; */
    border: 1px solid #1379d886;
    border-radius: 4px;
    background: #fff;
    /* flex-wrap: wrap; */
    margin-bottom: 10px;
}

.search-form>* {
    /* border: 1px solid #232323; */
    margin-left: 10px;
    margin-bottom: 20px;
}

#components-form-demo-advanced-search .search-result-list {
    margin-top: 20px;
    border: 1px solid #232323;
    border-radius: 2px;
    background-color: #b68484;
    min-height: 200px;
    text-align: center;
    padding-top: 80px;
}

[data-theme='dark'] .ant-advanced-search-form {
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid #434343;
    padding: 24px;
    border-radius: 2px;
}

[data-theme='dark'] #components-form-demo-advanced-search .search-result-list {
    border: 1px dashed #434343;
    background: rgba(255, 255, 255, 0.04);
}

:deep(.ant-table-tbody>tr>td) {
    text-align: center;
    vertical-align: middle;
}

:deep(.ant-table-thead>tr>th) {
    color: #ffffff;
    background: #0976c3bb;
    text-align: center;
    vertical-align: middle;
}
</style>