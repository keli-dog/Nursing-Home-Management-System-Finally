<template>
  <myCard title="业务趋势" class="w-full">
    <div
      id="myEcharts"
      style="width: 1000px; height: 333px"
      ref="Echarts"
      class="w-full"
    ></div>
  </myCard>
</template>

<script lang="ts" setup>
import * as echarts from 'echarts'
import { onMounted, onUnmounted, ref } from 'vue'
import { getBusinessTrend } from '@/apis/home'
let myEcharts = echarts
let trendData = ref<any[]>([])
let Echarts = ref()
onMounted(async () => {
  const data: any = await getBusinessTrend()
  if (data.code === 200 && data.data) {
    trendData.value = data.data
  }
  initChart()
})

onUnmounted(() => {
  myEcharts.dispose
})

function initChart() {
  let chart = myEcharts.init(Echarts.value, 'purple-passion')
  chart.setOption({
    xAxis: {
      type: 'category',
      data: trendData.value.map((item: any) => item.month)
    },
    tooltip: {
      trigger: 'axis'
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: trendData.value.map((item: any) => item.consultNum),
        type: 'line',
        smooth: true,
        label: {
          show: true,
          position: 'top',
          formatter: '{c}'
        }
      }
    ]
  })
  window.onresize = function () {
    chart.resize()
  }
}
</script>

<style lang="scss" scoped>
#myEcharts {
  width: 100% !important;
}
</style>
