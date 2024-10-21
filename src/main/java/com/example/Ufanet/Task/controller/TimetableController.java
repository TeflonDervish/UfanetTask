package com.example.Ufanet.Task.controller;


import com.example.Ufanet.Task.model.dto.TimetableReservationDTO;
import com.example.Ufanet.Task.model.exception.ClientNotFoundException;
import com.example.Ufanet.Task.model.exception.ClientRecordLimit;
import com.example.Ufanet.Task.model.exception.IncorrectRecordingTime;
import com.example.Ufanet.Task.model.exception.TimetableNotFound;
import com.example.Ufanet.Task.service.TimetableService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v0/pool/timetable")
@AllArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @GetMapping("/all")
    public ResponseEntity<List<TimetableReservationDTO>> getAll(@RequestParam String date){
        log.info("Получение информации всей информации о клиентах");
        return new ResponseEntity<>(
                timetableService.getAll(date + " 00:00")
                , HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<TimetableReservationDTO>> getAvailable (@RequestParam String date){
        log.info("Получение сведений о свободных местах");
        return new ResponseEntity<>(
                timetableService.getAvailable(date + " 00:00"),
                HttpStatus.OK
        );
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@RequestBody Map<String, Object> body){
        log.info("Бронирование времени");
        try {
            return new ResponseEntity<>(
                    timetableService.reserve(Long.valueOf(body.get("clientId").toString()), (String) body.get("dateTime")),
                    HttpStatus.CREATED);
        }catch (IncorrectRecordingTime e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (ClientRecordLimit e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancel(@RequestBody Map<String, Object> body) {
        log.info("Отмена записи");
        try {
            timetableService.cancel(Long.valueOf((String) body.get("clientId")), Long.valueOf((String) body.get("orderId")));
            return ResponseEntity.ok().build();
        }catch (TimetableNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
