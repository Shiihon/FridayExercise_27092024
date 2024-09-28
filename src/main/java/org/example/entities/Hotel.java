package org.example.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.dtos.HotelDTO;
import org.example.dtos.RoomDTO;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Long hotelId;
    @Column(name = "hotel_name")
    private String hotelName;
    @Column(name = "hotel_address")
    private String hotelAddress;
    @EqualsAndHashCode.Exclude // spørg thomas, stack overflow uden exclude.
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Room> rooms = new HashSet<>();

    // Constructor for creating Hotel from HotelDTO
    public Hotel(HotelDTO hotelDTO) {
        this.hotelName = hotelDTO.getHotelName();
        this.hotelAddress = hotelDTO.getHotelAddress();
        for (RoomDTO roomDTO : hotelDTO.getRooms()) {
            addRoom(new Room(roomDTO, this));
        }
    }

    public void addRoom(Room room) {
        rooms.add(room);
        room.setHotel(this); // Set back-reference
    }

    @Override
    public String toString() {
        return "Hotel { " +
                "Hotel Id = " + hotelId +
                ", Hotel name = '" + hotelName + '\'' +
                // Instead of rooms, show the count or specific attributes
                ", Number of rooms = " + (rooms != null ? rooms.size() : 0) +
                '}';
    }
}
