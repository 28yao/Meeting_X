<script setup lang="ts">
import type { MeetingRoom } from '../../api/room'

defineProps<{
  rooms: MeetingRoom[]
  loading: boolean
  selectedRoomId: number | null
}>()

const emit = defineEmits<{
  select: [room: MeetingRoom]
}>()
</script>

<template>
  <div v-loading="loading" class="rooms-step">
    <el-empty v-if="!loading && rooms.length === 0" description="该时段暂无空闲会议室" />
    <el-row v-else :gutter="16">
      <el-col v-for="room in rooms" :key="room.id" :xs="24" :sm="12" :md="8">
        <el-card
          class="room-card"
          :class="{ 'room-card--selected': selectedRoomId === room.id }"
          shadow="hover"
          @click="emit('select', room)"
        >
          <div class="room-name">{{ room.name }}</div>
          <div class="room-meta">容纳 {{ room.capacity }} 人 · {{ room.floor || '—' }}</div>
          <div class="room-meta">{{ room.roomType || '普通会议室' }}</div>
          <div v-if="room.equipment" class="room-equipment">{{ room.equipment }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.rooms-step {
  min-height: 200px;
}

.room-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  border: 1px solid var(--app-border-light);
}

.room-card:hover {
  box-shadow: var(--app-shadow-md);
  border-color: var(--app-border);
}

.room-card--selected {
  border: 2px solid var(--app-primary);
  box-shadow: var(--app-shadow-sm);
  background: var(--app-surface-muted);
}

.room-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--app-text);
}

.room-meta {
  font-size: 13px;
  color: var(--app-text-secondary);
  margin-bottom: 4px;
}

.room-equipment {
  font-size: 12px;
  color: var(--app-text-muted);
  margin-top: 8px;
  line-height: 1.5;
}
</style>
