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
          <div class="flex justify-around flex-wrap" v-if="drawerProps.title === '新增'">
            <div class="w-full md:w-1/2">
              <el-form-item label="老人姓名:" prop="elderId">
                <el-input v-model="formData.elderName" clearable disabled>
                  <template #append>
                    <el-button
                      @click="openElderDrawer"
                      :icon="Plus"
                      :style="{cursor: (drawerProps.title !== '新增' ? not-allowed : cursor)}"
                      :disabled="drawerProps.title !== '新增'"
                    />
                  </template>
                </el-input>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="就餐方式:" prop="dineType">
                <el-select
                  v-model="formData.dineType"
                  placeholder="请选择"
                  class="w-full"
                >
                  <el-option
                    clearable
                    :label="item.name"
                    :value="item.value"
                    v-for="item in DineTypeList"
                    :key="item.value"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="就餐时间:" prop="dineDate">
                <el-date-picker
                  class="w-full"
                  type="datetime"
                  v-model="formData.dineDate"
                  unlink-panels
                  placeholder="请选择"
                  :disabled-date="disabledNowBeforeDate"
                />
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
              <el-form-item label="套餐菜品:" prop="orderDishesList">
                <div>
                  <el-form>
                    <div style="position: relative;">
                      <el-button
                        class="bg-blue"
                        type="primary"
                        icon="Plus"
                        circle
                        style="position: absolute;width: 27.6px;height: 27.6px; top: 6.5px; left: 1.7%;z-index: 1000"
                        @click="openDishesDrawer"
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
                          <el-input v-model="scope.row.dishesName" disabled></el-input>
                        </template>
                      </el-table-column>
                      <el-table-column label="菜品价格（元）" align="center">
                        <template v-slot="scope">
                          <el-input v-model="scope.row.price" disabled></el-input>
                        </template>
                      </el-table-column>
                      <el-table-column label="菜品份数" align="center">
                        <template v-slot="scope">
                          <el-input-number v-model="scope.row.orderNum" :min="1" :max="10" :step="1" step-strictly />
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-form>
                </div>
              </el-form-item>
            </div>
          </div>
          <div class="flex justify-around flex-wrap" v-else>
            <div class="w-full md:w-1/2">
              <el-form-item label="护理人员:" prop="staffId">
                <el-select
                  v-model="formData.staffId"
                  placeholder="请选择"
                  class="w-full"
                >
                  <el-option
                    clearable
                    :label="item.name"
                    :value="item.id"
                    v-for="item in StaffList"
                    :key="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="送餐时间:" prop="deliverDishesDate">
                <el-date-picker
                  class="w-full"
                  type="datetime"
                  v-model="formData.deliverDishesDate"
                  unlink-panels
                  placeholder="请选择"
                  :disabled-date="disabledNowBeforeDate"
                />
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
    <elderListDialog @getCheckElderInfo="getCheckElderInfo" ref="ElderDialogRef" />
    <dishesListDialog @addDishes="addDishes" ref="DishesDialogRef" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import elderListDialog from "@/components/elderListDialog/index.vue";
import dishesListDialog from "@/views/diet/package/packageDialog/dishesListDialog.vue";
import { ElMessage, FormInstance, FormRules } from "element-plus";
import { Plus, Delete } from "@element-plus/icons-vue";
import { IDineTypeList, listNurseStaff, pageSearchElderByKey } from "@/apis/order";
import { arrayDeduplicationByFiled, arrayPushAllByFiled, arrayPushAllByObj, initArray } from "@/utils/commonUtil";
import {
  disabledNowBeforeDate,
  disabledNowAfterDate
} from "@/utils/formRules"

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const DineTypeList = IDineTypeList;
const StaffList = ref<any>([]);

const rules = reactive<FormRules>({
  elderId: [{ required: true, message: "请选择点餐老人", trigger: "blur" }],
  dineType: [{ required: true, message: "请选择就餐方式", trigger: "blur" }],
  dineDate: [{ required: true, message: "请选择就餐时间", trigger: "blur" }],
  orderDishesList: [
    {
      required: true,
      validator(rule, value, callback) {
        const list: any = formData.value.setDishesVoList;
        if (!list || list.length < 1) {
          callback("至少添加一个菜品");
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ],
  staffId: [{ required: true, message: "请选择护理人员", trigger: "blur" }],
  deliverDishesDate: [{ required: true, message: "请选择送餐时间", trigger: "blur" }]
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
  elderId: "",
  elderName: "",
  dineType: "",
  dineDate: "",
  deliverDishesDate: "",
  staffId: "",
  orderDishesList: [{
    dishesId: "",
    orderNum: ""
  }],
  setDishesVoList: []
});

// 打开选择老人的列表 drawer
const ElderDialogRef = ref();
const DishesDialogRef = ref();
const openElderDrawer = () => {
  const params = {
    elderApi: pageSearchElderByKey
  };
  ElderDialogRef.value.elderAcceptParams(params);
};

// 获取选择老人的信息
const getCheckElderInfo = (row: any) => {
  formData.value.elderId = row.id;
  formData.value.elderName = row.name;
};

// 打开选择菜品的列表 drawer
const openDishesDrawer = () => {
  DishesDialogRef.value.acceptParams();
};

// 添加菜品
const addDishes = (dishesList: any) => {
  // 初始化Array
  formData.value.orderDishesList = initArray(formData.value.orderDishesList);
  formData.value.setDishesVoList = initArray(formData.value.setDishesVoList);

  // 传入所有对象
  dishesList.value.forEach((dishes: any) => {
    dishes.orderNum = dishes.orderNum ? dishes.orderNum : 1;
    formData.value.setDishesVoList.push(dishes);
  });

  // 去重
  formData.value.setDishesVoList = arrayDeduplicationByFiled(formData.value.setDishesVoList, "id");

  // 菜品编号列表
  // formData.value.orderDishesList = arrayPushAllByFiled(formData.value.setDishesVoList, formData.value.orderDishesList, "id");
};

// 删除菜品
const deleteDishes = (index: number) => {
  // 菜品列表
  formData.value.setDishesVoList.splice(index, 1);
  // 菜品编号列表
  // formData.value.orderDishesList = [];
  // formData.value.orderDishesList = arrayPushAllByFiled(formData.value.setDishesVoList, formData.value.orderDishesList, "id");
};

// 接收父组件传过来的参数
const acceptParams = async (params: DialogProps) => {
  if (params.title === "送餐") {
    formData.value.id = params.rowData.id;
    const StaffListData = await listNurseStaff();
    StaffList.value = StaffListData.data;
  }
  drawerProps.value = params;
  dialogVisible.value = true;
};

//点击提交
const handleSubmit = () => {
  ruleFormRef.value!.validate(async valid => {
    if (!valid) return;
    formData.value.orderDishesList = [];
    formData.value.setDishesVoList.forEach((item: any) => {
      formData.value.orderDishesList.push({
        dishesId: item.id,
        orderNum: item.orderNum
      });
    });
    formData.value.setDishesVoList = [];
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
