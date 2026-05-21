<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import RoomFormDialog from '../../components/admin/RoomFormDialog.vue'
import {
  createAdminRoom,
  deleteAdminRoom,
  listAdminRooms,
  updateAdminRoom,
  updateAdminRoomStatus,
  type AdminRoom,
  type CreateAdminRoomRequest,
  type UpdateAdminRoomRequest,
} from '../../api/adminRoom'
import { resolveAxiosError } from '../../utils/errorMessages'

const loading = ref(false)
const rooms = ref<AdminRoom[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingRoom = ref<AdminRoom | null>(null)

async function loadRooms() {
  loading.value = true
  try {
    const res = await listAdminRooms(page.value)
    if (res.code !== 0) {
      ElMessage.error(res.message || '加载失败')
      return
    }
    rooms.value = res.data?.items ?? []
    pageSize.value = res.data?.pageSize ?? 20
    total.value = res.data?.total ?? 0
    if (rooms.value.length === 0 && page.value > 1) {
      page.value--
      loading.value = false
      await loadRooms()
      return
    }
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  } finally {
    loading.value = false
  }
}

function onPageChange(newPage: number) {
  page.value = newPage
  loadRooms()
}

function openCreate() {
  dialogMode.value = 'create'
  editingRoom.value = null
  dialogVisible.value = true
}

function openEdit(row: AdminRoom) {
  dialogMode.value = 'edit'
  editingRoom.value = row
  dialogVisible.value = true
}

async function onFormSubmit(payload: CreateAdminRoomRequest | UpdateAdminRoomRequest) {
  try {
    if (dialogMode.value === 'create') {
      const res = await createAdminRoom(payload as CreateAdminRoomRequest)
      if (res.code !== 0) {
        ElMessage.error(res.message || '创建失败')
        return
      }
      ElMessage.success('会议室已创建')
    } else if (editingRoom.value) {
      const res = await updateAdminRoom(editingRoom.value.id, payload as UpdateAdminRoomRequest)
      if (res.code !== 0) {
        ElMessage.error(res.message || '更新失败')
        return
      }
      ElMessage.success('会议室已更新')
    }
    dialogVisible.value = false
    await loadRooms()
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  }
}

async function onToggleMaintenance(row: AdminRoom) {
  const toMaintenance = row.status === 'NORMAL'
  const action = toMaintenance ? '设为维护中' : '恢复正常'
  try {
    await ElMessageBox.confirm(`确定将「${row.name}」${action}吗？`, action, {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    })
    const status = toMaintenance ? 'MAINTENANCE' : 'NORMAL'
    const res = await updateAdminRoomStatus(row.id, status)
    if (res.code !== 0) {
      ElMessage.error(res.message || '操作失败')
      return
    }
    ElMessage.success(toMaintenance ? '已设为维护中' : '已恢复正常')
    await loadRooms()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error(resolveAxiosError(err))
    }
  }
}

async function onDelete(row: AdminRoom) {
  try {
    await ElMessageBox.confirm(
      `确定删除会议室「${row.name}」吗？删除后不可恢复。`,
      '删除会议室',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' },
    )
    const res = await deleteAdminRoom(row.id)
    if (res.code !== 0) {
      ElMessage.error(res.message || '删除失败')
      return
    }
    ElMessage.success('会议室已删除')
    await loadRooms()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error(resolveAxiosError(err))
    }
  }
}

function statusLabel(status: string) {
  return status === 'MAINTENANCE' ? '维护中' : '正常'
}

onMounted(() => {
  loadRooms()
})
</script>

<template>
  <div class="admin-rooms">
    <div class="page-header">
      <h2 class="page-title">会议室管理</h2>
      <el-button type="primary" @click="openCreate">新增会议室</el-button>
    </div>
    <el-card v-loading="loading" shadow="never">
      <el-table :data="rooms" stripe style="width: 100%">
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column prop="floor" label="楼层" min-width="100" />
        <el-table-column prop="roomType" label="类型" min-width="100" />
        <el-table-column prop="equipment" label="设备" min-width="140" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'MAINTENANCE' ? 'warning' : 'success'" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 'NORMAL'"
              type="warning"
              link
              @click="onToggleMaintenance(row)"
            >
              设为维护中
            </el-button>
            <el-button v-else type="success" link @click="onToggleMaintenance(row)">
              恢复正常
            </el-button>
            <el-button type="danger" link @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > pageSize" class="pager">
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page="page"
          :page-size="pageSize"
          :total="total"
          @current-change="onPageChange"
        />
      </div>
    </el-card>
    <RoomFormDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :room="editingRoom"
      @submit="onFormSubmit"
    />
  </div>
</template>

<style scoped>
.admin-rooms {
  max-width: 1100px;
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
}
</style>
