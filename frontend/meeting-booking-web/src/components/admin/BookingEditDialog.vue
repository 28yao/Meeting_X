<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listAllAdminRooms, type AdminRoom } from '../../api/adminRoom'
import type { AdminBookingItem, UpdateAdminBookingRequest } from '../../api/adminBooking'

const props = defineProps<{
  visible: boolean
  booking: AdminBookingItem | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: [payload: UpdateAdminBookingRequest]
}>()

const formRef = ref<FormInstance>()
const rooms = ref<AdminRoom[]>([])
const form = reactive({
  roomId: 0,
  date: '',
  startTime: '',
  endTime: '',
})

const rules: FormRules = {
  roomId: [{ required: true, message: '请选择会议室', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
}

const timeOptions = computed(() => {
  const options: string[] = []
  for (let h = 0; h < 24; h++) {
    for (let m = 0; m < 60; m += 15) {
      options.push(`${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`)
    }
  }
  return options
})

const normalRooms = computed(() => rooms.value.filter((r) => r.status === 'NORMAL'))

function parseIso(iso: string) {
  const normalized = iso.replace('T', ' ')
  const date = normalized.substring(0, 10)
  const time = normalized.length >= 16 ? normalized.substring(11, 16) : '10:00'
  return { date, time }
}

function buildIso(date: string, time: string) {
  return `${date}T${time}:00`
}

watch(
  () => props.visible,
  async (open) => {
    if (!open || !props.booking) {
      return
    }
    const start = parseIso(props.booking.startTime)
    const end = parseIso(props.booking.endTime)
    form.roomId = props.booking.roomId
    form.date = start.date
    form.startTime = start.time
    form.endTime = end.time
    rooms.value = await listAllAdminRooms()
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
  if (form.startTime >= form.endTime) {
    ElMessage.warning('结束时间必须晚于开始时间')
    return
  }
  emit('submit', {
    roomId: form.roomId,
    startTime: buildIso(form.date, form.startTime),
    endTime: buildIso(form.date, form.endTime),
  })
}
</script>

<template>
  <el-dialog :model-value="visible" title="编辑预约" width="520px" @close="onClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
      <el-form-item label="会议室" prop="roomId">
        <el-select v-model="form.roomId" style="width: 100%">
          <el-option
            v-for="r in normalRooms"
            :key="r.id"
            :label="r.name"
            :value="r.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="日期" prop="date">
        <el-date-picker
          v-model="form.date"
          type="date"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="开始" prop="startTime">
        <el-select v-model="form.startTime" style="width: 100%">
          <el-option v-for="t in timeOptions" :key="'s-' + t" :label="t" :value="t" />
        </el-select>
      </el-form-item>
      <el-form-item label="结束" prop="endTime">
        <el-select v-model="form.endTime" style="width: 100%">
          <el-option v-for="t in timeOptions" :key="'e-' + t" :label="t" :value="t" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" @click="onConfirm">保存</el-button>
    </template>
  </el-dialog>
</template>
