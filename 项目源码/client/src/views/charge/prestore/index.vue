<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="用户列表"
      :columns="columns"
      :requestApi="getTableList"
      :initParam="initParam"
      :dataCallback="dataCallback"
    >
      <!-- 表格 header 按钮 -->

      <!-- 表格操作 -->
      <template #operation="scope">
        <el-button
          size="small"
          link
          :icon="CreditCard"
          @click="openDrawer('账户充值',scope)"
        >
          账户充值
        </el-button>
      </template>
    </ProTable>
    <prestoreDialog ref="DialogRef" />
  </div>
</template>

<script setup lang="ts" name="useProTable">
import { ref, reactive } from "vue";
import prestoreDialog from "./prestoreDialog/index.vue";
import { ColumnProps } from "@/components/ProTable/interface";
import ProTable from "@/components/ProTable/index.vue";
import { CreditCard } from "@element-plus/icons-vue";
import { pageDepositRechargeByKey, recharge } from "@/apis/depositRecharge";

// 获取 ProTable 元素，调用其获取刷新数据方法（还能获取到当前查询参数，方便导出携带参数）
const proTable = ref();
const initParam = reactive({});

// dataCallback 是对于返回的表格数据做处理，如果你后台返回的数据不是 list && total && pageNum && pageSize 这些字段，那么你可以在这里进行处理成这些字段
// 或者直接去 hooks/useTable.ts 文件中把字段改为你后端对应的就行
const dataCallback = (data: any) => {
  return {
    list: data.list,
    total: data.total,
    pageNum: data.pageNum,
    pageSize: data.pageSize
  };
};

// 如果你想在请求之前对当前请求参数做一些操作，可以自定义如下函数：params 为当前所有的请求参数（包括分页），最后返回请求列表接口
// 默认不做操作就直接在 ProTable 组件上绑定	:requestApi="getUserList"
const getTableList = (params: any) => {
  let newParams = JSON.parse(JSON.stringify(params));
  return pageDepositRechargeByKey(newParams);
};

// 打开 drawer(新增、查看、编辑)
const DialogRef = ref();
const openDrawer = (title: string, rowData: any = {}) => {
  const params = {
    title,
    rowData: { ...rowData.row },
    api: recharge,
    getTableList: proTable.value.getTableList
  };
  DialogRef.value.acceptParams(params);
};

// 表格配置项
const columns: ColumnProps<any>[] = [
  { prop: "rank", label: "序号", width: 55 },
  { prop: "elderName", label: "老人姓名", search: { el: "input" } },
  { prop: "bedName", label: "床位名称" },
  { prop: "idNum", label: "身份证号", search: { el: "input" } },
  { prop: "elderPhone", label: "老人电话", search: { el: "input" } },
  { prop: "balance", label: "账户余额" },
  { prop: "operation", label: "操作", width: 150 }
];
</script>

<style lang="scss" scoped></style>
