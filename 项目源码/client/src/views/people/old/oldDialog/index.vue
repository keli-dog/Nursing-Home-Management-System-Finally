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
              <el-form-item label="老人姓名:" prop="name">
                <el-input v-model="formData.name" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="身份证号:" prop="idNum">
                <el-input v-model="formData.idNum" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="老人年龄:" prop="age">
                <el-input v-model="formData.age" clearable />
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
              <el-form-item label="老人电话:" prop="phone">
                <el-input v-model="formData.phone" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="老人地址:" prop="address">
                <el-input v-model="formData.address" clearable />
              </el-form-item>
            </div>
            <!-- 占位 -->
            <div
              class="md:w-1/2"
              v-for="i in 1"
              :key="i"
              style="height: 0"
            ></div>
            <div style="width: 100%;" v-show="!show">
              <el-form-item label="床位信息:">
                <div class="w-full md:w-1/2">
                  <el-form-item label="床位名称:">
                    <el-input v-if="formData.elderBedByIdVo" v-model="formData.elderBedByIdVo.bedName" clearable />
                    <el-input v-else clearable />
                  </el-form-item>
                </div>
                <div class="w-full md:w-1/2">
                  <el-form-item label="房间类型:">
                    <el-input v-if="formData.elderBedByIdVo" v-model="formData.elderBedByIdVo.roomType" clearable />
                    <el-input v-else clearable />
                  </el-form-item>
                </div>
                <div class="w-full md:w-1/2" style="margin-top: 20px;">
                  <el-form-item label="月费（元）:">
                    <el-input v-if="formData.elderBedByIdVo" v-model="formData.elderBedByIdVo.monthPrice" clearable />
                    <el-input v-else clearable />
                  </el-form-item>
                </div>
              </el-form-item>
            </div>
            <div style="width: 100%" v-show="!show">
              <el-form-item label="紧急联系人:">
                <div style="width: 100%">
                  <el-table :data="formData.elderEmergencyContactByIdVoList" border>
                    <el-table-column label="姓名" align="center">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.name" placeholder="请输入姓名" />
                      </template>
                    </el-table-column>
                    <el-table-column label="电话" align="center">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.phone" placeholder="请输入电话"></el-input>
                      </template>
                    </el-table-column>
                    <el-table-column label="邮箱" align="center">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.email" placeholder="请输入邮箱"></el-input>
                      </template>
                    </el-table-column>
                    <el-table-column label="关系" align="center">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.relation" placeholder="请输入关系"></el-input>
                      </template>
                    </el-table-column>
                    <el-table-column label="是否接收消息" align="center">
                      <template v-slot="scope">
                        <el-radio-group v-model="scope.row.receiveFlag" aria-placeholder="请选择">
                          <el-radio label="Y">是</el-radio>
                          <el-radio label="N">否</el-radio>
                        </el-radio-group>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-form-item>
            </div>
            <div style="width: 100%" v-show="!show">
              <el-form-item label="护理等级:">
                <div class="w-full md:w-1/2">
                  <el-form-item label="护理名称:">
                    <el-input v-if="formData.elderNurseGradeByIdVo" v-model="formData.elderNurseGradeByIdVo.name"
                              clearable />
                    <el-input v-else clearable />
                  </el-form-item>
                </div>
                <div class="w-full md:w-1/2">
                  <el-form-item label="护理类型:">
                    <el-input v-if="formData.elderNurseGradeByIdVo" v-model="formData.elderNurseGradeByIdVo.type"
                              clearable />
                    <el-input v-else clearable />
                  </el-form-item>
                </div>
                <div class="w-full md:w-1/2" style="margin-top: 20px;">
                  <el-form-item label="月费（元）:">
                    <el-input v-if="formData.elderNurseGradeByIdVo" v-model="formData.elderNurseGradeByIdVo.monthPrice"
                              clearable />
                    <el-input v-else clearable />
                  </el-form-item>
                </div>
              </el-form-item>
            </div>
            <div style="width: 100%" v-show="!show">
              <el-form-item label="护理项目:" v-if="formData.elderNurseGradeByIdVo">
                <div style="width: 100%">
                  <el-table :data="formData.elderNurseGradeByIdVo.nurseGradeServiceVoList" border>
                    <el-table-column label="服务名称" align="center">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.name" />
                      </template>
                    </el-table-column>
                    <el-table-column label="服务单价" align="center">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.price"></el-input>
                      </template>
                    </el-table-column>
                    <el-table-column label="服务时间（min）" align="center">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.needDate"></el-input>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-form-item>
            </div>
            <div style="width: 100%" v-show="!show">
              <el-form-item label="餐饮套餐:">
                <div class="w-full md:w-1/2">
                  <el-form-item label="套餐名称:">
                    <el-input v-if="formData.elderCateringSetByIdVo" v-model="formData.elderCateringSetByIdVo.name"
                              clearable />
                    <el-input v-else clearable />
                  </el-form-item>
                </div>
                <div class="w-full md:w-1/2">
                  <el-form-item label="月费（元）:">
                    <el-input v-if="formData.elderCateringSetByIdVo"
                              v-model="formData.elderCateringSetByIdVo.monthPrice" clearable />
                    <el-input v-else clearable />
                  </el-form-item>
                </div>
              </el-form-item>
            </div>
            <div style="width: 100%" v-show="!show">
              <el-form-item label="套餐食物:" v-if="formData.elderCateringSetByIdVo">
                <div style="width: 100%">
                  <el-table :data="formData.elderCateringSetByIdVo.setDishesVoList" border>
                    <el-table-column label="食物名称" align="center">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.name" />
                      </template>
                    </el-table-column>
                    <el-table-column label="食物单价" align="center">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.price"></el-input>
                      </template>
                    </el-table-column>
                  </el-table>
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
      <span class="dialog-footer" v-show="show">
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
import { ElMessage, FormInstance, FormRules } from "element-plus";
import {
  idNumRule, integerRule,
  phoneRule,
  stringRule
} from "@/utils/formRules";
import { getElderById, getElderRecordById } from "@/apis/elderRecord";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const show = ref<boolean>(false);

