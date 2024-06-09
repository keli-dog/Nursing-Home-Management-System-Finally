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
              <el-form-item label="陪同人类型:" prop="chaperoneType">
                <el-select
                  v-model="formData.chaperoneType"
                  placeholder="请选择"
                  class="w-full"
                  @change="selectChangeHandle"
                >
                  <el-option
                    clearable
                    :label="item.label"
                    :value="item.value"
                    v-for="item in selectTypeList"
                    :key="item.value"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="陪同人:" prop="chaperoneName">
                <el-select
                  v-model="formData.chaperoneName"
                  placeholder="请选择"
                  class="w-full"
                  @change="setChaperoneData"
                >
                  <el-option
                    clearable
                    :label="item.name"
                    :value="item"
                    v-for="item in chaperoneList"
                    :key="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="陪同人电话:" prop="chaperonePhone">
                <el-input v-model="formData.chaperonePhone" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="外出时间:" prop="outwardDate">
                <el-date-picker
                  class="w-full"
                  type="datetime"
                  v-model="formData.outwardDate"
                  unlink-panels
                  placeholder="请选择"
                  :disabled-date="disabledNowAfterDate"
                  :disabled="drawerProps.title === '延期返回'"
                />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="计划返回时间:" prop="planReturnDate">
                <el-date-picker
                  class="w-full"
                  type="datetime"
                  v-model="formData.planReturnDate"
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
import { onMounted, reactive, ref, toRefs } from "vue";
import elderListDialog from "@/components/elderListDialog/index.vue";
import { ElMessage, ElMessageBox, FormInstance, FormRules } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import {
  phoneRule,
  disabledNowBeforeDate,
  disabledNowAfterDate
} from "@/utils/formRules";
import { getOutwardById, listContactByElderId, listOutwardStaff, pageSearchElderByKey, typeList } from "@/apis/outward";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const selectTypeList = typeList;
const chaperoneList = ref<any[]>([]);

const rules = reactive<FormRules>({
  elderId: [{ required: true, message: "请选择外出老人", trigger: "blur" }],
  chaperoneName: [{ required: true, message: "请选择陪同人", trigger: "blur" }],
  chaperonePhone: [
    {
      required: true,
      validator(rule, value, callback) {
        phoneRule(rule, value, callback, "陪同人");
      },
      trigger: "blur"
    }
  ],
  chaperoneType: [{ required: true, message: "请选择陪同人类型", trigger: "blur" }],
  outwardDate: [{ required: true, message: "请选择外出时间", trigger: "blur" }],
  planReturnDate: [{ required: true, message: "请选择计划返回时间", trigger: "blur" }]
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
  elderId: "",
  elderName: "",
  chaperoneName: "",
  chaperonePhone: "",
  chaperoneType: "",
  outwardDate: "",
  planReturnDate: ""
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
  getChaperoneList(formData.value.chaperoneType, true);
};

// 获取陪同人列表
const selectChangeHandle = (type: any) => {
  getChaperoneList(type, true);
};

const getChaperoneList = async (type: any, resetFlag: boolean) => {
  if (resetFlag) {
    formData.value.chaperoneName = "";
    formData.value.chaperonePhone = "";
  }
  const res = ref<any>();
  if (type === "护工") {
    res.value = await listOutwardStaff();
  } else if (type === "家属") {
    res.value = await listContactByElderId({
      elderId: formData.value.elderId
    });
  }
  chaperoneList.value = res.value.data;
};

const setChaperoneData = (data: any) => {
  formData.value.chaperoneName = data.name;
  formData.value.chaperonePhone = data.phone;
};

// 接收父组件传过来的参数
const acceptParams = async (params: DialogProps) => {
  const [echoData] =
    await Promise.all([
      getOutwardById({
        outwardId: params.rowData.id
      })
    ]);
  drawerProps.value = params;
  formData.value = { ...params.rowData, ...echoData.data };
  if (drawerProps.value.title !== "新增") {
    formData.value.elderId = '0'
    await getChaperoneList(formData.value.chaperoneType, false);
  }
  dialogVisible.value = true;
};

//点击提交
const handleSubmit = () => {
  ruleFormRef.value!.validate(async valid => {
    if (!valid) return;
    try {
      const reqData = drawerProps.value.title === "新增" ?
        formData.value :
        {
          id: drawerProps.value.rowData.id,
          planReturnDate: formData.value.planReturnDate
        };
      const res = await drawerProps.value.api!(reqData);
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
