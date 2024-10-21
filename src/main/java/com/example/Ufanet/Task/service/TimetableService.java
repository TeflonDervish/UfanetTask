package com.example.Ufanet.Task.service;


import com.example.Ufanet.Task.model.Client;
import com.example.Ufanet.Task.model.Timetable;
import com.example.Ufanet.Task.model.dto.TimeCount;
import com.example.Ufanet.Task.model.dto.TimetableReservationDTO;
import com.example.Ufanet.Task.model.exception.ClientNotFoundException;
import com.example.Ufanet.Task.model.exception.ClientRecordLimit;
import com.example.Ufanet.Task.model.exception.IncorrectRecordingTime;
import com.example.Ufanet.Task.model.exception.TimetableNotFound;
import com.example.Ufanet.Task.repository.ClientRepository;
import com.example.Ufanet.Task.repository.TimetableRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Service
@AllArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final ClientRepository clientRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public List<TimetableReservationDTO> getAll(String date) {
        LocalDateTime reservationDate = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        timetableRepository.findByDate(reservationDate)
                .stream()
                .map((timetable) -> new TimetableReservationDTO(timetable.getTime(), timetable.getCount()))
                .collect(Collectors.toList());

        return timetableRepository.findByDate(reservationDate)
                .stream()
                .map((timetable) -> new TimetableReservationDTO(timetable.getTime(), timetable.getCount()))
                .collect(Collectors.toList()) ;
    }

    public List<TimetableReservationDTO> getAvailable(String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        List<TimeCount> list = timetableRepository.findByDate(dateTime);
        Map<String, Integer> countOccupiedMap = new HashMap<>();

        for (TimeCount t: list) {
            countOccupiedMap.put(t.getTime().substring(0, 5), countOccupiedMap.getOrDefault(t.getTime(), 0) + 1);
        }

        List<TimetableReservationDTO> result = new ArrayList<>();

        for (LocalTime i = LocalTime.of(6, 00);
                i.isBefore(LocalTime.of( 23, 00));
                i = i.plusHours(1)){
            result.add(new TimetableReservationDTO(
                    i.toString(),
                    (long) (10 - countOccupiedMap.getOrDefault(i.toString(), 0))
            ));
        }

        return result;

    }

    public String reserve(Long id, String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        Optional<Client> clientOptional = clientRepository.findById(id);
        Long count = timetableRepository.countByDate(localDateTime);
        if (count < 10) {
                if (clientOptional.isPresent()) {
                    Timetable timetable = new Timetable(clientOptional.get(), localDateTime);
                    if (localDateTime.getHour() >= 6 && localDateTime.getHour() <= 23) {
                        return timetableRepository.save(timetable).getOrderId().toString();
                    } else {
                        throw new IncorrectRecordingTime("Нельзя записаться в не рабочее время");
                    }
                } else {
                    throw new ClientNotFoundException("Нельзя внести запись с несуществующим клиентом");
                }
        }else {
            throw new ClientRecordLimit("На " + localDateTime.toString() + " закончились места");
        }
    }

    public void cancel(Long id, Long orderId) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        Optional<Timetable> reservationOptional = timetableRepository.findById(id);

        System.out.println("Все ок");

        if (clientOptional.isPresent() && reservationOptional.isPresent()) {
            timetableRepository.delete(reservationOptional.get());
        } else {
            throw new TimetableNotFound("Записи с такими данными не существует");
        }

    }
}