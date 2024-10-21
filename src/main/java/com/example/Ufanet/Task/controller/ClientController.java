package com.example.Ufanet.Task.controller;


import com.example.Ufanet.Task.model.Client;
import com.example.Ufanet.Task.model.dto.ClientSummary;
import com.example.Ufanet.Task.model.exception.ClientNotFoundException;
import com.example.Ufanet.Task.service.ClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v0/pool/client")
@AllArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/all")
    public ResponseEntity<List<ClientSummary>> getClients(){
        log.info("Получение информации всей информации о клиентах");
        return new ResponseEntity<>(clientService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<Client> getClient(@RequestParam(name = "id") Long id){
        try {
            Client client = clientService.getClientById(id);
            log.info("Информация о клиенте " + client.toString());
            return new ResponseEntity<>(client, HttpStatus.OK);
        }catch (ClientNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addClient(@RequestBody Client client) {
        clientService.addClient(client);
        log.info("Добавлен клиент" + client.toString());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateClient(@RequestBody Client client) {
        try {
            clientService.updateClient(client);
            log.info("Обновлена информация о клиенте " + client);
            return ResponseEntity.ok().build();
        }catch (ClientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }



}
