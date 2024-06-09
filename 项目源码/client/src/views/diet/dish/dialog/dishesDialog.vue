<template>
  <div>
    <el-dialog
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
              <el-form-item label="菜品名称:" prop="name">
                <el-input v-model="formData.name" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="菜品分类:" prop="typeId">
                <el-select
                  v-model="formData.typeId"
                  placeholder="请选择"
                  class="w-full"
                >
                  <el-option
                    clearable
                    :label="item.name"
                    :value="item.id"
                    v-for="item in DishesTypeList"
                    :key="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="菜品价格（元）:" prop="price">
                <el-input v-model="formData.price" clearable />
              </el-form-item>
            </div>
            <!-- 占位 -->
            <div
              class="border md:w-1/2"
              v-for="i in 2"
              :key="i"
              style="visibility: hidden; height: 0"
            ></div>
          </div>
          <div class="flex flex-row-reverse">
            <div></div>
          </div>
        </el-form>
      </div>
      <template v-if="!drawerProps.isView" #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button class="bg-blue" type="primary" @click="handleSubmit">
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import { ElMessage, ElMessageBox, FormInstance, FormRules } from "element-plus";
import { Plus } from "@element-plus/icons-vue";

import {
  idNumRule,
  integerRule,
  numberRule,
  phoneRule,
  stringRule
} from "@/utils/formRules";

import {
  addReserve,
  getBuildTree, getReserveById, listReserveStaff,
  pageSearchElderByKey
} from "@/apis/bookManage";
import { getServiceById, getServiceType, IChargeMethodList } from "@/apis/service";
import { getDishesById, listDishesType } from "@/apis/dishes";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const DishesTypeList = ref<{ id: number; name: string | number }[]>([]);

const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "菜品名称", 2, 10);
      },
      trigger: "blur"
    }
  ],
  typeId: [{ required: true, message: "请选择菜品分类", trigger: "blur" }],
  price: [
    {
      required: true,
      validator(rule, value, callback) {
        numberRule(rule, value, callback, "菜品价格", 1, 1000000);
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

const formData = ref({
  id: "",
  name: "",
  price: "",
  typeId: ""
});

// 接收父组件传过来的参数
const dishesAcceptParams = async (params: DialogProps) => {
  const [DishesTypeListData, DishesData] =
    await Promise.all([
      listDishesType({ dishesTypeName: "" }),
      getDishesById({
        dishesId: params.rowData.id
      })
    ]);
  DishesTypeList.value = DishesTypeListData.data;
  drawerProps.value = params;
  formData.value = { ...params.rowData, ...DishesData.data };
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
  dishesAcceptParams
});
</script>
<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
