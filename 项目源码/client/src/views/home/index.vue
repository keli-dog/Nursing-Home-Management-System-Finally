<template>
  <div>
    <todayOverview/>
    <SoldBed class="flex-1 mt-2"/>
    <monthPerformance class="flex-1 mt-2"/>
    <trend class="flex mt-2"/>
  </div>
</template>

<script setup lang="ts">
import todaySold from './todaySold.vue'
import monthPerformance from './monthPerformance.vue'
import trend from './trend.vue'
import SoldBed from './soldBed.vue'
import todayOverview from './todayOverview.vue'
</script>

<style lang="scss" scoped>
.svg-container {
  padding: 6px 5px 6px 15px;
  color: #889aa4;
  vertical-align: middle;
  display: inline-block;
  font-size: 2rem;
}
</style>
