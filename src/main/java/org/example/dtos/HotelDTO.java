package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.Hotel;
import org.example.entities.Room;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {

    private Long id;
    private String hotelName;
    private String hotelAddress;
    private Set<RoomDTO> rooms;

    public HotelDTO(Hotel hotel) {
        this.id = hotel.getHotelId();
        this.hotelName = hotel.getHotelName();
        this.hotelAddress = hotel.getHotelAddress();
        this.rooms = hotel.getRooms().stream().map(RoomDTO::new) // converts Room to RoomDTO
                .collect(Collectors.toSet());
    }

    public static Set<HotelDTO> toHotelDTOList(Set<Hotel> hotels) {
        return hotels.stream().map(HotelDTO::new).collect(Collectors.toSet());
    }

    @JsonIgnore
    public Hotel getAsEntity() {
        Set<Room> roomEntities;
        if (this.rooms != null) {
            roomEntities = this.rooms.stream()
                    .map(RoomDTO::getAsEntity) // convert each RoomDTO to Room
                    .collect(Collectors.toSet());
        } else {
            roomEntities = new HashSet<>();
        }

        return Hotel.builder()
                .hotelName(hotelName)
                .hotelAddress(hotelAddress)
                .rooms(roomEntities)
                .build();
    }
}