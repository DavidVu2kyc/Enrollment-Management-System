package com.obu.ems.mapper;

import com.obu.ems.dto.SectionResponse;
import com.obu.ems.model.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {
    public SectionResponse.RoomResponse mapToRoomResponse(Room room) {
        if (room == null) {
            return null;
        }
        return SectionResponse.RoomResponse.builder()
                .roomId(room.getRoomId())
                .building(room.getBuilding())
                .roomNumber(room.getRoomNumber())
                .build();
    }
}
