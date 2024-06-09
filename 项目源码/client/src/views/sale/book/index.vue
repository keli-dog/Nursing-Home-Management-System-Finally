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
      <template #tableHeader>
        <el-button @click="openDrawer('新增')">
          <IconPark :icon="Plus" class="mr-1"></IconPark>
          <span>新增</span></el-button
        >
      </template>

      <!-- 表格操作 -->
      <template #operation="scope">
        <el-button
          size="small"
          link
          :icon="View"
          @click="checkViewReserve(scope)"
        >
          查看
        </el-button>
        <el-popconfirm
          title="Are you sure to refund this?"
          @confirm="refundReserve(scope)"
          confirm-button-type="warning"
        >
          <template #reference>
            <el-button v-show="scope.row.reserveFlag === '未退款'" size="small" link :icon="Delete">退款</el-button>
          </template>
        </el-popconfirm>
      </template>
    </ProTable>
    <bookDialog ref="DialongRef" />
  </div>
</template>

<script setup lang="ts" name="useProTable">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import bookDialog from "./bookDialog/index.vue";
import { Plus } from "@icon-park/vue-next";
import { ColumnProps } from "@/components/ProTable/interface";
import ProTable from "@/components/ProTable/index.vue";
import { Delete, EditPen, View } from "@element-plus/icons-vue";
import {
  listReserveStaff,
  pageReserveByKey,
  addReserve, refund
} from "@/apis/bookManage";

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
  return pageReserveByKey(newParams);
};

// 查看
const checkViewReserve = (params: any) => {
  openDrawer("查看", params.row);
};

// 退款
const refundReserve = async (params: any) => {
  const res: any = await refund({ reserveId: params.row.reserveId });
  if (res.code === 200) {
    ElMessage.success(res.msg);
    proTable.value.getTableList();
  } else {
    ElMessage.error(res.msg);
  }
};

// 打开 drawer(新增、查看、编辑)
const DialongRef = ref();
const openDrawer = (title: string, rowData: any = {}) => {
  const params = {
    title,
    rowData: { ...rowData },
    isView: title === "查看",
    api: addReserve,
    getTableList: proTable.value.getTableList
  };
  DialongRef.value.acceptParams(params);
};

// 表格配置项
const columns: ColumnProps<any>[] = [
  { prop: "rank", label: "序号", width: 55 },
  { prop: "elderName", label: "老人姓名", search: { el: "input" } },
  { prop: "idNum", label: "身份证号" },
  { prop: "elderSex", label: "性别", width: 60 },
  { prop: "elderAge", label: "年龄", width: 60 },
  { prop: "bedName", label: "预定床位名称" },
  { prop: "deposit", label: "定金（元）" },
  { prop: "payerPhone", label: "交款人电话", search: { el: "input" } },
  { prop: "staffName", label: "销售人员" },
  { prop: "reserveFlag", label: "退款状态" },
  { prop: "checkFlag", label: "入住状态" },
  { prop: "operation", label: "操作", width: 150 }
];
</script>

<style lang="scss" scoped></style>
