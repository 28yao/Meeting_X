<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

onMounted(async () => {
  if (auth.isAuthenticated || (await auth.restoreSession())) {
    router.replace({ name: 'book' })
  } else {
    router.replace({ name: 'login' })
  }
})
</script>

<template>
  <div class="home-placeholder">
    <p>正在跳转…</p>
  </div>
</template>

<style scoped>
.home-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  color: var(--app-text-secondary);
}
</style>
