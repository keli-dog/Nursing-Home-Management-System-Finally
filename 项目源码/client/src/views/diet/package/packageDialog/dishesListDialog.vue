<template>
  <div>
    <el-dialog
      v-model="dialogVisible"
      destroy-on-close
      style="width: 70%"
    >
      <ProTable
        ref="proTable"
        title="用户列表"
        :columns="columns"
        :requestApi="getTableList"
        :initParam="initParam"
        :dataCallback="dataCallback"
        @selection-change="operateDishes"
      >
        <!-- 表格 header 按钮 -->
        <template #tableHeader>
          <el-button type="primary" @click="confirmData">
            <span>添加</span>
          </el-button>
        </template>
      </ProTable>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import { ColumnProps } from "@/components/ProTable/interface";
import { pageDishesByKey } from "@/apis/cateringSet";

const dialogVisible = ref(false);
// 获取 ProTable 元素，调用其获取刷新数据方法（还能获取到当前查询参数，方便导出携带参数）
const proTable = ref();
const initParam = reactive({});
const dishesList = ref<any>([]);

// 获取选择的数据
const operateDishes = (selection: any) => {
  dishesList.value = selection;
};

// 确认数据
const emit = defineEmits<{
  (event: "addDishes", val: any): void
}>();
const confirmData = () => {
  emit("addDishes", dishesList);
  dialogVisible.value = false;
};

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
  return pageDishesByKey(newParams);
};

// 表格配置项
const columns: ColumnProps<any>[] = [
  { prop: "operation", type: "selection", width: 55 },
  { prop: "dishesName", label: "菜品名称", search: { el: "input" } },
  { prop: "price", label: "菜品价格（元）" },
  { prop: "typeName", label: "菜品类别" }
];

interface DialogProps {
  isView: boolean;
}

const drawerProps = ref<DialogProps>({
  isView: false
});

// 父组件须调用子组件的方法
const acceptParams = async () => {
  dialogVisible.value = true;
};

defineExpose({
  acceptParams
});
</script>
<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
