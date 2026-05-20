<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelBooking,
  listMyBookings,
  type MyBookingItem,
} from '../api/booking'
import { resolveAxiosError } from '../utils/errorMessages'
import {
  formatDateTime,
  getStatusLabel,
  getStatusTagType,
  type BookingDisplayStatus,
} from '../utils/bookingStatus'

const loading = ref(false)
const cancellingId = ref<number | null>(null)
const items = ref<MyBookingItem[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

async function loadList() {
  loading.value = true
  try {
    const res = await listMyBookings(page.value)
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

async function onCancel(row: MyBookingItem) {
  try {
    await ElMessageBox.confirm(
      `确定取消预约「${row.title}」吗？取消后该时段将释放。`,
      '取消预约',
      { type: 'warning', confirmButtonText: '确定取消', cancelButtonText: '返回' },
    )
  } catch {
    return
  }

  cancellingId.value = row.bookingId
  try {
    const res = await cancelBooking(row.bookingId)
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
  <div class="my-bookings">
    <h2 class="page-title">我的预约</h2>
    <el-card v-loading="loading" shadow="never">
      <el-empty v-if="!loading && items.length === 0" description="暂无预约记录" />
      <el-table v-else :data="items" stripe style="width: 100%">
        <el-table-column prop="title" label="会议主题" min-width="140" />
        <el-table-column prop="roomName" label="会议室" min-width="120" />
        <el-table-column label="开始时间" min-width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" min-width="160">
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
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.cancellable"
              type="danger"
              link
              :loading="cancellingId === row.bookingId"
              @click="onCancel(row)"
            >
              取消
            </el-button>
            <span v-else class="no-action">—</span>
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
  </div>
</template>

<style scoped>
.my-bookings {
  max-width: 960px;
  margin: 0 auto;
}

.page-title {
  margin: 0 0 16px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.no-action {
  color: #c0c4cc;
}
</style>
