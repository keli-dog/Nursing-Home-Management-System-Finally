<template>
  <div class="body">
    <div class="container">
      <h1>404</h1>
      <p>Oops! The page you are looking for cannot be found.</p>
      <p><a @click="toHome">Return to homepage</a></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import router from "@/router";

const toHome = () => {
  router.push({ path: "/" });
};
</script>

<style lang="scss" scoped>
.body {
  height: 100%;
  background-color: #f1f1f1;
  font-family: Arial, sans-serif;

  .container {
    max-width: 800px;
    margin: 0 auto;
    text-align: center;
    padding: 50px;

    h1 {
      font-size: 72px;
      margin-bottom: 0;
    }

    p {
      font-size: 24px;
      margin-top: 0;
    }

    a {
      color: #333;
      text-decoration: none;
    }

    a:hover {
      text-decoration: underline;
    }
  }
}
</style>
