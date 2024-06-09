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
                      @click="openDrawer"
                      :icon="Plus"
                      :style="{cursor: (drawerProps.title !== '新增' ? not-allowed : cursor)}"
                      :disabled="drawerProps.title !== '新增'"
                    />
                  </template>
                </el-input>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="护理项目:" prop="serviceId">
                <el-select
                  v-model="formData.serviceId"
                  placeholder="请选择"
                  class="w-full"
                  @change="selectChangeHandle"
                >
                  <el-option
                    clearable
                    :label="item.name"
                    :value="item.id"
                    v-for="item in ServiceList"
                    :key="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="服务时长:" prop="needDate">
                <el-input v-model="formData.needDate" clearable disabled />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="服务单价:" prop="servicePrice">
                <el-input v-model="formData.servicePrice" clearable disabled />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="收费方式:" prop="chargeMethod">
                <el-input v-model="formData.chargeMethod" clearable disabled />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="服务次数:" prop="frequency">
                <el-input v-model="formData.frequency" clearable />
              </el-form-item>
            </div>
            <!-- 占位 -->
            <div
              class="md:w-1/2"
              v-for="i in 1"
              :key="i"
              style="height: 0"
            ></div>
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
              <el-form-item label="执行时间:" prop="nurseDate">
                <el-date-picker
                  class="w-full"
                  type="datetime"
                  v-model="formData.nurseDate"
                  unlink-panels
                  placeholder="请选择"
                  :disabled-date="disabledNowAfterDate"
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
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import elderListDialog from "@/components/elderListDialog/index.vue";
import { ElMessage, FormInstance, FormRules } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { integerRule, disabledNowAfterDate } from "@/utils/formRules";
import { getServiceById, listNurseStaff, listService, pageSearchElderByKey } from "@/apis/nurseReserve";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const ServiceList = ref<any>([]);
const StaffList = ref<any>([]);

const rules = reactive<FormRules>({
  elderId: [{ required: true, message: "请选择预定老人", trigger: "blur" }],
  serviceId: [{ required: true, message: "请选择护理项目", trigger: "blur" }],
  frequency: [
    {
      required: true,
      validator(rule, value, callback) {
        integerRule(rule, value, callback, "服务次数", 1, 5);
      },
      trigger: "blur"
    }
  ],
  staffId: [{ required: true, message: "请选择护理人员", trigger: "blur" }],
  nurseDate: [{ required: true, message: "请选择服务时间", trigger: "blur" }]
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
  serviceId: "",
  elderName: "",
  serviceName: "",
  needDate: "",
  servicePrice: "",
  chargeMethod: "",
  frequency: "",
  payAmount: "",
  staffId: "",
  nurseDate: ""
});

// 打开选择老人的列表 drawer
const ElderDialogRef = ref();
const openDrawer = () => {
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

// 选择服务项目后
const selectChangeHandle = async (value: any) => {
  const res = await getServiceById({
    serviceId: value
  });
  formData.value.serviceName = res.data.name;
  formData.value.needDate = res.data.needDate;
  formData.value.servicePrice = res.data.price;
  formData.value.chargeMethod = res.data.chargeMethod;
};

// 接收父组件传过来的参数
const acceptParams = async (params: DialogProps) => {
  if (params.title === "新增") {
    const ServiceListData = await listService();
    ServiceList.value = ServiceListData.data;
  } else if (params.title === "执行") {
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
    try {
      if (drawerProps.value.title === "新增") {
        formData.value.payAmount = String(Number(formData.value.servicePrice) * Number(formData.value.frequency));
      }
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
</style>
