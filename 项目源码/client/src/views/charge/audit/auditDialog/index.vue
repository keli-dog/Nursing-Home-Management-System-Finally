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
              <el-form-item label="老人姓名:" prop="">
                <el-input v-model="ElderFee.elderName" clearable disabled />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="合同有效期限:" prop="contractPeriod">
                <el-date-picker
                  class="w-full"
                  v-model="ElderFee.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  unlink-panels
                  placeholder="请选择"
                  disabled
                />
              </el-form-item>
            </div>
            <div style="width: 100%">
              <el-form-item label="月费详情:" prop="">
                <div>
                  <el-form>
                    <el-table :data="ElderFee.feeDetailList" border>
                      <el-table-column label="月份" align="center" width="220">
                        <template v-slot="scope">
                          <el-input v-model="scope.row.feeDate" disabled />
                        </template>
                      </el-table-column>
                      <el-table-column label="护理费(元)" align="center">
                        <template v-slot="scope">
                          <el-input v-model="scope.row.contractInsideNurseFee" disabled />
                        </template>
                      </el-table-column>
                      <el-table-column label="餐饮费(元)" align="center">
                        <template v-slot="scope">
                          <el-input v-model="scope.row.contractInsideDishesFee" disabled />
                        </template>
                      </el-table-column>
                      <el-table-column label="床位费(元)" align="center">
                        <template v-slot="scope">
                          <el-input v-model="scope.row.contractInsideBedFee" disabled />
                        </template>
                      </el-table-column>
                      <el-table-column label="应缴费用(元)" align="center">
                        <template v-slot="scope">
                          <el-input v-model="scope.row.payableFee" disabled />
                        </template>
                      </el-table-column>
                      <el-table-column label="应退费用(元)" align="center">
                        <template v-slot="scope">
                          <el-input v-model="scope.row.returnableFee" disabled />
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-form>
                </div>
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="应缴总额:" prop="">
                <el-input v-model="payAmount" disabled />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="应退总额:" prop="">
                <el-input v-model="returnAmount" disabled />
              </el-form-item>
            </div>
            <div class="w-full md:w-1/2">
              <el-form-item label="审核结果:" prop="auditResult">
                <el-select
                  v-model="formData.auditResult"
                  placeholder="请选择"
                  class="w-full"
                >
                  <el-option
                    clearable
                    :label="item.name"
                    :value="item.value"
                    v-for="item in auditResultList"
                    :key="item.value"
                  />
                </el-select>
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
import { ElMessage, FormInstance, FormRules } from "element-plus";
import { getElderFeeById, IAuditResultList } from "@/apis/retreatAudit";

const ruleFormRef = ref<FormInstance | null>(null);
const dialogVisible = ref(false);
const ElderFee = ref<any>([]);
const payAmount = ref(0);
const returnAmount = ref(0);
const auditResultList = IAuditResultList;

const rules = reactive<FormRules>({
  auditResult: [{ required: true, message: "请选择审核结果", trigger: "blur" }]
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
  applyId: "",
  elderId: "",
  auditResult: ""
});

// 接收父组件传过来的参数
const acceptParams = async (params: DialogProps) => {
  const res: any = await getElderFeeById({
    elderId: params.rowData.elderId
  });
  ElderFee.value = res.data;
  ElderFee.value.dateRange = [new Date(ElderFee.value.contractStartTime), new Date(ElderFee.value.contractEndTime)];
  payAmount.value = 0
  returnAmount.value = 0
  ElderFee.value.feeDetailList.forEach((feeDetail: any) => {
    payAmount.value += feeDetail.payableFee;
    returnAmount.value += feeDetail.returnableFee;
  });
  formData.value = params.rowData;
  drawerProps.value = params;
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

:deep(.el-form-item__content) {
  display: block;
}
</style>
