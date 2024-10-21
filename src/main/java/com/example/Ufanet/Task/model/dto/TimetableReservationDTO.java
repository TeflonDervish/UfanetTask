package com.example.Ufanet.Task.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class TimetableReservationDTO {

    private String time;
    private Long count;

}
