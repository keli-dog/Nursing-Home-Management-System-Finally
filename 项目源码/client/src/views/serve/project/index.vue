<template>
  <div style="display: flex;">
    <div class="bg-white" style="margin-right: 10px;padding: 10px;">
      <el-input v-model="searchTypeName" clearable placeholder="请输入分类名称" />
      <el-scrollbar height="560px" style="margin-top: 10px;">
        <el-radio-group v-model="serviceType" aria-placeholder="请选择" style="width: 250px;"
                        @change="setServiceTypeIdHandle">
          <el-radio v-for="(item,index) in serviceTypeList" :key="index" :label="item">
            {{ item.name }}
          </el-radio>
        </el-radio-group>
      </el-scrollbar>
    </div>
    <div class="table-box" style="flex: 8">
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
          <el-button type="primary" :icon="Plus" @click="openDrawer('serviceType','新增',null)">
            <span>新增分类</span>
          </el-button>
          <el-button type="warning" :icon="EditPen" @click="openDrawer('serviceType','编辑',serviceType)">
            <span>编辑分类</span>
          </el-button>
          <el-button type="danger" :icon="Delete" @click="deleteTypeData(serviceType)">
            <span>删除分类</span>
          </el-button>
          <el-button type="primary" :icon="Plus" @click="openDrawer('service','新增')">
            <span>新增服务</span>
          </el-button>
        </template>

        <!-- 表格操作 -->
        <template #operation="scope">
          <el-button
            size="small"
            link
            :icon="View"
            @click="openDrawer('service','查看',scope)"
          >
            查看
          </el-button>
          <el-button
            size="small"
            link
            :icon="EditPen"
            @click="openDrawer('service','编辑',scope)"
          >
            编辑
          </el-button>
          <el-popconfirm
            title="Are you sure to delete this?"
            @confirm="deleteServiceData(scope)"
            confirm-button-type="danger"
          >
            <template #reference>
              <el-button size="small" link :icon="Delete">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </ProTable>
      <serviceDialog ref="serviceDialogRef" />
      <serviceTypeDialog @getTypeHandle="getTypeHandle" ref="serviceTypeDialogRef" />
    </div>
  </div>
</template>

<script setup lang="ts" name="useProTable">
import { ref, reactive, watch, onMounted } from "vue";
import { ElMessage } from "element-plus";
import serviceDialog from "./dialog/serviceDialog.vue";
import serviceTypeDialog from "./dialog/serviceTypeDialog.vue";
import { ColumnProps } from "@/components/ProTable/interface";
import ProTable from "@/components/ProTable/index.vue";
import { Delete, EditPen, View, Plus } from "@element-plus/icons-vue";
import {
  addService,
  addServiceType, deleteService,
  deleteServiceType,
  editService,
  editServiceType,
  getServiceType,
  pageServiceByKey
} from "@/apis/service";

// 获取 ProTable 元素，调用其获取刷新数据方法（还能获取到当前查询参数，方便导出携带参数）
const proTable = ref();
const initParam = reactive({});
const serviceType = ref<any>({});
const searchTypeName = ref<string>("");
const serviceTypeList = ref<any[]>([]);

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
  if (serviceType.value.id !== -1) {
    params.typeId = serviceType.value.id;
  }
  let newParams = JSON.parse(JSON.stringify(params));
  return pageServiceByKey(newParams);
};

// 重新获取表格数据
const getTableListHandle = (pageNum: number, pageSize: number, serviceName: string, typeId: number) => {
  proTable.value.getTableList({
    pageNum: pageNum,
    pageSize: pageSize,
    name: serviceName,
    typeId: typeId
  });
};

onMounted(async () => {
  await getTypeHandle(null);
});

// 实时获取服务分类数据
watch(searchTypeName, async (value) => {
  await getTypeHandle(value);
});

// 获取分类列表
const getTypeHandle = async (typeName: any) => {
  // 清除选中的服务分类
  serviceType.value = { id: -1, name: "" };
  // 获取分类列表
  const res: any = await getServiceType({
    serviceTypeName: typeName
  });
  serviceTypeList.value = res.data;
};

// 设置搜索分类并更新表格数据
const setServiceTypeIdHandle = async (selectType: any) => {
  serviceType.value = selectType;
  // 重新获取表格数据
  getTableListHandle(1, 10, proTable.value.searchParam.name, selectType.id);
};

// 删除分类
const deleteTypeData = async (params: any) => {
  if (Object.keys(params).length === 0 || params.id === -1) {
    ElMessage.warning("请选择分类");
    return;
  }
  const res: any = await deleteServiceType({
    serviceTypeId: params.id
  });
  if (res.code === 200) {
    ElMessage.success(res.msg);
    await getTypeHandle(null);
  } else {
    ElMessage.error(res.msg);
  }
};

// 删除服务
const deleteServiceData = async (params: any) => {
  const res: any = await deleteService({
    serviceId: params.row.id
  });
  if (res.code === 200) {
    ElMessage.success(res.msg);
    proTable.value.getTableList();
  } else {
    ElMessage.error(res.msg);
  }
};

// 打开服务 drawer(新增、查看、编辑)
const serviceDialogRef = ref();
const serviceTypeDialogRef = ref();
const openDrawer = (type: string, title: string, rowData: any = {}) => {
  if (type === "serviceType") {
    if (title === "编辑" && (Object.keys(rowData).length === 0 || rowData.id === -1)) {
      ElMessage.warning("请选择分类");
      return;
    }
    const params = {
      title,
      rowData: title !== "新增" ? { ...serviceType.value } : {},
      isView: title === "查看",
      api: title === "新增" ? addServiceType : title === "编辑" ? editServiceType : ""
    };
    serviceTypeDialogRef.value.serviceTypeAcceptParams(params);
  } else if (type === "service") {
    const params = {
      title,
      rowData: { ...rowData.row },
      isView: title === "查看",
      api: title === "新增" ? addService : title === "编辑" ? editService : "",
      getTableList: proTable.value.getTableList
    };
    serviceDialogRef.value.serviceAcceptParams(params);
  }
};

// 表格配置项
const columns: ColumnProps<any>[] = [
  { prop: "rank", label: "序号", width: 55 },
  { prop: "name", label: "服务名称", search: { el: "input" } },
  { prop: "chargeMethod", label: "收费方式" },
  { prop: "price", label: "服务费用（元）" },
  { prop: "needDate", label: "服务时长（min）" },
  { prop: "operation", label: "操作", width: 220 }
];
</script>

<style lang="scss" scoped>
:deep(.el-radio__input) {
  display: none !important;
}

:deep(.el-radio__label) {
  font-size: 1em;
  width: 248px;
  padding: 5px 0 5px 10px;
  transition: all 0.2s;

  &:hover {
    background-color: #eeeeee;
  }
}

.el-radio-group {
  display: flex;
  flex-wrap: wrap;

  .el-radio {
    flex: 1;
    margin-right: 100%;
  }
}
</style>
