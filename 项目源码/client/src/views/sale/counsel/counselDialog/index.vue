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
            <el-form-item label="咨询人姓名:" prop="consultName">
              <el-input v-model="formData.consultName" clearable />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="咨询人电话:" prop="consultPhone">
              <el-input v-model="formData.consultPhone" clearable />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="老人姓名:" prop="elderName">
              <el-input v-model="formData.elderName" clearable />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="老人电话:" prop="elderPhone">
              <el-input v-model="formData.elderPhone" clearable />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="老人身份证:" prop="idNum">
              <el-input v-model="formData.idNum" clearable />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="老人性别:" prop="sex">
              <el-select
                v-model="formData.sex"
                placeholder="请选择"
                class="w-full"
              >
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="老人年龄:" prop="age">
              <el-input v-model="formData.age" clearable />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="与老人关系:" prop="relation">
              <el-input v-model="formData.relation" clearable />
            </el-form-item>
          </div>
          <!-- 咨询时间 -->
          <div class="w-full md:w-1/2">
            <el-form-item label="咨询日期:" prop="consultDate">
              <el-date-picker
                class="w-full"
                type="datetime"
                v-model="formData.consultDate"
                unlink-panels
                placeholder="请选择"
                :disabled-date="disabledNowAfterDate"
              />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="媒体渠道:" prop="sourceId">
              <el-select
                v-model="formData.sourceId"
                placeholder="请选择"
                class="w-full"
              >
                <el-option
                  clearable
                  :label="item.name"
                  :value="item.id"
                  v-for="item in ConsultSourceList"
                  :key="item.id"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="接待人:" prop="staffId">
              <el-select
                v-model="formData.staffId"
                placeholder="请选择"
                class="w-full"
              >
                <el-option
                  clearable
                  :label="item.name"
                  :value="item.id"
                  v-for="item in ConsultStaffList"
                  :key="item.id"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="老人地址:" prop="address">
              <el-input v-model="formData.address" clearable />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="咨询内容:" prop="consultContent">
              <el-input v-model="formData.consultContent" clearable />
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
  idNumRule,
  disabledNowAfterDate
} from "@/utils/formRules";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const ConsultSourceList = ref<{ id: number; name: string | number }[]>([]);
const ConsultStaffList = ref<{ id: number; name: string | number }[]>([]);

const rules = reactive<FormRules>({
  consultName: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "咨询人姓名", 2, 10);
      },
      trigger: "blur"
    }
  ],
  consultPhone: [
    {
      required: true,
      validator(rule, value, callback) {
        phoneRule(rule, value, callback, "咨询人");
      },
      trigger: "blur"
    }
  ],
  elderName: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "老人姓名", 2, 10);
      },
      trigger: "blur"
    }
  ],
  elderPhone: [
    {
      required: true,
      validator(rule, value, callback) {
        phoneRule(rule, value, callback, "老人");
      },
      trigger: "blur"
    }
  ],
  consultDate: [{ required: true, message: "请选择咨询日期", trigger: "blur" }],
  sourceId: [{ required: true, message: "请选择媒体渠道", trigger: "blur" }],
  staffId: [{ required: true, message: "请选择接待人", trigger: "blur" }],
  address: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "老人地址", 6, 50);
      },
      trigger: "blur"
    }
  ],
  age: [
    {
      required: true,
      validator(rule, value, callback) {
        integerRule(rule, value, callback, "老人年龄", 50, 200);
      },
      trigger: "blur"
    }
  ],
  consultContent: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "咨询内容", 1, 255);
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
  idNum: [
    {
      required: true,
      validator(rule, value, callback) {
        idNumRule(rule, value, callback, "老人");
      },
      trigger: "blur"
    }
  ],
  sex: [{ required: true, message: "请选择老人性别", trigger: "blur" }]
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
  consultName: "",
  consultPhone: "",
  age: "",
  elderName: "",
  elderPhone: "",
  consultContent: "",
  relation: "",
  consultDate: "",
  sourceId: "",
  staffId: "",
  address: "",
  idNum: "",
  sex: ""
});

// 接收父组件传过来的参数
const acceptParams = async (params: DialogProps) => {
  const [ConsultSourceListData, ConsultStaffListData, echoData] =
    await Promise.all([
      listConsultSource(),
      listConsultStaff(),
      getConsultByForm({
        consultId: params.rowData.consultId,
        elderId: params.rowData.elderId
      })
    ]);
  ConsultSourceList.value = ConsultSourceListData.data;
  ConsultStaffList.value = ConsultStaffListData.data;
  drawerProps.value = params;
  formData.value = { ...params.rowData, ...echoData.data[0] };
  dialogVisible.value = true;
};

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
    } else {
      return fields;
    }
  });
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
