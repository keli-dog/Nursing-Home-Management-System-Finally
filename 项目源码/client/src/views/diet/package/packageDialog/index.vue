<template>
  <div>
    <el-dialog
      style="width: 70%"
      v-model="dialogVisible"
      :title="drawerProps.title"
      destroy-on-close
    >
      <div>
        <el-form
          :model="formData"
          class="login-form"
          ref="ruleFormRef"
          :rules="rules"
          label-width="120px"
        >
          <div class="flex justify-around flex-wrap">
            <div class="w-full md:w-1/2">
              <el-form-item label="套餐名称:" prop="name">
                <el-input v-model="formData.name" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="月费（元）:" prop="monthPrice">
                <el-input v-model="formData.monthPrice" clearable />
              </el-form-item>
            </div>
            <!-- 占位 -->
            <div
              class="md:w-1/2"
              v-for="i in 1"
              :key="i"
              style="height: 0"
            ></div>
            <div style="width: 100%">
              <el-form-item label="套餐菜品:" prop="dishesIdList">
                <div>
                  <el-form>
                    <div style="position: relative;">
                      <el-button
                        class="bg-blue"
                        type="primary"
                        icon="Plus"
                        circle
                        style="position: absolute;width: 27.6px;height: 27.6px; top: 6.5px; left: 1.7%;z-index: 1000"
                        @click="openDrawer"
                      />
                    </div>
                    <el-table :data="formData.setDishesVoList" border>
                      <el-table-column width="60px">
                        <template v-slot="scope">
                          <el-button class="bg-red-600" type="danger" :icon="Delete" circle
                                     @click="deleteDishes(scope.$index)" />
                        </template>
                      </el-table-column>
                      <el-table-column label="菜品名称" align="center">
                        <template v-slot="scope">
                          <el-input v-model="scope.row.name" disabled></el-input>
                        </template>
                      </el-table-column>
                      <el-table-column label="菜品价格（元）" align="center">
                        <template v-slot="scope">
                          <el-input v-model="scope.row.price" disabled></el-input>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-form>
                </div>
              </el-form-item>
            </div>
          </div>
          <div class="flex flex-row-reverse">
            <div></div>
          </div>
        </el-form>
      </div>
      <template v-if="!drawerProps.isView" #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          class="bg-blue"
          type="primary"
          @click="handleSubmit"
        >
          提交
        </el-button>
      </span>
      </template>
    </el-dialog>
    <dishesListDialog @addDishes="addDishes" ref="DialogRef" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import dishesListDialog from "./dishesListDialog.vue";
import { ElMessage, FormInstance, FormRules } from "element-plus";
import { Delete } from "@element-plus/icons-vue";
import {
  integerRule,
  numberRule,
  stringRule
} from "@/utils/formRules";
import { getCateringSetById } from "@/apis/cateringSet";
import {
  arrayDeduplicationByFiled,
  arrayDeduplicationByObj,
  arrayPushAllByFiled,
  initArray
} from "@/utils/commonUtil";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);

const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "护理等级名称", 2, 10);
      },
      trigger: "blur"
    }
  ],
  monthPrice: [
    {
      required: true,
      validator(rule, value, callback) {
        numberRule(rule, value, callback, "月费", 1, 1000000);
      },
      trigger: "blur"
    }
  ],
  dishesIdList: [
    {
      required: true,
      validator(rule, value, callback) {
        if (!value || value.length < 1) {
          callback("至少添加一个菜品");
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ]
});

interface DialogProps {
  title: string;
  isView: boolean;
  rowData?: any;
  api?: (params: any) => Promise<any>;
  getTableList?: () => Promise<any>;
}

const drawerProps = ref<DialogProps>({
  isView: false,
  title: ""
});

const formData = ref<any>({
  id: "",
  name: "",
  monthPrice: "",
  orderDishesList: [],
  setDishesVoList: []
});

// 添加菜品
const addDishes = (dishesList: any) => {
  // 初始化Array
  formData.value.dishesIdList = initArray(formData.value.dishesIdList);
  formData.value.setDishesVoList = initArray(formData.value.setDishesVoList);

  // 传入所有对象
  dishesList.value.forEach((dishes: any) => {
    dishes.name = dishes.typeName;
    formData.value.setDishesVoList.push(dishes);
  });

  // 去重
  formData.value.setDishesVoList = arrayDeduplicationByFiled(formData.value.setDishesVoList, "id");

  // 菜品编号列表
  formData.value.dishesIdList = arrayPushAllByFiled(formData.value.setDishesVoList, formData.value.dishesIdList, "id");
};

// 删除菜品
const deleteDishes = (index: number) => {
  // 菜品列表
  formData.value.setDishesVoList.splice(index, 1);
  // 菜品编号列表
  formData.value.dishesIdList = [];
  formData.value.dishesIdList = arrayPushAllByFiled(formData.value.setDishesVoList, formData.value.dishesIdList, "id");
};

// 打开 drawer(新增、查看、编辑)
const DialogRef = ref();
const openDrawer = () => {
  DialogRef.value.acceptParams();
};

// 接收父组件传过来的参数
const acceptParams = async (params: DialogProps) => {
  drawerProps.value = params;
  const [echoData] = await Promise.all([
    getCateringSetById({
      setId: params.rowData.id
    })
  ]);
  formData.value = { ...params.rowData, ...echoData.data };
  dialogVisible.value = true;
};

//点击提交
const handleSubmit = () => {
  ruleFormRef.value!.validate(async valid => {
    if (!valid) return;
    try {
      const res = await drawerProps.value.api!(formData.value);
      if (res.code == 200) {
        ElMessage.success({
          message: res.msg
        });
        drawerProps.value.getTableList!();
        dialogVisible.value = false;
      } else {
        ElMessage.error({
          message: res.msg
        });
      }
    } catch (error) {
      console.log(error);
    }
  });
};

defineExpose({
  acceptParams
});
</script>
<style scoped lang="scss">
.dialog-footer button:first-child {
  margin-right: 10px;
}

:deep(.el-form-item__content) {
  display: block;
}
</style>
