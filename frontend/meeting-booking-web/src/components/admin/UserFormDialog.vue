<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { AdminUser, CreateAdminUserRequest, UpdateAdminUserRequest } from '../../api/adminUser'

const props = defineProps<{
  visible: boolean
  mode: 'create' | 'edit'
  user: AdminUser | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: [payload: CreateAdminUserRequest | UpdateAdminUserRequest]
}>()

const formRef = ref<FormInstance>()
const form = reactive({
  username: '',
  password: '',
  displayName: '',
  role: 'EMPLOYEE' as 'EMPLOYEE' | 'ADMIN',
  enabled: true,
})

const title = computed(() => (props.mode === 'create' ? '新建用户' : '编辑用户'))

const rules = computed<FormRules>(() => {
  const base: FormRules = {
    displayName: [{ required: true, message: '请输入显示名称', trigger: 'blur' }],
    role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  }
  if (props.mode === 'create') {
    base.username = [{ required: true, message: '请输入登录账号', trigger: 'blur' }]
    base.password = [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码至少 6 位', trigger: 'blur' },
    ]
  }
  return base
})

watch(
  () => props.visible,
  (open) => {
    if (!open) {
      return
    }
    if (props.mode === 'edit' && props.user) {
      form.username = props.user.username
      form.password = ''
      form.displayName = props.user.displayName
      form.role = props.user.role
      form.enabled = props.user.enabled
    } else {
      form.username = ''
      form.password = ''
      form.displayName = ''
      form.role = 'EMPLOYEE'
      form.enabled = true
    }
    formRef.value?.clearValidate()
  },
)

function onClose() {
  emit('update:visible', false)
}

async function onConfirm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  if (props.mode === 'create') {
    emit('submit', {
      username: form.username.trim(),
      password: form.password,
      displayName: form.displayName.trim(),
      role: form.role,
      enabled: form.enabled,
    })
  } else {
    emit('submit', {
      displayName: form.displayName.trim(),
      role: form.role,
      enabled: form.enabled,
    })
  }
}
</script>

<template>
  <el-dialog :model-value="visible" :title="title" width="480px" @close="onClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
      <el-form-item v-if="mode === 'create'" label="登录账号" prop="username">
        <el-input v-model="form.username" placeholder="员工登录用账号" />
      </el-form-item>
      <el-form-item v-else label="登录账号">
        <el-input v-model="form.username" disabled />
      </el-form-item>
      <el-form-item v-if="mode === 'create'" label="初始密码" prop="password">
        <el-input v-model="form.password" type="password" show-password placeholder="至少 6 位" />
      </el-form-item>
      <el-form-item label="显示名称" prop="displayName">
        <el-input v-model="form.displayName" placeholder="界面展示名称" />
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-select v-model="form.role" style="width: 100%">
          <el-option label="普通员工" value="EMPLOYEE" />
          <el-option label="管理员" value="ADMIN" />
        </el-select>
      </el-form-item>
      <el-form-item label="是否启用">
        <el-switch v-model="form.enabled" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" @click="onConfirm">保存</el-button>
    </template>
  </el-dialog>
</template>
