package com.dduongdev.hotel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_type")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomType extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "price_per_night", nullable = false)
    private double pricePerNight;

    private boolean hidden = false;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

}
