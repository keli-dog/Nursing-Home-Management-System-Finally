<template>
  <myCard title="今日预览">
    <div class="flex justify-between">
      <div
        class="p-3 cursor-pointer text-white flex text-xl flex-col w-1/4 h-20 mx-1 rounded bg-yellow-400"
      >
        <div class="flex justify-between">
          <div>{{ overViewData?.todayAddConsultNum }}</div>
          <IconPark :icon="Mail" class="" :size="33"></IconPark>
        </div>
        今日新增咨询
      </div>
      <div
        class="p-3 cursor-pointer text-white flex text-xl flex-col w-1/4 h-20 mx-1 rounded bg-green-400"
      >
        <div class="flex justify-between">
          <div>{{ overViewData?.todayAddReserveNum }}</div>
          <IconPark :icon="Mail" class="" :size="33"></IconPark>
        </div>
        今日新增预定
      </div>
      <div
        class="p-3 cursor-pointer text-white flex text-xl flex-col w-1/4 h-20 mx-1 rounded bg-blue-400"
      >
        <div class="flex justify-between">
          <div>{{ overViewData?.todayAddContractNum }}</div>
          <IconPark :icon="Mail" class="" :size="33"></IconPark>
        </div>
        今日新增合同
      </div>
      <div
        class="p-3 cursor-pointer text-white flex text-xl flex-col w-1/4 h-20 mx-1 rounded bg-red-400"
      >
        <div class="flex justify-between">
          <div>{{ overViewData?.todayContractExpireNum }}</div>
          <IconPark :icon="Mail" class="" :size="33"></IconPark>
        </div>
        合同到期提醒
      </div>
    </div>
  </myCard>
</template>

<script lang="ts" setup>
import { MusicList, Mail } from '@icon-park/vue-next'
import { getTodayOverview } from '@/apis/home'
import { onMounted, ref } from 'vue'
const overViewData = ref()
onMounted(async () => {
  const data: any = await getTodayOverview()
  if (data.code === 200 && data.data) {
    overViewData.value = data.data
  }
})
</script>

<style></style>
