<template>
  <myCard title="今日销售跟进">
    <div class="flex justify-between">
      <div
        class="mx-1 flex px-3 py-2 flex-col rounded items-center border w-1/3"
      >
        <div class="large-card-font">{{ SaleFollow?.todayReturnVisitNum }}</div>
        <div class="text-center">今日应回访</div>
      </div>
      <div
        class="mx-1 flex px-3 py-2 flex-col rounded items-center border w-1/3"
      >
        <div class="large-card-font">
          {{ SaleFollow?.todayReturnedVisitNum }}
        </div>
        <div class="text-center">今日已回访</div>
      </div>
      <div
        class="mx-1 flex px-3 py-2 flex-col rounded items-center border w-1/3"
      >
        <div class="large-card-font">
          {{ SaleFollow?.todayReturnedVisitNum }}
        </div>
        <div class="text-center">待回访</div>
      </div>
      <!-- <div
        class="mx-1 flex px-3 py-2 flex-col rounded items-center border w-1/4"
      >
        <div class="large-card-font">{{ SaleFollow.todayReturnVisitNum }}</div>
        <div class="text-center">回收客户</div>
      </div> -->
    </div>
  </myCard>
</template>

<script lang="ts" setup>
import { getTodaySaleFollow } from '@/apis/home'
import { onMounted, ref } from 'vue'
const SaleFollow = ref()
onMounted(async () => {
  const data: any = await getTodaySaleFollow()
  if (data.code === 200 && data.data) {
    SaleFollow.value = data.data
  }
})
</script>

<style></style>
