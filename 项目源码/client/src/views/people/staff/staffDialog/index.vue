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
              <el-form-item label="员工姓名:" prop="name">
                <el-input v-model="formData.name" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="员工角色:" prop="roleId">
                <el-select
                  v-model="formData.roleId"
                  placeholder="请选择"
                  class="w-full"
                >
                  <el-option
                    clearable
                    :label="item.name"
                    :value="item.id"
                    v-for="item in RoleList"
                    :key="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="身份证号:" prop="idNum">
                <el-input v-model="formData.idNum" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="年龄:" prop="age">
                <el-input v-model="formData.age" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="员工性别:" prop="sex">
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
              <el-form-item label="员工电话:" prop="phone">
                <el-input v-model="formData.phone" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="员工邮箱:" prop="email">
                <el-input v-model="formData.email" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="员工地址:" prop="address">
                <el-input v-model="formData.address" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="员工头像:" prop="avator">
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
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import uploadImage from "@/components/upload/image/index.vue";
import { ElMessage, FormInstance, FormRules, UploadUserFile } from "element-plus";
import {
  emailRule,
  idNumRule, integerRule,
  phoneRule,
  stringRule
} from "@/utils/formRules";
import { getRole, getStaffById } from "@/apis/staff";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const RoleList = ref<{ id: number; name: string | number }[]>([]);

// 上传图片需要的变量
const uploadParams = ref({
  uploadType: "single",
  imageList: ref<UploadUserFile[]>([])
});
const setImageData = (url: string) => {
  formData.value.avator = url;
};

const rules = reactive<FormRules>({
  roleId: [{ required: true, message: "请选择员工角色", trigger: "blur" }],
  name: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "员工姓名", 2, 10);
      },
      trigger: "blur"
    }
  ],
  idNum: [
    {
      required: true,
      validator(rule, value, callback) {
        idNumRule(rule, value, callback, "员工");
      },
      trigger: "blur"
    }
  ],
  age: [
    {
      required: true,
      validator(rule, value, callback) {
        integerRule(rule, value, callback, "员工年龄", 18, 200);
      },
      trigger: "blur"
    }
  ],
  sex: [{ required: true, message: "请选择员工性别", trigger: "blur" }],
  phone: [
    {
      required: true,
      validator(rule, value, callback) {
        phoneRule(rule, value, callback, "员工");
      },
      trigger: "blur"
    }
  ],
  email: [
    {
      required: true,
      validator(rule, value, callback) {
        emailRule(rule, value, callback, "员工");
      },
      trigger: "blur"
    }
  ],
  address: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "员工地址", 6, 50);
      },
      trigger: "blur"
    }
  ],
  avator:[{ required: true, message: "请选择上传员工头像", trigger: "blur" }]
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
  roleId: "",
  name: "",
  idNum: "",
  age: "",
  sex: "",
  phone: "",
  email: "",
  address: "",
  avator: ""
});

// 接收父组件传过来的参数
const acceptParams = async (params: DialogProps) => {
  const [RoleListData, echoData] =
    await Promise.all([
      getRole(),
      getStaffById({
        staffId: params.rowData.id
      })
    ]);
  RoleList.value = RoleListData.data;
  drawerProps.value = params;
  formData.value = { ...params.rowData, ...echoData.data };
  // 在加载上传组件之前清空图片列表
  uploadParams.value.imageList = []
  if (drawerProps.value.title !== "新增") {
    // 添加显示上传的图片
    uploadParams.value.imageList.push({
      uid: 0,
      name: "0.jpeg",
      url: formData.value.avator
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
