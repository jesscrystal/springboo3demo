<script setup>

</script>

<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex;">
    <div style="flex: 1;">
      <el-image style="width: 100%;height: 100%;" src="/fj.webp"></el-image>
    </div>
    <div class="weicome-title">
        <div style="font-size: 30px;font-weight: bold">欢迎使用智能云学习平台</div>
        <div style="margin-top: 10px">在这里你可以学习如何使用Java，如何搭建网站，本网站为爱心发电不含有任何收费项目</div>
    </div>
    <div style="width: 400px;background-color: white">
      <transition name="el-fade-in-linear">
        <router-view/>
      </transition>
    </div>
  </div>
</template>

<style scoped>
  .weicome-title{
    position: absolute;
    bottom: 30px;
    left: 30px;
    color: white;
    text-shadow: 0 0 10px black;
  }
</style>