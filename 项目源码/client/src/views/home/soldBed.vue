<template>
  <myCard title="可售床位">
    <div class="flex justify-between">
      <div
        class="mx-1 flex px-3 py-2 flex-col rounded items-center border w-1/2"
      >
        <div class="large-card-font">
          {{ AvailableBed?.idleRoomNum }}
        </div>
        <div class="text-center">空闲房间</div>
      </div>
      <div
        class="mx-1 flex px-3 py-2 flex-col rounded items-center border w-1/2"
      >
        <div class="large-card-font">{{ AvailableBed?.idleBedNum }}</div>
        <div class="text-center">空闲床位</div>
      </div>
      <!-- <div
        class="mx-1 flex px-3 py-2 flex-col rounded items-center border w-1/4"
      >
        <div class="large-card-font">{{ AvailableBed.exitAuditNum }}</div>
        <div class="text-center">已登记退房</div>
      </div>
      <div
        class="mx-1 flex px-3 py-2 flex-col rounded items-center border w-1/4"
      >
        <div class="large-card-font">0</div>
        <div class="text-center">已登记退床</div>
      </div> -->
    </div>
  </myCard>
</template>

<script lang="ts" setup>
import { getAvailableBed } from '@/apis/home'
import { onMounted, ref } from 'vue'
const AvailableBed = ref()
onMounted(async () => {
  const data: any = await getAvailableBed()
  if (data.code === 200 && data.data) {
    AvailableBed.value = data.data
  }
})
</script>

<style></style>
