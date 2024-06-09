<template>
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
            <el-form-item label="分类名称:" prop="name">
              <el-input v-model="formData.name" clearable />
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
        <el-button
          class="bg-blue"
          type="primary"
          @click="handleSubmit(ruleFormRef)"
        >
          提交
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox, FormInstance, FormRules } from "element-plus";

import {
  listConsultSource,
  listConsultStaff,
  getConsultByForm
} from "@/apis/soldManage";
import {
  phoneRule,
  integerRule,
  stringRule,
  idNumRule
} from "@/utils/formRules";
import { getServiceType } from "@/apis/service";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const ConsultSourceList = ref<{ id: number; name: string | number }[]>([]);
const ConsultStaffList = ref<{ id: number; name: string | number }[]>([]);

const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "分类名称", 2, 10);
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
}

const drawerProps = ref<DialogProps>({
  isView: false,
  title: ""
});
const formData = ref({
  id: "",
  name: ""
});

// 接收父组件传过来的参数
const serviceTypeAcceptParams = async (params: DialogProps) => {
  drawerProps.value = params;
  formData.value = { ...params.rowData, ...params.rowData };
  dialogVisible.value = true;
};

const emit = defineEmits<{
  (event: "getTypeHandle", val: any): void
}>();

//点击提交
const handleSubmit = async (formRef: FormInstance | null) => {
  if (!formRef) return;
  formRef.validate(async (valid, fields) => {
    if (valid) {
      try {
        const res = await drawerProps.value.api!(formData.value);
        if (res.code == 200) {
          ElMessage.success({
            message: res.msg
          });
          emit("getTypeHandle", null);
          dialogVisible.value = false;
        } else {
          ElMessage.error({
            message: res.msg
          });
        }
      } catch (error) {
        console.log(error);
      }
    } else {
      return fields;
    }
  });
};

defineExpose({
  serviceTypeAcceptParams
});
</script>
<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
