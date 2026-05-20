package com.meeting.booking.room.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * 管理员更新会议室请求体。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
public class UpdateAdminRoomRequest {

    @Size(max = 128, message = "会议室名称过长")
    private String name;

    @Min(value = 1, message = "容量至少为 1")
    private Integer capacity;

    @Size(max = 64, message = "楼层信息过长")
    private String floor;

    @Size(max = 64, message = "类型过长")
    private String roomType;

    @Size(max = 512, message = "设备描述过长")
    private String equipment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
}
