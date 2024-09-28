package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Room;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private Long roomId;
    private Long hotelId;
    private Integer roomNumber;
    private Double price;

    public RoomDTO(Room room) {
        this.roomId = room.getRoomId();
        this.hotelId = room.getHotel().getHotelId();
        this.roomNumber = room.getRoomNumber();
        this.price = room.getPrice();
    }

    @JsonIgnore
    public Room getAsEntity() {
        return Room.builder()
                .roomId(roomId)
                .roomNumber(roomNumber)
                .price(price)
                .build();
    }

    public static Set<RoomDTO> addToRoomList(Set<Room> rooms) {
        return rooms.stream().map(RoomDTO::new).collect(Collectors.toSet());
    }

}
