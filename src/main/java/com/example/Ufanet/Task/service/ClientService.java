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

    private final ClientRepository clientRepository;

    public Client getClientById(Long id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isPresent()) {
            return clientOptional.get();
        }else {
            throw new ClientNotFoundException("Нет клиента с таким id");
        }
    }

    public Client addClient(Client client) {
        return clientRepository.save(client);
    }

    public Client updateClient(Client client) {
        Client clientNew = getClientById(client.getId());

        clientNew.setName(client.getName());
        clientNew.setEmail(client.getEmail());
        clientNew.setPhone(client.getPhone());
        clientRepository.save(clientNew);
        return clientNew;
    }

    public List<ClientSummary> getAll() {
        return clientRepository.findAllWithSummary();
    }
}
