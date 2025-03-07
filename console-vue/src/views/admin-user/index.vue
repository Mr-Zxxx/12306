<template>
    <div>
        <h3> &nbsp;请输入用户信息：</h3>
        <a-form ref="formRef" name="advanced_search" class="ant-advanced-search-form" :model="formState"
            @finish="onFinish">
            <!-- 表单 -->
            <a-row :gutter="0" :span="0" class="search-form-wrapper">
                <div class="search-form">
                    <template v-for="(field, index) in fields" :key="index">
                        <a-form-item class="center" :name="field.name" :label="field.label">
                            <a-input v-model:value="formState[index]" :placeholder="field.placeholder"></a-input>
                        </a-form-item>
                    </template>
                    <a-button type="primary" html-type="submit" @click="onFinish">搜索</a-button>
                    <a-button @click="() => formRef.resetFields()">重置</a-button>
                </div>
            </a-row>
        </a-form>
        <h3> &nbsp;搜索结果如下：</h3>
        <div class="search-result-list">
            <!-- 表头 -->
            <a-table v-if="dataSource.length" :columns="columns" :data-source="dataSource" bordered>
                <template #bodyCell="{ column, text, record }">
                    <template v-if="['name', 'idNumber', 'phone','email','type'].includes(column.dataIndex)">
                        <div>
                            <a-input v-if="editableData[record.key]"
                                v-model:value="editableData[record.key][column.dataIndex]" style="margin: -5px 0" />
                            <template v-else>
                                {{ text }}
                            </template>
                        </div>
                    </template>
                    <template v-else-if="column.dataIndex === 'operation'">
                        <div class="editable-row-operations">
                            <span v-if="editableData[record.key]">
                                <a-typography-link @click="save(record.key)">保存</a-typography-link>
                                <a-popconfirm title="尚未保存，是否取消？" @confirm="cancel(record.key)">
                                    <a>取消</a>
                                </a-popconfirm>
                            </span>
                            <span v-else>
                                <a @click="edit(record.key)">修改</a>
                            </span>
                        </div>
                    </template>
                </template>
            </a-table>
            <div class="no-search-result-list" v-else>
                <div>暂无数据</div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { FormInstance } from 'ant-design-vue';
import { cloneDeep } from 'lodash-es';
import { UnwrapRef } from 'vue';
const expand = ref(false);
const formRef = ref();
const formState = reactive({});

// 输入框字段
const fields = [{
    label: '姓名',
    name: 'name',
    placeholder: '姓名',
    rules: [{ required: true, message: 'Please input something' }],
},
{
    label: '身份证号',
    name: 'idNumber',
    placeholder: '身份证号码',
    rules: [{ required: true, message: 'Please input something' }],
},
{
    label: '电话号码',
    name: 'phone',
    placeholder: '电话号码',
    rules: [{ required: true, message: 'Please input something' }],
},
]
const onFinish = (values) => {
    // 打印表单提交时收集到的值
    console.log('Received values of form: ', values);
    // 打印当前表单的状态
    console.log('formState: ', formState);
};

// 表格
const columns = [
    {
        title: '姓名',
        dataIndex: 'name',
        width: '15%',
    },
    {
        title: '身份证号码',
        dataIndex: 'idNumber',
        width: '20%',
    },
    {
        title: '手机号',
        dataIndex: 'phone',
        width: '20%',
    },
    {
        title: '邮箱信息',
        dataIndex: 'email',
        width: '20%',
    },
    {
        title: '乘客类型',
        dataIndex: 'type',
        width: '10%',
    },
    {
        title: '编辑',
        dataIndex: 'operation',
    },
];

const data = [];
for (let i = 0; i < 5; i++) {
    data.push({
        key: i.toString(),
        name: `张${i}`,
        idNumber: 32,
        phone: `152xxxx000${i}`,
        email: `${i}@qq.com`,
        type: '成人票',
    });
}

const dataSource = ref(data);
const editableData = reactive({});

const edit = (key) => {
    editableData[key] = cloneDeep(dataSource.value.filter(item => key === item.key)[0]);
};
const save = (key) => {
    Object.assign(dataSource.value.filter(item => key === item.key)[0], editableData[key]);
    delete editableData[key];
};
const cancel = (key) => {
    delete editableData[key];
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
    border: 1px solid #1379d881;
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

.editable-row-operations a {
    margin-right: 8px;
}

/* 表格单元格文字居中 */
:deep(.ant-table-tbody>tr>td) {
    text-align: center ;
    vertical-align: middle ;
}
:deep(.ant-table-thead > tr > th) {
    color: #ffffff ;
    background: #0976c3bb;
    text-align: center;
    vertical-align: middle;
}
.no-search-result-list{
    width: 100%;
    height: 300px;
    display: flex;
    justify-content: center;
    align-items: center;
    background:#fff;
    color: #43434384;
    border-radius: 6px;
    font-size: 20px;
}
</style>