package com.example.Ufanet.Task.service;


import com.example.Ufanet.Task.model.Client;
import com.example.Ufanet.Task.model.dto.ClientDTO;
import com.example.Ufanet.Task.model.dto.ClientSummary;
import com.example.Ufanet.Task.model.exception.ClientNotFoundException;
import com.example.Ufanet.Task.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public Client addClient(Client client) {
        return clientRepository.save(client);
    }

    public Optional<Client> updateClient(Client client) {
        Optional<Client> clientNew = getClientById(client.getId());

        if (clientNew.isPresent()) {
            clientNew.get().setName(client.getName());
            clientNew.get().setEmail(client.getEmail());
            clientNew.get().setPhone(client.getPhone());
            clientRepository.save(clientNew.get());
        }else {
            throw new ClientNotFoundException("Нет клиента с таким id");
        }
        return clientNew;
    }

    public List<ClientSummary> getAll() {
        return clientRepository.findAllWithSummary();
    }
}
