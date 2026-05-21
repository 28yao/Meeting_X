<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BookingEditDialog from '../../components/admin/BookingEditDialog.vue'
import {
  cancelAdminBooking,
  listAdminBookings,
  updateAdminBooking,
  type AdminBookingItem,
  type UpdateAdminBookingRequest,
} from '../../api/adminBooking'
import { resolveAxiosError } from '../../utils/errorMessages'
import {
  formatDateTime,
  getStatusLabel,
  getStatusTagType,
  type BookingDisplayStatus,
} from '../../utils/bookingStatus'

const loading = ref(false)
const items = ref<AdminBookingItem[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const dialogVisible = ref(false)
const editingBooking = ref<AdminBookingItem | null>(null)
const cancellingId = ref<number | null>(null)

async function loadList() {
  loading.value = true
  try {
    const res = await listAdminBookings(page.value)
    if (res.code !== 0) {
      ElMessage.error(res.message || '加载失败')
      return
    }
    items.value = res.data?.items ?? []
    pageSize.value = res.data?.pageSize ?? 20
    total.value = res.data?.total ?? 0
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  } finally {
    loading.value = false
  }
}

function openEdit(row: AdminBookingItem) {
  editingBooking.value = row
  dialogVisible.value = true
}

async function onEditSubmit(payload: UpdateAdminBookingRequest) {
  if (!editingBooking.value) {
    return
  }
  try {
    const res = await updateAdminBooking(editingBooking.value.bookingId, payload)
    if (res.code !== 0) {
      ElMessage.error(res.message || '保存失败')
      return
    }
    ElMessage.success('预约已更新')
    dialogVisible.value = false
    await loadList()
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  }
}

async function onCancel(row: AdminBookingItem) {
  try {
    await ElMessageBox.confirm(
      `确定取消预约「${row.title}」吗？组织者将收到通知。`,
      '取消预约',
      { type: 'warning', confirmButtonText: '确定取消', cancelButtonText: '返回' },
    )
  } catch {
    return
  }
  cancellingId.value = row.bookingId
  try {
    const res = await cancelAdminBooking(row.bookingId)
    if (res.code !== 0) {
      ElMessage.error(res.message || '取消失败')
      return
    }
    ElMessage.success('预约已取消')
    await loadList()
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  } finally {
    cancellingId.value = null
  }
}

function onPageChange(newPage: number) {
  page.value = newPage
  loadList()
}

onMounted(() => {
  loadList()
})
</script>

<template>
  <div class="admin-bookings">
    <h2 class="page-title">预约管理</h2>
    <el-card v-loading="loading" shadow="never">
      <el-empty v-if="!loading && items.length === 0" description="暂无预约记录" />
      <el-table v-else :data="items" stripe style="width: 100%">
        <el-table-column prop="title" label="会议主题" min-width="120" />
        <el-table-column prop="roomName" label="会议室" min-width="110" />
        <el-table-column prop="organizerDisplayName" label="组织者" min-width="100" />
        <el-table-column label="开始时间" min-width="150">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" min-width="150">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getStatusTagType(row.displayStatus as BookingDisplayStatus)"
              size="small"
            >
              {{ getStatusLabel(row.displayStatus as BookingDisplayStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.editable" type="primary" link @click="openEdit(row)">
              编辑
            </el-button>
            <el-button
              v-if="row.cancellable"
              type="danger"
              link
              :loading="cancellingId === row.bookingId"
              @click="onCancel(row)"
            >
              取消
            </el-button>
            <span v-if="!row.editable && !row.cancellable" class="no-action">—</span>
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
    <BookingEditDialog
      v-model:visible="dialogVisible"
      :booking="editingBooking"
      @submit="onEditSubmit"
    />
  </div>
</template>

<style scoped>
.admin-bookings {
  max-width: 1100px;
  margin: 0 auto;
}

.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
