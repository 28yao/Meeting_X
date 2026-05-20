<script setup lang="ts">
import type { MeetingRoom } from '../../api/room'

defineProps<{
  date: string
  startTime: string
  endTime: string
  room: MeetingRoom | null
  title: string
}>()

const emit = defineEmits<{
  'update:title': [value: string]
}>()
</script>

<template>
  <div class="confirm-step">
    <el-descriptions title="预约信息确认" :column="1" border>
      <el-descriptions-item label="日期">{{ date }}</el-descriptions-item>
      <el-descriptions-item label="时段">{{ startTime }} — {{ endTime }}</el-descriptions-item>
      <el-descriptions-item label="会议室">{{ room?.name || '—' }}</el-descriptions-item>
      <el-descriptions-item label="位置">{{ room?.floor || '—' }}</el-descriptions-item>
    </el-descriptions>
    <el-form label-width="80px" class="title-form">
      <el-form-item label="会议主题" required>
        <el-input
          :model-value="title"
          placeholder="请输入会议主题"
          maxlength="100"
          show-word-limit
          @update:model-value="emit('update:title', $event)"
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.confirm-step {
  max-width: 560px;
}

.title-form {
  margin-top: 24px;
}
</style>
