<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { AdminRoom, CreateAdminRoomRequest, UpdateAdminRoomRequest } from '../../api/adminRoom'

const props = defineProps<{
  visible: boolean
  mode: 'create' | 'edit'
  room: AdminRoom | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: [payload: CreateAdminRoomRequest | UpdateAdminRoomRequest]
}>()

const formRef = ref<FormInstance>()
const form = reactive({
  name: '',
  capacity: 10,
  floor: '',
  roomType: '',
  equipment: '',
})

const title = computed(() => (props.mode === 'create' ? '新增会议室' : '编辑会议室'))

const rules: FormRules = {
  name: [{ required: true, message: '请输入会议室名称', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }],
}

watch(
  () => props.visible,
  (open) => {
    if (!open) {
      return
    }
    if (props.mode === 'edit' && props.room) {
      form.name = props.room.name
      form.capacity = props.room.capacity
      form.floor = props.room.floor ?? ''
      form.roomType = props.room.roomType ?? ''
      form.equipment = props.room.equipment ?? ''
    } else {
      form.name = ''
      form.capacity = 10
      form.floor = ''
      form.roomType = ''
      form.equipment = ''
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
  const payload = {
    name: form.name.trim(),
    capacity: form.capacity,
    floor: form.floor.trim() || undefined,
    roomType: form.roomType.trim() || undefined,
    equipment: form.equipment.trim() || undefined,
  }
  emit('submit', payload)
}
</script>

<template>
  <el-dialog :model-value="visible" :title="title" width="520px" @close="onClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="如：三号会议室" />
      </el-form-item>
      <el-form-item label="容量" prop="capacity">
        <el-input-number v-model="form.capacity" :min="1" :max="500" style="width: 100%" />
      </el-form-item>
      <el-form-item label="楼层/位置">
        <el-input v-model="form.floor" placeholder="如：3F 东侧" />
      </el-form-item>
      <el-form-item label="类型">
        <el-input v-model="form.roomType" placeholder="如：中型、视频会议室" />
      </el-form-item>
      <el-form-item label="设备">
        <el-input v-model="form.equipment" type="textarea" :rows="2" placeholder="投影、白板等" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" @click="onConfirm">保存</el-button>
    </template>
  </el-dialog>
</template>
