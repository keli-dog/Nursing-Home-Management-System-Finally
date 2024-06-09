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
                    v-for="item in AccidentStaffList"
                    :key="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="发生时间:" prop="occurDate">
                <el-date-picker
                  class="w-full"
                  v-model="formData.occurDate"
                  unlink-panels
                  placeholder="请选择"
                  :disabled-date="disabledNowAfterDate"
                />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="事故描述:" prop="description">
                <el-input v-model="formData.description" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="事故图片:" prop="picture">
                <uploadImage :uploadParams="uploadParams" @setImageData="setImageData" />
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
import { onBeforeUnmount, onMounted, reactive, ref, toRefs } from "vue";
import elderListDialog from "@/components/elderListDialog/index.vue";
import uploadImage from "@/components/upload/image/index.vue";
import { ElMessage, ElMessageBox, FormInstance, FormRules, UploadUserFile } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import {
  stringRule,
  disabledNowAfterDate
} from "@/utils/formRules";
import { getAccidentById, listAccidentStaff, pageSearchElderByKey } from "@/apis/accident";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const AccidentStaffList = ref<{ id: number; name: string | number }[]>([]);

// 上传图片需要的变量
const uploadParams = ref({
  uploadType: "single",
  imageList: ref<UploadUserFile[]>([])
});
const setImageData = (url: string) => {
  formData.value.picture = url;
};

const rules = reactive<FormRules>({
  elderId: [{ required: true, message: "请选择事故老人", trigger: "blur" }],
  staffId: [{ required: true, message: "请选择护理人员", trigger: "blur" }],
  occurDate: [{ required: true, message: "请选择发生时间", trigger: "blur" }],
  description: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "事故描述", 1, 255);
      },
      trigger: "blur"
    }
  ],
  picture: [{ required: true, message: "请选择事故图片", trigger: "blur" }]
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
  staffId: "",
  occurDate: "",
  description: "",
  picture: ""
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
  const [AccidentStaffListData, echoData] =
    await Promise.all([
      listAccidentStaff(),
      getAccidentById({
        accidentId: params.rowData.id
      })
    ]);
  AccidentStaffList.value = AccidentStaffListData.data;
  drawerProps.value = params;
  formData.value = { ...params.rowData, ...echoData.data };
  // 在加载上传组件之前清空图片列表
  uploadParams.value.imageList = []
  if (drawerProps.value.title !== "新增") {
    formData.value.elderId = "0";
    // 添加显示上传的图片
    uploadParams.value.imageList.push({
      uid: 0,
      name: "0.jpeg",
      url: formData.value.picture
    });
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
