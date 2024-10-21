package com.example.Ufanet.Task.service;


import com.example.Ufanet.Task.model.Client;
import com.example.Ufanet.Task.model.dto.ClientSummary;
import com.example.Ufanet.Task.model.exception.ClientNotFoundException;
import com.example.Ufanet.Task.repository.ClientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ClientService {

    /**
     * Репозиторий для работы с клиентами.
     */
    private final ClientRepository clientRepository;

    /**
     * Получение клиента по его идентификатору.
     *
     * @param id Идентификатор клиента.
     * @return Объект {@link Client} с соответствующим id.
     * @throws ClientNotFoundException Если клиент с указанным id не найден.
     */
    public Client getClientById(Long id) {
        // Ищем клиента по id
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isPresent()) {
            // Если клиент найден, возвращаем его
            return clientOptional.get();
        } else {
            // Если клиент не найден, выбрасываем исключение
            throw new ClientNotFoundException("Нет клиента с таким id");
        }
    }

    /**
     * Добавление нового клиента в базу данных.
     *
     * @param client Объект {@link Client}, который нужно сохранить.
     * @return Сохраненный объект {@link Client}.
     */
    public Client addClient(Client client) {
        // Сохраняем клиента в базе данных
        return clientRepository.save(client);
    }

    /**
     * Обновление информации о клиенте.
     *
     * @param client Объект {@link Client}, содержащий новые данные.
     * @return Обновленный объект {@link Client}.
     * @throws ClientNotFoundException Если клиент с указанным id не найден.
     */
    public Client updateClient(Client client) {
        // Получаем клиента по его id
        Client clientNew = getClientById(client.getId());

        // Обновляем данные клиента
        clientNew.setName(client.getName());
        clientNew.setEmail(client.getEmail());
        clientNew.setPhone(client.getPhone());

        // Сохраняем обновленные данные в базе данных
        clientRepository.save(clientNew);
        return clientNew;
    }

    /**
     * Получение списка всех клиентов с краткой информацией о них.
     *
     * @return Список объектов {@link ClientSummary}, содержащих краткую информацию о клиентах.
     */
    public List<ClientSummary> getAll() {
        // Получаем всех клиентов с краткой информацией (через кастомный метод репозитория)
        return clientRepository.findAllWithSummary();
    }
}
