package com.example.Ufanet.Task.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Timetable(Client client, LocalDateTime localDateTime) {
        this.client = client;
        this.dateTime = localDateTime;
    }

}
