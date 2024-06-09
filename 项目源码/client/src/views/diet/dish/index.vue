<template>
  <div style="display: flex;">
    <div class="bg-white" style="margin-right: 10px;padding: 10px;">
      <el-input v-model="searchTypeName" clearable placeholder="请输入分类名称" />
      <el-scrollbar height="560px" style="margin-top: 10px;">
        <el-radio-group v-model="dishesType" aria-placeholder="请选择" style="width: 250px;"
                        @change="setDishesTypeIdHandle">
          <el-radio v-for="(item,index) in dishesTypeList" :key="index" :label="item">
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
          <el-button type="primary" :icon="Plus" @click="openDrawer('dishesType','新增',null)">
            <span>新增分类</span>
          </el-button>
          <el-button type="warning" :icon="EditPen" @click="openDrawer('dishesType','编辑',dishesType)">
            <span>编辑分类</span>
          </el-button>
          <el-button type="danger" :icon="Delete" @click="deleteTypeData(dishesType)">
            <span>删除分类</span>
          </el-button>
          <el-button type="primary" :icon="Plus" @click="openDrawer('dishes','新增')">
            <span>新增菜品</span>
          </el-button>
        </template>

        <!-- 表格操作 -->
        <template #operation="scope">
          <el-button
            size="small"
            link
            :icon="View"
            @click="openDrawer('dishes','查看',scope)"
          >
            查看
          </el-button>
          <el-button
            size="small"
            link
            :icon="EditPen"
            @click="openDrawer('dishes','编辑',scope)"
          >
            编辑
          </el-button>
          <el-popconfirm
            title="Are you sure to delete this?"
            @confirm="deleteDishesData(scope)"
            confirm-button-type="danger"
          >
            <template #reference>
              <el-button size="small" link :icon="Delete">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </ProTable>
      <dishesDialog ref="dishesDialogRef" />
      <dishesTypeDialog @getTypeHandle="getTypeHandle" ref="dishesTypeDialogRef" />
    </div>
  </div>
</template>

<script setup lang="ts" name="useProTable">
import { ref, reactive, watch, onMounted } from "vue";
import { ElMessage } from "element-plus";
import dishesDialog from "./dialog/dishesDialog.vue";
import dishesTypeDialog from "./dialog/dishesTypeDialog.vue";
import { ColumnProps } from "@/components/ProTable/interface";
import ProTable from "@/components/ProTable/index.vue";
import { Delete, EditPen, View, Plus } from "@element-plus/icons-vue";
import { addDishes, addDishesType, deleteDishes,
  deleteDishesType, editDishes, editDishesType, listDishesType, pageDishesByKey } from "@/apis/dishes";

// 获取 ProTable 元素，调用其获取刷新数据方法（还能获取到当前查询参数，方便导出携带参数）
const proTable = ref();
const initParam = reactive({});
const dishesType = ref<any>({});
const searchTypeName = ref<string>("");
const dishesTypeList = ref<any[]>([]);

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
  if (dishesType.value.id !== -1) {
    params.typeId = dishesType.value.id;
  }
  let newParams = JSON.parse(JSON.stringify(params));
  return pageDishesByKey(newParams);
};

// 重新获取表格数据
const getTableListHandle = (pageNum: number, pageSize: number, dishesName: string, typeId: number) => {
  proTable.value.getTableList({
    pageNum: pageNum,
    pageSize: pageSize,
    name: dishesName,
    typeId: typeId
  });
};

onMounted(async () => {
  await getTypeHandle(null);
});

// 实时获取菜品分类数据
watch(searchTypeName, async (value) => {
  await getTypeHandle(value);
});

// 获取分类列表
const getTypeHandle = async (typeName: any) => {
  // 清除选中的菜品分类
  dishesType.value = { id: -1, name: "" };
  // 获取分类列表
  const res: any = await listDishesType({
    dishesTypeName: typeName
  });
  dishesTypeList.value = res.data;
};

// 设置搜索分类并更新表格数据
const setDishesTypeIdHandle = async (selectType: any) => {
  dishesType.value = selectType;
  // 重新获取表格数据
  getTableListHandle(1, 10, proTable.value.searchParam.name, selectType.id);
};

// 删除分类
const deleteTypeData = async (params: any) => {
  if (Object.keys(params).length === 0 || params.id === -1) {
    ElMessage.warning("请选择分类");
    return;
  }
  const res: any = await deleteDishesType({
    dishesTypeId: params.id
  });
  if (res.code === 200) {
    ElMessage.success(res.msg);
    await getTypeHandle(null);
  } else {
    ElMessage.error(res.msg);
  }
};

// 删除菜品
const deleteDishesData = async (params: any) => {
  const res: any = await deleteDishes({
    dishesId: params.row.id
  });
  if (res.code === 200) {
    ElMessage.success(res.msg);
    proTable.value.getTableList();
  } else {
    ElMessage.error(res.msg);
  }
};

// 打开菜品 drawer(新增、查看、编辑)
const dishesDialogRef = ref();
const dishesTypeDialogRef = ref();
const openDrawer = (type: string, title: string, rowData: any = {}) => {
  if (type === "dishesType") {
    if (title === "编辑" && (Object.keys(rowData).length === 0 || rowData.id === -1)) {
      ElMessage.warning("请选择分类");
      return;
    }
    const params = {
      title,
      rowData: title !== "新增" ? { ...dishesType.value } : {},
      isView: title === "查看",
      api: title === "新增" ? addDishesType : title === "编辑" ? editDishesType : ""
    };
    dishesTypeDialogRef.value.dishesTypeAcceptParams(params);
  } else if (type === "dishes") {
    const params = {
      title,
      rowData: { ...rowData.row },
      isView: title === "查看",
      api: title === "新增" ? addDishes : title === "编辑" ? editDishes : "",
      getTableList: proTable.value.getTableList
    };
    dishesDialogRef.value.dishesAcceptParams(params);
  }
};

// 表格配置项
const columns: ColumnProps<any>[] = [
  { prop: "rank", label: "序号", width: 55 },
  { prop: "dishesName", label: "菜品名称", search: { el: "input" } },
  { prop: "typeName", label: "类别名称" },
  { prop: "price", label: "菜品价格（元）" },
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
