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
          @click="checkViewConsult(scope)"
        >查看
        </el-button
        >
        <el-button
          size="small"
          link
          :icon="EditPen"
          @click="checkeditConsult(scope)"
        >
          编辑
        </el-button>
        <el-popconfirm
          title="Are you sure to delete this?"
          @confirm="deleteConsult(scope)"
          confirm-button-type="danger"
        >
          <template #reference>
            <el-button size="small" link :icon="Delete">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </ProTable>
    <counselDialong ref="DialongRef" />
  </div>
</template>

<script setup lang="ts" name="useProTable">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import counselDialong from "./counselDialog/index.vue";
import { Plus } from "@icon-park/vue-next";
import { ColumnProps } from "@/components/ProTable/interface";
import ProTable from "@/components/ProTable/index.vue";
import { Delete, EditPen, View } from "@element-plus/icons-vue";
import {
  pageConsultByKey,
  addConsult,
  editConsult,
  listConsultSource,
  listConsultStaff,
  delConsult
} from "@/apis/soldManage";
import { TableColumnCtx } from "element-plus/es/components/table/src/table-column/defaults";

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
  return pageConsultByKey(newParams);
};

// 删除咨询
const deleteConsult = async (params: any) => {
  const res: any = await delConsult(params.row.elderId);
  if (res.code === 200 && res.msg === "操作成功") {
    ElMessage.success("删除成功");
    proTable.value.getTableList();
  } else {
    ElMessage.error("删除失败");
  }
};

// 编辑咨询
const checkeditConsult = (params: any) => {
  openDrawer("编辑", params.row);
};
//  点击查看
const checkViewConsult = (params: any) => {
  openDrawer("查看", params.row);
};

// 打开 drawer(新增、查看、编辑)
const DialongRef = ref();
const openDrawer = (title: string, rowData: any = {}) => {
  const params = {
    title,
    rowData: { ...rowData },
    isView: title === "查看",
    api: title === "新增" ? addConsult : title === "编辑" ? editConsult : "",
    getTableList: proTable.value.getTableList
  };
  DialongRef.value.acceptParams(params);
};

// 表格配置项
const columns: ColumnProps<any>[] = [
  { prop: "rank", label: "序号", width: 55 },
  {
    prop: "consultName",
    label: "咨询人姓名",
    search: { el: "input" }
  },
  {
    prop: "consultPhone",
    label: "联系电话",
    search: { el: "input" }
  },
  {
    prop: "elderName",
    label: "老人姓名",
    search: { el: "input" }
  },
  { prop: "elderPhone", label: "老人联系电话", search: { el: "input" } },
  { prop: "sex", label: "性别", width: 60 },
  { prop: "age", label: "年龄", width: 60 },
  {
    prop: "consultDate",
    label: "咨询日期",
    search: {
      el: "date-picker",
      props: { type: "datetimerange", valueFormat: "YYYY-MM-DD HH:mm:ss" }
    }
  },
  {
    prop: "sourceName",
    label: "媒体渠道",
    enum: listConsultSource,
    search: {
      el: "select",
      props: { filterable: false }
    },
    isFilterEnum: false,
    fieldNames: { label: "name", value: "id" }
  },
  {
    enum: listConsultStaff,
    prop: "staffName",
    label: "接待人",
    search: { el: "select", props: { filterable: true } },
    isFilterEnum: false,
    fieldNames: { label: "name", value: "id" }
  },

  { prop: "operation", label: "操作", width: 220 }
];
</script>