const rules = reactive<FormRules>({
  roleId: [{ required: true, message: "请选择员工角色", trigger: "blur" }],
  name: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "老人姓名", 2, 10);
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
  age: [
    {
      required: true,
      validator(rule, value, callback) {
        integerRule(rule, value, callback, "老人年龄", 50, 200);
      },
      trigger: "blur"
    }
  ],
  sex: [{ required: true, message: "请选择老人性别", trigger: "blur" }],
  phone: [
    {
      required: true,
      validator(rule, value, callback) {
        phoneRule(rule, value, callback, "老人");
      },
      trigger: "blur"
    }
  ],
  address: [
    {
      required: true,
      validator(rule, value, callback) {
        stringRule(rule, value, callback, "老人地址", 6, 50);
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
  idNum: "",
  age: "",
  sex: "",
  phone: "",
  address: "",
  elderEmergencyContactByIdVoList: [{
    name: "",
    phone: "",
    email: "",
    relation: "",
    receiveFlag: ""
  }],
  elderNurseGradeByIdVo: {
    name: "",
    type: "",
    monthPrice: "",
    nurseGradeServiceVoList: [
      {
        name: "",
        chargeMethod: "",
        price: "",
        needDate: ""
      }
    ]
  },
  elderCateringSetByIdVo: {
    id: "",
    name: "",
    monthPrice: "",
    setDishesVoList: [{
      id: "",
      name: "",
      price: ""
    }]
  },
  elderBedByIdVo: {
    bedId: "",
    bedName: "",
    roomType: "",
    monthPrice: ""
  }
});

// 接收父组件传过来的参数
const acceptParams = async (params: DialogProps) => {
  drawerProps.value = params;
  show.value = drawerProps.value.title === "编辑"
  if (drawerProps.value.title === "长者档案") {
    const echoData: any = await getElderRecordById({
      elderId: params.rowData.id
    });
    formData.value = echoData.data;
  } else {
    const echoData: any = await getElderById({
      elderId: params.rowData.id
    });
    formData.value = echoData.data;
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
