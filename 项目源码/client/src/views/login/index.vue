<template>
  <div class="login-container">
    <div class="left-box">
      <div class="img">
        <img src="@/assets/imgs/computer.svg" alt="" />
      </div>
    </div>
    <div class="right-box">
      <LoginModel
        @forgetPassHandle="forgetPassHandle"
        v-if="showFlag.loginModel.value"
      />
      <ForgetPassModel
        @returnLoginHandle="returnLoginHandle"
        v-if="showFlag.forgetModel.value"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginModel from './components/LoginModel.vue'
import ForgetPassModel from '@/views/login/components/ForgetPassModel.vue'
import { ref } from 'vue'

const showFlag = {
  loginModel: ref(true),
  forgetModel: ref(false)
}

const forgetPassHandle = () => {
  showFlag.loginModel.value = false
  showFlag.forgetModel.value = true
}

const returnLoginHandle = () => {
  showFlag.loginModel.value = true
  showFlag.forgetModel.value = false
}
</script>

<style lang="scss" scoped></style>
