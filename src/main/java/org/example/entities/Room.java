package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
    @Column(name = "room_number")
    private Integer roomNumber;
    private Double price;

    @Builder
    public Room(Hotel hotel, Integer roomNumber, Double price) {
        this.hotel = hotel;
        this.roomNumber = roomNumber;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Room { " +
                "Hotel Id = " + (hotel != null ? hotel.getHotelId() : "N/A") +
                ", Room Id = " + roomId +
                ", Room number = " + roomNumber +
                ", Price = " + price +
                " }";
    }


}
