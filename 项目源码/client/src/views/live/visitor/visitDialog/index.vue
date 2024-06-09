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
              <el-form-item label="来访者姓名:" prop="name">
                <el-input v-model="formData.name" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="来访者电话:" prop="phone">
                <el-input v-model="formData.phone" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="与老人关系:" prop="relation">
                <el-input v-model="formData.relation" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="来访人数:" prop="visitNum">
                <el-input v-model="formData.visitNum" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="来访时间:" prop="visitDate">
                <el-date-picker
                  class="w-full"
                  type="datetime"
                  v-model="formData.visitDate"
                  unlink-panels
                  placeholder="请选择"
                  :disabled-date="disabledNowAfterDate"
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
  integerRule,
  phoneRule,
  stringRule,
  disabledNowAfterDate
} from "@/utils/formRules";
import { pageSearchElderByKey, getVisitById, typeList } from "@/apis/visit";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const selectTypeList = typeList;

const rules = reactive<FormRules>({
  elderId: [{ required: true, message: "请选择来访老人", trigger: "blur" }],
  name: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "来访者姓名", 2, 10);
      },
      trigger: "blur"
    }
  ],
  phone: [
    {
      required: true,
      validator(rule, value, callback) {
        phoneRule(rule, value, callback, "来访者");
      },
      trigger: "blur"
    }
  ],
  relation: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "与老人关系", 2, 5);
      },
      trigger: "blur"
    }
  ],
  visitNum: [
    {
      required: true,
      validator(rule, value, callback) {
        integerRule(rule, value, callback, "来访人数", 1, 100);
      },
      trigger: "blur"
    }
  ],
  visitDate: [{ required: true, message: "请选择来访时间", trigger: "blur" }]
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
  name: "",
  visitName: "",
  phone: "",
  visitPhone: "",
  relation: "",
  visitDate: "",
  visitNum: ""
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

// 接收父组件传过来的参数
const acceptParams = async (params: DialogProps) => {
  const [echoData] =
    await Promise.all([
      getVisitById({
        visitId: params.rowData.id
      })
    ]);
  drawerProps.value = params;
  formData.value = { ...params.rowData, ...echoData.data };
  if (drawerProps.value.title !== "新增") {
    formData.value.elderId = "0";
    formData.value.name = formData.value.visitName;
    formData.value.phone = formData.value.visitPhone;
  }
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
</style>
