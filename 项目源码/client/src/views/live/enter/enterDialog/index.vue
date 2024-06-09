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
                <el-input v-model="formData.name" clearable>
                  <template #append>
                    <el-button
                      @click="openDrawer('elder')"
                      :icon="Plus"
                      :style="{cursor: (drawerProps.title !== '新增' ? not-allowed : cursor)}"
                      :disabled="drawerProps.title !== '新增'"
                    />
                  </template>
                </el-input>
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
              <el-form-item label="老人电话:" prop="phone">
                <el-input v-model="formData.phone" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="家庭地址:" prop="address">
                <el-input v-model="formData.address" clearable />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="入住床位:" prop="bedId">
                <el-input
                  v-model="formData.bedName"
                  @click="openDrawer('tree')"
                  clearable
                  placeholder="请选择"
                />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="护理等级:" prop="nursingGradeId">
                <el-select
                  v-model="formData.nursingGradeId"
                  placeholder="请选择"
                  class="w-full"
                >
                  <el-option
                    clearable
                    :label="item.name"
                    :value="item.id"
                    v-for="item in NursingGradeList"
                    :key="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="餐饮套餐:" prop="cateringSetId">
                <el-select
                  v-model="formData.cateringSetId"
                  placeholder="请选择"
                  class="w-full"
                >
                  <el-option
                    clearable
                    :label="item.name"
                    :value="item.id"
                    v-for="item in CateringSetList"
                    :key="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="营销人员:" prop="staffId">
                <el-select
                  v-model="formData.staffId"
                  placeholder="请选择"
                  class="w-full"
                >
                  <el-option
                    clearable
                    :label="item.name"
                    :value="item.id"
                    v-for="item in EnterStaffList"
                    :key="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="合同签订日期:" prop="signDate">
                <el-date-picker
                  class="w-full"
                  type="datetime"
                  v-model="formData.signDate"
                  unlink-panels
                  placeholder="请选择"
                  :disabled-date="disabledNowAfterDate"
                />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="合同有效期限:" prop="contractPeriod">
                <el-date-picker
                  class="w-full"
                  v-model="formData.contractPeriod"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  unlink-panels
                  placeholder="请选择"
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
              <el-form-item label="紧急联系人:" prop="operateEmergencyContactQueryList">
                <div>
                  <el-form>
                    <div style="position: relative;">
                      <el-button
                        class="bg-blue"
                        type="primary"
                        icon="Plus"
                        circle
                        style="position: absolute;width: 27.6px;height: 27.6px; top: 6.5px; left: 1.7%;z-index: 1000"
                        @click="addContact"
                      />
                    </div>
                    <el-table :data="contactList" border>
                      <el-table-column width="60px">
                        <template v-slot="scope">
                          <el-button class="bg-red-600" type="danger" :icon="Delete" circle
                                     @click="deleteContact(scope.$index)" />
                        </template>
                      </el-table-column>
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
                  </el-form>
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
    <treeDialog @getCheckBedInfo="getCheckBedInfo" ref="TreeDialogRef" />
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref, toRefs } from "vue";
import elderListDialog from "@/components/elderListDialog/index.vue";
import treeDialog from "@/components/treeDialog/index.vue";
import { ElMessage, ElMessageBox, FormInstance, FormRules } from "element-plus";
import { Plus, Delete } from "@element-plus/icons-vue";
import {
  phoneRule,
  integerRule,
  stringRule,
  idNumRule,
  disabledNowAfterDate
} from "@/utils/formRules";
import {
  getBedById,
  getBuildTree, getCheckContractById,
  listCateringSet,
  listNurseGrade,
  listReserveStaff,
  pageSearchElderByKey
} from "@/apis/checkContract";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const NursingGradeList = ref<{ id: number; name: string | number }[]>([]);
const CateringSetList = ref<{ id: number; name: string | number }[]>([]);
const EnterStaffList = ref<{ id: number; name: string | number }[]>([]);

// 紧急联系人列表
const contactList = ref<any[]>([]);
// 添加紧急联系人行
const addContact = () => {
  if (!contactList.value) {
    contactList.value = [{}];
  } else {
    contactList.value.push({});
  }
};
// 删除紧急联系人行
const deleteContact = (index: any) => {
  contactList.value.splice(index, 1);
};

