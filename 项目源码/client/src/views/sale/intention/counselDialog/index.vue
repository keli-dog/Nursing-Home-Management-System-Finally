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
            <el-form-item label="联系电话:" prop="consultPhone">
              <el-input v-model="formData.consultPhone" clearable />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="老人姓名:" prop="elderName">
              <el-input v-model="formData.elderName" clearable />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="老人联系电话:" prop="elderPhone">
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
                placeholder="选择接待人"
                class="w-full"
              >
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="老人联年龄:" prop="age">
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
            <el-form-item label="咨询日期" prop="consultDate">
              <el-date-picker
                class="w-full"
                v-model="formData.consultDate"
                unlink-panels
              />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="媒体渠道" prop="sourceId">
              <el-select
                v-model="formData.sourceId"
                placeholder="选择媒体渠道"
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
            <el-form-item label="接待人" prop="staffId">
              <el-select
                v-model="formData.staffId"
                placeholder="选择接待人"
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
            <el-form-item label="家庭地址" prop="address">
              <el-input v-model="formData.address" clearable />
            </el-form-item>
          </div>
          <div class="w-full md:w-1/2">
            <el-form-item label="咨询内容" prop="consultContent">
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
        <el-button @click="handleSubmit"> 提交 </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'

import {
  listConsultSource,
  listConsultStaff,
  getConsultByForm
} from '@/apis/soldManage'

const dialogVisible = ref(false)
const ConsultSourceList = ref<{ id: number; name: string | number }[]>([])
const ConsultStaffList = ref<{ id: number; name: string | number }[]>([])

const rules = reactive({
  consultName: [{ required: true, message: '请上传咨询人姓名' }],
  consultPhone: [{ required: true, message: '请上传联系电话' }],
  elderName: [{ required: true, message: '请填写老人姓名' }],
  elderPhone: [{ required: true, message: '请填写老人联系电话' }],
  consultDate: [{ required: true, message: '请填写咨询日期' }],
  sourceId: [{ required: true, message: '请填写媒体渠道' }],
  staffId: [{ required: true, message: '请填写接待人' }],
  address: [{ required: true, message: '请填写家庭地址' }],
  age: [{ required: true, message: '请填写老人年龄' }],
  consultContent: [{ required: true, message: '请填写咨询内容' }],
  relation: [{ required: true, message: '请填写与老人关系' }],
  idNum: [{ required: true, message: '请填写身份证' }],
  sex: [{ required: true, message: '请填写老人性别' }]
})

interface DialogProps {
  title: string
  isView: boolean
  rowData?: any
  api?: (params: any) => Promise<any>
  getTableList?: () => Promise<any>
}

function fullOpen(n: number, m: number) {
  let result = Math.random() * (m - n) + n
  while (result == n) {
    result = Math.random() * (m - n) + n
  }
  return Math.floor(result)
}

const drawerProps = ref<DialogProps>({
  isView: false,
  title: ''
})
const formData = ref({
  consultName: '',
  consultPhone: '',
  age: '',
  elderName: '',
  elderPhone: '',
  consultContent: '',
  relation: '',
  consultDate: '',
  sourceId: '',
  staffId: '',
  address: '',
  idNum: '',
  sex: ''
})

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
    ])
  ConsultSourceList.value = ConsultSourceListData.data
  ConsultStaffList.value = ConsultStaffListData.data
  drawerProps.value = params
  formData.value = { ...params.rowData, ...echoData.data[0] }
  dialogVisible.value = true
}

//点击提交
const ruleFormRef = ref<FormInstance>()
const handleSubmit = () => {
  ruleFormRef.value!.validate(async valid => {
    if (!valid) return
    try {
      const res = await drawerProps.value.api!(formData.value)
      if (res.code == 200 && res.msg == '操作成功') {
        ElMessage.success({
          message: drawerProps.value.title === '新增' ? '新增成功' : '编辑成功'
        })
        drawerProps.value.getTableList!()
        dialogVisible.value = false
      } else {
        ElMessage.error({
          message: drawerProps.value.title === '新增' ? '新增失败' : '编辑失败'
        })
      }
    } catch (error) {
      console.log(error)
    }
  })
}

defineExpose({
  acceptParams
})
</script>
<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
