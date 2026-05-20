package com.meeting.booking.booking;

import com.meeting.booking.booking.dto.AdminBookingItemDto;
import com.meeting.booking.booking.dto.AdminBookingRow;
import com.meeting.booking.booking.dto.PageResult;
import com.meeting.booking.booking.mapper.BookingMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 管理员全公司预约列表查询服务。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class AdminBookingQueryService {

    private final BookingMapper bookingMapper;

    public AdminBookingQueryService(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    /**
     * 分页查询全公司预约列表。
     *
     * @param page 页码，从 1 开始
     * @return 分页结果
     */
    public PageResult<AdminBookingItemDto> listAll(int page) {
        int safePage = page < 1 ? 1 : page;
        List<AdminBookingRow> rows = bookingMapper.selectAllWithRoomAndOrganizer();
        if (rows == null) {
            rows = Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now();
        List<AdminBookingItemDto> allItems = new ArrayList<AdminBookingItemDto>(rows.size());
        for (AdminBookingRow row : rows) {
            allItems.add(toItemDto(row, now));
        }
        Collections.sort(allItems, buildComparator());
        int from = (safePage - 1) * BookingQueryService.DEFAULT_PAGE_SIZE;
        int to = Math.min(from + BookingQueryService.DEFAULT_PAGE_SIZE, allItems.size());
        List<AdminBookingItemDto> pageItems = from >= allItems.size()
                ? Collections.<AdminBookingItemDto>emptyList()
                : allItems.subList(from, to);

        PageResult<AdminBookingItemDto> result = new PageResult<AdminBookingItemDto>();
        result.setItems(pageItems);
        result.setPage(safePage);
        result.setPageSize(BookingQueryService.DEFAULT_PAGE_SIZE);
        result.setTotal(allItems.size());
        return result;
    }

    private AdminBookingItemDto toItemDto(AdminBookingRow row, LocalDateTime now) {
        String displayStatus = BookingDisplayStatus.derive(
                row.getStatus(), row.getStartTime(), row.getEndTime(), now);
        boolean modifiable = BookingDisplayStatus.cancellable(row.getStatus(), displayStatus);
        AdminBookingItemDto dto = new AdminBookingItemDto();
        dto.setBookingId(row.getId());
        dto.setTitle(row.getTitle());
        dto.setRoomId(row.getRoomId());
        dto.setRoomName(row.getRoomName());
        dto.setOrganizerId(row.getOrganizerId());
        dto.setOrganizerDisplayName(row.getOrganizerDisplayName());
        dto.setStartTime(row.getStartTime());
        dto.setEndTime(row.getEndTime());
        dto.setStatus(row.getStatus());
        dto.setDisplayStatus(displayStatus);
        dto.setEditable(modifiable);
        dto.setCancellable(modifiable);
        return dto;
    }

    private Comparator<AdminBookingItemDto> buildComparator() {
        return new Comparator<AdminBookingItemDto>() {
            @Override
            public int compare(AdminBookingItemDto a, AdminBookingItemDto b) {
                int groupCompare = Integer.compare(
                        BookingDisplayStatus.groupOrder(a.getDisplayStatus()),
                        BookingDisplayStatus.groupOrder(b.getDisplayStatus()));
                if (groupCompare != 0) {
                    return groupCompare;
                }
                if (BookingDisplayStatus.UPCOMING.equals(a.getDisplayStatus())) {
                    return a.getStartTime().compareTo(b.getStartTime());
                }
                return b.getStartTime().compareTo(a.getStartTime());
            }
        };
    }
}
