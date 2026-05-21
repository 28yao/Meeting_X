<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getAvailableRooms, type MeetingRoom } from '../api/room'
import { createBooking } from '../api/booking'
import { useNotificationStore } from '../stores/notification'
import { resolveAxiosError } from '../utils/errorMessages'
import AvailableRoomsStep from './book/AvailableRoomsStep.vue'
import ConfirmStep from './book/ConfirmStep.vue'

const activeStep = ref(0)
const loadingRooms = ref(false)
const submitting = ref(false)
const notificationStore = useNotificationStore()

const form = reactive({
  date: '',
  startTime: '',
  endTime: '',
})

const rooms = ref<MeetingRoom[]>([])
const selectedRoom = ref<MeetingRoom | null>(null)
const title = ref('')

const timeOptions = computed(() => {
  const options: string[] = []
  for (let h = 0; h < 24; h++) {
    for (let m = 0; m < 60; m += 15) {
      const hh = String(h).padStart(2, '0')
      const mm = String(m).padStart(2, '0')
      options.push(`${hh}:${mm}`)
    }
  }
  return options
})

const disabledDate = (date: Date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const max = new Date()
  max.setDate(max.getDate() + 30)
  return date < today || date > max
}

function initDefaultDate() {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  const y = tomorrow.getFullYear()
  const m = String(tomorrow.getMonth() + 1).padStart(2, '0')
  const d = String(tomorrow.getDate()).padStart(2, '0')
  form.date = `${y}-${m}-${d}`
  form.startTime = '10:00'
  form.endTime = '10:30'
}

initDefaultDate()

async function loadRooms() {
  if (!form.date || !form.startTime || !form.endTime) {
    rooms.value = []
    return
  }
  loadingRooms.value = true
  selectedRoom.value = null
  try {
    const res = await getAvailableRooms(form.date, form.startTime, form.endTime)
    if (res.code !== 0) {
      ElMessage.error(res.message || '查询失败')
      rooms.value = []
      return
    }
    rooms.value = res.data || []
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
    rooms.value = []
  } finally {
    loadingRooms.value = false
  }
}

// 自动加载：日期或时间变更时重新查询会议室
watch(
  () => [form.date, form.startTime, form.endTime],
  () => {
    loadRooms()
  }
)

function onSelectRoom(room: MeetingRoom) {
  selectedRoom.value = room
}

function toDateTime(date: string, time: string): string {
  return `${date}T${time}:00`
}

function goNext() {
  if (activeStep.value === 0) {
    if (!form.date || !form.startTime || !form.endTime) {
      ElMessage.warning('请选择日期与时段')
      return
    }
    if (form.startTime >= form.endTime) {
      ElMessage.warning('结束时间必须晚于开始时间')
      return
    }
    if (!selectedRoom.value) {
      ElMessage.warning('请选择一间会议室')
      return
    }
    activeStep.value = 1
    return
  }
  if (activeStep.value === 1) {
    onSubmit()
  }
}

function goPrev() {
  if (activeStep.value > 0) {
    activeStep.value -= 1
  }
}

async function onSubmit() {
  if (!title.value.trim()) {
    ElMessage.warning('请填写会议主题')
    return
  }
  if (!selectedRoom.value) {
    ElMessage.warning('请选择会议室')
    return
  }
  submitting.value = true
  try {
    const res = await createBooking({
      roomId: selectedRoom.value.id,
      title: title.value.trim(),
      startTime: toDateTime(form.date, form.startTime),
      endTime: toDateTime(form.date, form.endTime),
    })
    if (res.code !== 0) {
      ElMessage.error(res.message || '预约失败')
      return
    }
    ElMessage.success('预约成功！')
    await notificationStore.refreshUnreadCount()
    activeStep.value = 0
    title.value = ''
    selectedRoom.value = null
    loadRooms()
  } catch (err) {
    ElMessage.error(resolveAxiosError(err))
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="book-view">
    <el-card shadow="never">
      <el-steps :active="activeStep" align-center finish-status="success" class="book-steps">
        <el-step title="选择时间与会议室" />
        <el-step title="确认预约" />
      </el-steps>

      <div v-show="activeStep === 0" class="step-panel">
        <el-form label-width="90px" class="time-form">
          <el-form-item label="会议日期" required>
            <el-date-picker
              v-model="form.date"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledDate"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="开始时间" required>
            <el-select v-model="form.startTime" placeholder="开始时间" style="width: 100%">
              <el-option v-for="t in timeOptions" :key="'s-' + t" :label="t" :value="t" />
            </el-select>
          </el-form-item>
          <el-form-item label="结束时间" required>
            <el-select v-model="form.endTime" placeholder="结束时间" style="width: 100%">
              <el-option v-for="t in timeOptions" :key="'e-' + t" :label="t" :value="t" />
            </el-select>
          </el-form-item>
        </el-form>

        <el-divider />

        <AvailableRoomsStep
          :rooms="rooms"
          :loading="loadingRooms"
          :selected-room-id="selectedRoom?.id ?? null"
          @select="onSelectRoom"
        />
      </div>

      <div v-show="activeStep === 1" class="step-panel">
        <ConfirmStep
          :date="form.date"
          :start-time="form.startTime"
          :end-time="form.endTime"
          :room="selectedRoom"
          :title="title"
          @update:title="title = $event"
        />
      </div>

      <div class="step-actions">
        <el-button v-if="activeStep > 0" @click="goPrev">上一步</el-button>
        <el-button v-if="activeStep < 1" type="primary" @click="goNext">下一步</el-button>
        <el-button
          v-if="activeStep === 1"
          type="primary"
          :loading="submitting"
          @click="onSubmit"
        >
          确认预约
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.book-view {
  max-width: 960px;
  margin: 0 auto;
}

.book-steps {
  margin-bottom: 32px;
}

.step-panel {
  min-height: 240px;
  padding: 8px 0 24px;
}

.time-form {
  max-width: 420px;
  margin: 0 auto;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
