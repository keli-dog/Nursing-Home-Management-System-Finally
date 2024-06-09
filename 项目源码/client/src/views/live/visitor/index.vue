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
          @click="openDrawer('查看',scope)"
        >
          查看
        </el-button>
        <el-button
          size="small"
          link
          :icon="EditPen"
          @click="openDrawer('编辑',scope)"
          v-show="scope.row.leaveDate === null"
        >
          编辑
        </el-button>
        <el-popconfirm
          title="Are you sure to Leave this?"
          @confirm="recordLeaveHandle(scope)"
          confirm-button-type="warning"
        >
          <template #reference>
            <el-button v-show="scope.row.leaveDate === null" size="small" link :icon="EditPen">登记离开</el-button>
          </template>
        </el-popconfirm>
        <el-popconfirm
          title="Are you sure to delete this?"
          @confirm="deleteData(scope)"
          confirm-button-type="danger"
        >
          <template #reference>
            <el-button size="small" link :icon="Delete">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </ProTable>
    <visitDialog ref="DialogRef" />
  </div>
</template>

<script setup lang="ts" name="useProTable">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import visitDialog from "./visitDialog/index.vue";
import { Plus } from "@icon-park/vue-next";
import { ColumnProps } from "@/components/ProTable/interface";
import ProTable from "@/components/ProTable/index.vue";
import { Delete, EditPen, View } from "@element-plus/icons-vue";
import { addVisit, deleteVisit, editVisit, pageVisitByKey, recordLeave, typeList } from "@/apis/visit";

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
  return pageVisitByKey(newParams);
};

// 删除
const deleteData = async (params: any) => {
  const res: any = await deleteVisit({
    visitId: params.row.id
  });
  responseHint(res);
};

// 登记离开
const recordLeaveHandle = async (params: any) => {
  const res: any = await recordLeave({
    id: params.row.id,
    leaveDate: new Date()
  });
  responseHint(res);
};

// 响应提示
const responseHint = (res: any) => {
  if (res.code === 200) {
    ElMessage.success(res.msg);
    proTable.value.getTableList();
  } else {
    ElMessage.error(res.msg);
  }
};

// 打开 drawer(新增、查看、编辑)
const DialogRef = ref();
const openDrawer = (title: string, rowData: any = {}) => {
  const params = {
    title,
    rowData: { ...rowData.row },
    isView: title === "查看",
    api: title === "新增" ? addVisit : title === "编辑" ? editVisit : "",
    getTableList: proTable.value.getTableList
  };
  DialogRef.value.acceptParams(params);
};

// 表格配置项
const columns: ColumnProps<any>[] = [
  { prop: "rank", label: "序号", width: 55 },
  { prop: "elderName", label: "老人姓名", search: { el: "input" } },
  { prop: "visitName", label: "来访者姓名", search: { el: "input" } },
  { prop: "visitPhone", label: "来访者电话", search: { el: "input" } },
  { prop: "relation", label: "与老人关系" },
  {
    enum: typeList,
    prop: "visitFlag",
    label: "来访状态",
    search: { el: "select", props: { filterable: true } }
  },
  { prop: "visitDate", label: "来访时间" },
  { prop: "leaveDate", label: "离开时间" },
  { prop: "visitNum", label: "来访人数" },
  { prop: "operation", label: "操作", width: 220 }
];
</script>

<style lang="scss" scoped></style>
