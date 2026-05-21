package com.meeting.booking.booking;

import com.meeting.booking.booking.dto.BookingMineRow;
import com.meeting.booking.booking.dto.MyBookingItemDto;
import com.meeting.booking.common.PagingDefaults;
import com.meeting.booking.common.dto.PageResult;
import com.meeting.booking.booking.mapper.BookingMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 我的预约查询服务：展示状态推导、排序与分页。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Service
public class BookingQueryService {

    public static final int DEFAULT_PAGE_SIZE = PagingDefaults.DEFAULT_PAGE_SIZE;

    private final BookingMapper bookingMapper;

    public BookingQueryService(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    /**
     * 分页查询当前用户预约列表。
     *
     * @param organizerId 组织者用户 ID
     * @param page        页码，从 1 开始
     * @return 分页结果
     */
    public PageResult<MyBookingItemDto> listMine(Long organizerId, int page) {
        int safePage = page < 1 ? 1 : page;
        List<BookingMineRow> rows = bookingMapper.selectByOrganizerIdWithRoom(organizerId);
        LocalDateTime now = LocalDateTime.now();
        List<MyBookingItemDto> allItems = new ArrayList<MyBookingItemDto>();
        for (BookingMineRow row : rows) {
            allItems.add(toItemDto(row, now));
        }
        Collections.sort(allItems, buildComparator());
        int from = (safePage - 1) * DEFAULT_PAGE_SIZE;
        int to = Math.min(from + DEFAULT_PAGE_SIZE, allItems.size());
        List<MyBookingItemDto> pageItems = from >= allItems.size()
                ? Collections.<MyBookingItemDto>emptyList()
                : allItems.subList(from, to);

        PageResult<MyBookingItemDto> result = new PageResult<MyBookingItemDto>();
        result.setItems(pageItems);
        result.setPage(safePage);
        result.setPageSize(DEFAULT_PAGE_SIZE);
        result.setTotal(allItems.size());
        return result;
    }

    private MyBookingItemDto toItemDto(BookingMineRow row, LocalDateTime now) {
        String displayStatus = BookingDisplayStatus.derive(
                row.getStatus(), row.getStartTime(), row.getEndTime(), now);
        MyBookingItemDto dto = new MyBookingItemDto();
        dto.setBookingId(row.getId());
        dto.setTitle(row.getTitle());
        dto.setRoomId(row.getRoomId());
        dto.setRoomName(row.getRoomName());
        dto.setStartTime(row.getStartTime());
        dto.setEndTime(row.getEndTime());
        dto.setStatus(row.getStatus());
        dto.setDisplayStatus(displayStatus);
        dto.setCancellable(BookingDisplayStatus.cancellable(row.getStatus(), displayStatus));
        return dto;
    }

    private Comparator<MyBookingItemDto> buildComparator() {
        return new Comparator<MyBookingItemDto>() {
            @Override
            public int compare(MyBookingItemDto a, MyBookingItemDto b) {
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
