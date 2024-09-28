package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
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

    @Builder
    public Hotel(String hotelName, String hotelAddress, Set<Room> rooms) {
        this.hotelName = hotelName;
        this.hotelAddress = hotelAddress;

        if (rooms != null) {
            this.rooms = rooms;
            for (Room room : rooms) {
                room.setHotel(this);
            }

        } else {
            this.rooms = new HashSet<>(); // to prevent nullpointerexcep.
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


    //maybe not needed?
//    public void addRoom(Room room) {
//        if (this.rooms == null) {
//            rooms = new HashSet<>();
//        }
//        rooms.add(room);
//    }
}
