package com.example.Ufanet.Task.repository;

import com.example.Ufanet.Task.model.Client;
import com.example.Ufanet.Task.model.dto.ClientDTO;
import com.example.Ufanet.Task.model.dto.ClientSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {


    @Query("""
                select id as id, name as name
                from Client
            """)
    List<ClientSummary> findAllWithSummary();

}
