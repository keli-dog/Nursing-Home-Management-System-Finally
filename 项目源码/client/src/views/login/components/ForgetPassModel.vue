<template>
  <div class="forget-pass-form">
    <el-form
      :model="formData"
      class="forget-pass-form"
      ref="ruleFormRef"
      :rules="forgetPassRules"
      size="large"
    >
      <div class="title">
        <img src="@/assets/imgs/logo.png" alt="" />
        <h2>敬老院管理系统</h2>
      </div>
      <el-form-item prop="phone">
        <el-input
          v-model="formData.phone"
          placeholder="账号"
          :prefix-icon="useRenderIcon('user', { size: 12 })"
          clearable
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="formData.password"
          placeholder="新密码"
          type="password"
          :prefix-icon="useRenderIcon('password', { size: 12 })"
          clearable
          show-password
        />
      </el-form-item>

      <el-form-item prop="confirmPassword">
        <el-input
          v-model="formData.confirmPassword"
          placeholder="确认密码"
          type="password"
          :prefix-icon="useRenderIcon('password', { size: 12 })"
          clearable
          show-password
        />
      </el-form-item>

      <el-form-item prop="verifyCode">
        <el-input
          v-model="formData.verifyCode"
          placeholder="验证码"
          :prefix-icon="useRenderIcon('verify')"
          clearable
        >
          <template v-slot:append>
            <el-button
              @click="sendCodeHandle"
              :disabled="SEND_code_FLAG"
              style="margin: 0 5px"
            >
              {{ SEND_CODE }}
            </el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <div class="password-set">
          <el-button
            id="returnLogin"
            link
            @click="$emit('returnLoginHandle')"
          >
            返回登录
          </el-button>
        </div>
        <el-button
          class="bg-blue"
          size="default"
          type="primary"
          style="width: 100%"
          @click="handleForgetPass(ruleFormRef)"
          :loading="loading"
          >忘记密码
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRenderIcon } from '@/hooks/useIcons'
import { FormInstance, FormRules, ElMessage } from 'element-plus'
import { isNotAccount } from '@/utils/is'
import { forgetPass, sendCode } from '@/apis/user'

const ruleFormRef = ref<FormInstance | null>(null)
const formData = ref({
  phone: '13547584400',
  password: '123456',
  confirmPassword: '123456',
  verifyCode: ''
})
let SEND_CODE = ref('发送验证码')
let SEND_code_FLAG = ref(false)
const loading = ref(false)

// 处理发送验证码
const sendCodeHandle = async () => {
  const account = formData.value.phone.trim()
  const pass = formData.value.password.trim()
  // 验证是否能发送验证码
  if (!account || !pass) {
    ElMessage({
      message: '账号和密码不能为空',
      type: 'warning'
    })
    return
  }
  // 发送验证码
  const res: any = await sendCode({
    account: formData.value.phone,
    pass: formData.value.password
  })
  if (res.code === 200) {
    // 验证码不为空则填入输入框
    if (res.data !== null) {
      formData.value.verifyCode = res.data
    }
    // 禁用按钮
    SEND_code_FLAG.value = true
    // 倒计时
    let time = 60
    const timmer = setInterval(() => {
      SEND_CODE.value = '重新发送(' + time-- + ')'
      if (!time) {
        // 清除定时器
        clearInterval(timmer)
        SEND_CODE.value = '发送验证码'
        // 启用按钮
        SEND_code_FLAG.value = false
      }
    }, 1000)
  } else {
    ElMessage({
      message: res.msg,
      type: 'warning'
    })
  }
}

const handleForgetPass = (formRef: FormInstance | null) => {
  loading.value = true
  if (!formRef) return
  formRef.validate(async (valid, fields) => {
    if (valid) {
      const res: any = await forgetPass({
        code: formData.value.verifyCode,
        pass: formData.value.password,
        account: formData.value.phone
      })
      if (res.code === 200) {
        const returnLogin: any = document.getElementById('returnLogin')
        returnLogin.click()
        ElMessage({
          message: res.msg,
          type: 'success'
        })
      } else {
        loading.value = false
        ElMessage({
          message: res.msg,
          type: 'warning'
        })
      }
    } else {
      loading.value = false
      return fields
    }
  })
}

/* 忘记密码校验 */
const forgetPassRules = reactive<FormRules>({
  phone: [
    {
      validator: (rule, value, callback) => {
        let phone = value?.trim()
        if (phone === '') {
          callback(new Error('账号不能为空'))
        } else if (isNotAccount(phone)) {
          callback(new Error('账号格式有误'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  password: [
    {
      validator: (rule, value, callback) => {
        const pass = value?.trim()
        if (!pass) {
          callback(new Error('新密码不能为空'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        const confirmPass = value?.trim()
        const pass = formData.value.password.trim()
        if (confirmPass === '') {
          callback(new Error('确认密码不能为空'))
        } else if (confirmPass !== pass) {
          callback('与新密码不一致')
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  verifyCode: [
    {
      validator(rule, value, callback) {
        const verifyCode = value?.trim()
        if (verifyCode === '') {
          callback(new Error('验证码不能为空'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})
</script>

<style scoped>
::v-deep .el-input-group__append {
  padding: 0;
}
</style>
