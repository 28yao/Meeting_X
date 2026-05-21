<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import * as authApi from '../api/auth'

const router = useRouter()
const auth = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const errorMessage = ref('')

const form = reactive({
  username: '',
  displayName: '',
  password: '',
  confirmPassword: '',
})

const validateConfirmPassword = (_rule: unknown, value: string, callback: (err?: Error) => void) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 64, message: '密码长度须在 6～64 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
  displayName: [{ max: 128, message: '显示名称过长', trigger: 'blur' }],
}

async function onSubmit() {
  if (!formRef.value) {
    return
  }
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }
  loading.value = true
  errorMessage.value = ''
  try {
    const res = await authApi.register({
      username: form.username.trim(),
      password: form.password,
      confirmPassword: form.confirmPassword,
      displayName: form.displayName.trim() || undefined,
    })
    if (res.code !== 0 || !res.data) {
      throw new Error(res.message || '注册失败')
    }
    auth.persistSession(res.data.token, res.data.user)
    router.replace('/book')
  } catch (err: unknown) {
    const axiosErr = err as { response?: { data?: { message?: string } }; message?: string }
    errorMessage.value =
      axiosErr.response?.data?.message || axiosErr.message || '注册失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page">
    <el-card class="register-card" shadow="hover">
      <template #header>
        <span class="register-title">注册账号</span>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="84px" @submit.prevent="onSubmit">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" autocomplete="username" />
        </el-form-item>
        <el-form-item label="显示姓名" prop="displayName">
          <el-input v-model="form.displayName" placeholder="选填，默认等于账号" autocomplete="name" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="6～64 位"
            show-password
            autocomplete="new-password"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            autocomplete="new-password"
            @keyup.enter="onSubmit"
          />
        </el-form-item>
        <el-alert
          v-if="errorMessage"
          :title="errorMessage"
          type="error"
          show-icon
          :closable="false"
          class="register-error"
        />
        <el-form-item>
          <el-button type="primary" :loading="loading" native-type="submit" style="width: 100%">
            注册
          </el-button>
        </el-form-item>
        <div class="login-link">
          已有账号？<router-link to="/login">去登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  background: var(--app-bg);
}

.register-card {
  width: 420px;
  max-width: 92vw;
  border: 1px solid var(--app-border-light);
}

.register-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--app-text);
  letter-spacing: -0.02em;
}

.register-error {
  margin-bottom: 16px;
}

.login-link {
  margin-top: 12px;
  font-size: 13px;
  color: var(--app-text-muted);
  text-align: center;
}

.login-link a {
  color: var(--app-primary);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s ease;
}

.login-link a:hover {
  color: var(--app-primary-dark);
  text-decoration: underline;
}
</style>
