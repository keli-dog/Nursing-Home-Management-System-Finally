<template>
  <myCard title="本月业绩排行">
    <div class="flex flex-1 justify-between">
      <div class="bg-blue-50 mx-1 flex px-3 py-2 flex-col rounded border w-1/3">
        <div class="text-slate-400">咨询客户</div>
        <div class="flex justify-between items-center">
          <div class="large-card-font">
            {{ MonthPerformance?.consultClientNum }}
          </div>
          <div class="text-green-500">
            {{ MonthPerformance?.consultClientFloatRate }}%
          </div>
        </div>
      </div>

      <div class="bg-blue-50 mx-1 flex px-3 py-2 flex-col rounded border w-1/3">
        <div class="text-slate-400">签约合同</div>
        <div class="flex justify-between items-center">
          <div class="large-card-font">
            {{ MonthPerformance?.signContractNum }}
          </div>
          <div class="text-green-500">
            {{ MonthPerformance?.signContractFloatRate }}%
          </div>
        </div>
      </div>
      <div class="bg-blue-50 mx-1 flex px-3 py-2 flex-col rounded border w-1/3">
        <div class="text-slate-400">咨询转换率</div>
        <div class="flex justify-between items-center">
          <div class="large-card-font">
            {{ MonthPerformance?.consultConversionRate }}%
          </div>
          <div class="text-green-500">
            {{ MonthPerformance?.consultConversionFloatRate }}%
          </div>
        </div>
      </div>
    </div>
    <div class="mt-1">
      <el-table :data="tableData" stripe fit>
        <el-table-column prop="rank" label="排名" :width="1 / 10" />
        <el-table-column prop="name" label="业务员" :width="3 / 10" />
        <el-table-column
          prop="consultNum"
          sortable
          label="接待咨询"
          :width="3 / 10"
        />
        <el-table-column
          prop="contractNum"
          sortable
          label="签约合同"
          :width="3 / 10"
        />
      </el-table>
    </div>
  </myCard>
</template>

<script lang="ts" setup>
import { getMonthPerformanceRank } from '@/apis/home'
import { onMounted, ref } from 'vue'
const MonthPerformance = ref()
const tableData = ref<any[]>([])
onMounted(async () => {
  const data: any = await getMonthPerformanceRank()
  if (data.code === 200 && data.data) {
    MonthPerformance.value = data.data
    tableData.value = data.data.saleRankList
  }
})
</script>

<style></style>
