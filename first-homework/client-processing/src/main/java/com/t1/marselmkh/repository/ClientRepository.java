package com.t1.marselmkh.repository;

import com.t1.marselmkh.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByClientId(String clientId);

    Optional<Client> findByClientId(String clientId);
}
