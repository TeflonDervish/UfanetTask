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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
@Service
@AllArgsConstructor
public class TimetableService {

    /**
     * Репозиторий для работы с таблицей расписания.
     */
    private final TimetableRepository timetableRepository;

    /**
     * Репозиторий для работы с клиентами.
     */
    private final ClientRepository clientRepository;

    /**
     * Форматтер для парсинга строки с датой и временем.
     * Формат: "yyyy-MM-dd HH:mm".
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Получение всех записей на определенную дату.
     *
     * @param date Строка с датой в формате "yyyy-MM-dd HH:mm".
     * @return Список объектов {@link TimetableReservationDTO}, содержащий время и количество записей.
     */
    public List<TimetableReservationDTO> getAll(String date) {
        // Преобразуем строку в объект LocalDateTime
        LocalDateTime reservationDate = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        System.out.println(reservationDate);

        // Получаем список всех записей на указанную дату и преобразуем в DTO
        return timetableRepository.findByDate(reservationDate)
                .stream()
                .map((timetable) -> new TimetableReservationDTO(timetable.getTime(), timetable.getCount()))
                .collect(Collectors.toList());
    }

    /**
     * Получение доступных записей на определенную дату.
     *
     * @param date Строка с датой в формате "yyyy-MM-dd HH:mm".
     * @return Список объектов {@link TimetableReservationDTO}, содержащий время и количество доступных мест.
     */
    public List<TimetableReservationDTO> getAvailable(String date) {
        // Преобразуем строку в объект LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);

        // Получаем список всех записей для заданной даты
        List<TimeCount> list = timetableRepository.findByDate(dateTime);

        // Создаем Map для хранения времени и количества занятых мест
        Map<String, Long> countOccupiedMap = new HashMap<>();

        // Заполняем карту с количеством занятых мест по времени
        for (TimeCount t : list) {
            countOccupiedMap.put(t.getTime().substring(0, 5), t.getCount());
        }

        // Формируем список для результата
        List<TimetableReservationDTO> result = new ArrayList<>();

        // Перебираем все возможные временные промежутки (от 06:00 до 23:00)
        for (LocalTime i = LocalTime.of(6, 0); i.isBefore(LocalTime.of(23, 0)); i = i.plusHours(1)) {
            // Добавляем DTO с временем и количеством доступных мест
            result.add(new TimetableReservationDTO(
                    i.toString(),
                    (10 - countOccupiedMap.getOrDefault(i.toString(), 0L))
            ));
        }

        return result;
    }

    /**
     * Резервирование записи для клиента на определенное время.
     *
     * @param id   Идентификатор клиента.
     * @param date Строка с датой и временем в формате "yyyy-MM-dd HH:mm".
     * @return Идентификатор записи.
     * @throws IncorrectRecordingTime Если время записи выходит за рамки рабочего времени.
     * @throws ClientNotFoundException Если клиент не найден.
     * @throws ClientRecordLimit Если на выбранное время уже нет доступных мест.
     */
    public String reserve(Long id, String date) {
        // Преобразуем строку в объект LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);

        // Находим клиента по id
        Optional<Client> clientOptional = clientRepository.findById(id);

        // Проверяем, сколько уже сделано записей на выбранное время
        Long count = timetableRepository.countByDate(localDateTime);

        // Если свободные места есть, то создаем запись
        if (count < 10) {
            if (clientOptional.isPresent()) {
                log.info("Бронирование " + clientOptional.get() + " на время " + localDateTime);
                Timetable timetable = new Timetable(clientOptional.get(), localDateTime);

                // Проверяем, что время записи находится в рабочие часы (с 6:00 до 23:00)
                if (localDateTime.getHour() >= 6 && localDateTime.getHour() < 23) {
                    return timetableRepository.save(timetable).getOrderId().toString();
                } else {
                    throw new IncorrectRecordingTime("Нельзя записаться в не рабочее время");
                }
            } else {
                throw new ClientNotFoundException("Нельзя внести запись с несуществующим клиентом");
            }
        } else {
            throw new ClientRecordLimit("На " + localDateTime + " закончились места");
        }
    }

    /**
     * Отмена записи клиента по идентификатору клиента и идентификатору записи.
     *
     * @param id      Идентификатор клиента.
     * @param orderId Идентификатор записи.
     * @throws TimetableNotFound Если запись с таким orderId не найдена.
     */
    public void cancel(Long id, Long orderId) {
        // Ищем клиента по id
        Optional<Client> clientOptional = clientRepository.findById(id);

        // Ищем запись по orderId
        Optional<Timetable> reservationOptional = timetableRepository.findById(orderId);

        // Если оба объекта найдены, удаляем запись
        if (clientOptional.isPresent() && reservationOptional.isPresent()) {
            log.info("Отмена записи " + clientOptional.get() + " на время " + reservationOptional.get().getDateTime());
            timetableRepository.delete(reservationOptional.get());
        } else {
            throw new TimetableNotFound("Записи с такими данными не существует");
        }
    }
}
