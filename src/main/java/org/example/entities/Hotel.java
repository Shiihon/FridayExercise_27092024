package org.example.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.dtos.HotelDTO;
import org.example.dtos.RoomDTO;

import java.util.HashSet;
import java.util.Set;

@Data
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
    @OneToMany(mappedBy = "hotel", orphanRemoval = true)
    private Set<Room> rooms;

    @Builder
    public Hotel(Long hotelId, String hotelName, String hotelAddress, Set<Room> rooms) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.hotelAddress = hotelAddress;

       if(rooms != null) {
           this.rooms = new HashSet<>();
           for(Room room : rooms) {
               this.rooms.add(room);
           }
       } else {
           this.rooms = new HashSet<>();
       }
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
