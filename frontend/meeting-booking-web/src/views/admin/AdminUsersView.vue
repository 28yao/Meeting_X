<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import UserFormDialog from '../../components/admin/UserFormDialog.vue'
import {
  createAdminUser,
  listAdminUsers,
  resetAdminUserPassword,
  updateAdminUser,
  type AdminUser,
  type CreateAdminUserRequest,
  type UpdateAdminUserRequest,
} from '../../api/adminUser'
import { resolveAxiosError } from '../../utils/errorMessages'

const loading = ref(false)
const users = ref<AdminUser[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingUser = ref<AdminUser | null>(null)

async function loadUsers() {
  loading.value = true
  try {
    const res = await listAdminUsers()
    if (res.code !== 0) {
      ElMessage.error(res.message || '加载失败')
      return
    }
    users.value = res.data ?? []
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  } finally {
    loading.value = false
  }
}

function openCreate() {
  dialogMode.value = 'create'
  editingUser.value = null
  dialogVisible.value = true
}

function openEdit(row: AdminUser) {
  dialogMode.value = 'edit'
  editingUser.value = row
  dialogVisible.value = true
}

async function onFormSubmit(payload: CreateAdminUserRequest | UpdateAdminUserRequest) {
  try {
    if (dialogMode.value === 'create') {
      const res = await createAdminUser(payload as CreateAdminUserRequest)
      if (res.code !== 0) {
        ElMessage.error(res.message || '创建失败')
        return
      }
      ElMessage.success('用户已创建')
    } else if (editingUser.value) {
      const res = await updateAdminUser(editingUser.value.id, payload as UpdateAdminUserRequest)
      if (res.code !== 0) {
        ElMessage.error(res.message || '更新失败')
        return
      }
      ElMessage.success('用户已更新')
    }
    dialogVisible.value = false
    await loadUsers()
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  }
}

async function onResetPassword(row: AdminUser) {
  try {
    await ElMessageBox.confirm(
      `确定将用户「${row.username}」的密码重置为默认密码 123456 吗？`,
      '重置密码',
      { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' },
    )
    const res = await resetAdminUserPassword(row.id)
    if (res.code !== 0) {
      ElMessage.error(res.message || '重置失败')
      return
    }
    ElMessage.success('密码已重置为 123456')
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error(resolveAxiosError(err))
    }
  }
}

function roleLabel(role: string) {
  return role === 'ADMIN' ? '管理员' : '员工'
}

onMounted(() => {
  loadUsers()
})
</script>

<template>
  <div class="admin-users">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-button type="primary" @click="openCreate">新建用户</el-button>
    </div>
    <el-card v-loading="loading" shadow="never">
      <el-table :data="users" stripe style="width: 100%">
        <el-table-column prop="username" label="登录账号" min-width="120" />
        <el-table-column prop="displayName" label="显示名称" min-width="120" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" size="small">
              {{ roleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" size="small">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="onResetPassword(row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <UserFormDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :user="editingUser"
      @submit="onFormSubmit"
    />
  </div>
</template>

<style scoped>
.admin-users {
  max-width: 960px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}
</style>
