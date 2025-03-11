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
                    <template v-if="column.key === 'name'">
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
                    <template v-else-if="column.key === 'tags'">
                        <span>
                            <a-tag v-for="tag in record.tags" :key="tag"
                                :color="tag === '已取消' ? 'volcano' : tag==='待出行' ? 'geekblue' : 'green'">
                                {{ tag }}
                            </a-tag>
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
import { reactive, ref } from 'vue';
import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue';
const expand = ref(false);
const formRef = ref();
const formState = reactive({});
const orderDate = ref();
const date = new Date();
// 输入框字段
const fields = [{
    label: '姓名',
    name: 'name',
    placeholder: '姓名',
    rules: [{ required: true, message: 'Please input something' }],
},
{
    label: '身份证号',
    name: 'idCard',
    placeholder: '身份证号码',
    rules: [{ required: true, message: 'Please input something' }],
}]

const columns = [
    {
        title: '订单号',
        dataIndex: 'orderId',
        key: 'orderId',
    },
    {
        name: 'Name',
        dataIndex: 'name',
        key: 'name',
    },
    {
        title: '身份证号',
        dataIndex: 'idNumber',
        key: 'idNumber',
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
        title: '目地站',
        dataIndex: 'destination',
        key: 'destination',
    },
    {
        title: '出发时间',
        dataIndex: 'startTime',
        key: 'startTime',
    },
    {
        title: '下单日期',
        dataIndex: 'orderDate',
        key: 'orderDate',
    },
    {
        title: '订单状态',
        dataIndex: 'tags',
        key: 'tags',
    },
    {
        title: '操作',
        key: 'action',
    },
];
const data = [
    {
        key: '1',
        orderId:'000001',
        name: '张三',
        idNumber: '123456789000000001',
        trainNumber: 'G35',
        departure: '北京',
        destination:'杭州',
        startTime:'2025-05-02',
        orderDate:'2025-03-01',
        tags: ['已完成'],
    },
    {
        key: '2',
        orderId:'000002',
        name: '李四',
        idNumber: '123456789000000002',
        trainNumber: 'G35',
        departure: '北京',
        destination:'杭州',
        startTime:'2025-05-01',
        orderDate:'2025-03-01',
        tags: ['已取消'],
    },
    {
        key: '3',
        orderId:'000003',
        name: '王五',
        idNumber: '123456789000000003',
        trainNumber: 'G35',
        departure: '北京',
        destination:'杭州',
        startTime:'2025-04-30',
        orderDate:'2025-03-01',
        tags: ['待出行'],
    },
];

const onFinish = (values) => {
    // 打印表单提交时收集到的值
    console.log('Received values of form: ', values);
    // 打印当前表单的状态
    console.log('formState: ', formState);
};


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
    text-align: center ;
    vertical-align: middle ;
}
:deep(.ant-table-thead>tr>th) {
    color: #ffffff ;
    background: #0976c3bb;
    text-align: center;
    vertical-align: middle;
}
</style>