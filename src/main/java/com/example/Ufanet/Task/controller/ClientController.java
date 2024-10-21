package com.example.Ufanet.Task.controller;


import com.example.Ufanet.Task.model.Client;
import com.example.Ufanet.Task.model.dto.ClientDTO;
import com.example.Ufanet.Task.model.dto.ClientSummary;
import com.example.Ufanet.Task.model.exception.ClientNotFoundException;
import com.example.Ufanet.Task.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v0/pool/client")
@AllArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/all")
    public ResponseEntity<List<ClientSummary>> getClients(){
        return new ResponseEntity<>(clientService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<Client> getClient(@RequestParam(name = "id") Long id){
        Optional<Client> clientOptional = clientService.getClientById(id);

        return clientOptional
                .map(client -> new ResponseEntity<>(client, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addClient(@RequestBody Client client) {
        clientService.addClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateClient(@RequestBody Client client) {
        try {
            clientService.updateClient(client);
            return ResponseEntity.ok().build();
        }catch (ClientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }



}
