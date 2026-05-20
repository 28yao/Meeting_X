<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  router.replace({ name: 'login' })
})
</script>

<template>
  <div class="home-placeholder">
    <p>正在跳转到登录页…</p>
  </div>
</template>

<style scoped>
.home-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  color: #606266;
}
</style>