const rules = reactive<FormRules>({
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
  sex: [{ required: true, message: "请选择老人性别", trigger: "blur" }],
  age: [
    {
      required: true,
      validator(rule, value, callback) {
        integerRule(rule, value, callback, "老人年龄", 50, 200);
      },
      trigger: "blur"
    }
  ],
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
  ],
  bedId: [{ required: true, message: "请选择床位", trigger: "blur" }],
  nursingGradeId: [{ required: true, message: "请选择护理等级", trigger: "blur" }],
  cateringSetId: [{ required: true, message: "请选择餐饮套餐", trigger: "blur" }],
  signDate: [{ required: true, message: "请选择签订日期", trigger: "blur" }],
  contractPeriod: [{
    required: true,
    validator(rule, value, callback) {
      if (!value) {
        callback("请选择合同有效期限");
      } else {
        formData.value.startDate = value[0];
        formData.value.endDate = value[1];
        callback();
      }
    },
    trigger: "blur"
  }],
  sourceId: [{ required: true, message: "请选择媒体渠道", trigger: "blur" }],
  staffId: [{ required: true, message: "请选择营销人员", trigger: "blur" }],
  operateEmergencyContactQueryList: [
    {
      required: true,
      validator(rule, value, callback) {
        if (!contactList.value || contactList.value.length < 1) {
          callback("至少添加一个紧急联系人");
        } else {
          const errExist = ref(false);
          contactList.value.forEach(contact => {
            const propNum = ref(0);
            for (var prop in contact) {
              propNum.value++;
            }
            if (propNum.value < 5) {
              errExist.value = true;
            }
          });
          if (errExist.value) {
            callback("请按规则填写紧急联系人信息");
          } else {
            formData.value.operateEmergencyContactQueryList = contactList.value;
            callback();
          }
        }
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
  nursingGradeId: "",
  cateringSetId: "",
  bedId: "",
  bedName: "",
  name: "",
  idNum: "",
  age: "",
  sex: "",
  phone: "",
  address: "",
  staffId: "",
  signDate: "",
  contractPeriod: ref<any[]>([]),
  startDate: "",
  endDate: "",
  operateEmergencyContactQueryList: contactList
});

// 打开选择老人的列表 drawer
const ElderDialogRef = ref();
const TreeDialogRef = ref();
const openDrawer = (type: string) => {
  if (type === "elder") {
    const params = {
      elderApi: pageSearchElderByKey
    };
    ElderDialogRef.value.elderAcceptParams(params);
  } else if (type === "tree") {
    const params = {
      treeApi: getBuildTree
    };
    TreeDialogRef.value.treeAcceptParams(params);
  }
};

// 获取选择老人的信息
const getCheckElderInfo = (row: any) => {
  formData.value.name = row.name;
  formData.value.idNum = row.idNum;
  formData.value.age = row.age;
  formData.value.sex = row.sex;
  formData.value.phone = row.phone;
  formData.value.address = row.address;
};

// 获取选择床位的信息
const getCheckBedInfo = (bed: any) => {
  formData.value.bedId = bed.id;
  formData.value.bedName = bed.name;
};

// 接收父组件传过来的参数
const acceptParams = async (params: DialogProps) => {
  const [NursingGradeListData, CateringSetListData, EnterStaffListData, echoData] =
    await Promise.all([
      listNurseGrade(),
      listCateringSet(),
      listReserveStaff(),
      getCheckContractById({
        elderId: params.rowData.id
      })
    ]);
  NursingGradeList.value = NursingGradeListData.data;
  CateringSetList.value = CateringSetListData.data;
  EnterStaffList.value = EnterStaffListData.data;
  drawerProps.value = params;
  formData.value = { ...params.rowData, ...echoData.data };
  formData.value.contractPeriod = [formData.value.startDate, formData.value.endDate];
  if (drawerProps.value.title !== "新增") {
    const res: any = await getBedById(formData.value.bedId);
    formData.value.bedName = res.data.bedName;
  }
  contactList.value = formData.value.operateEmergencyContactQueryList;
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

.contactForm {
  display: flex;
  flex-wrap: wrap;
  padding: 16px 16px 0 16px;

  &:hover {
    background-color: #79bbff;
  }
}

.contactFormItem {
  width: 30%;
  margin-right: 3%;
  margin-bottom: 16px;
}

:deep(.el-form-item__content) {
  display: block;
}
</style>
