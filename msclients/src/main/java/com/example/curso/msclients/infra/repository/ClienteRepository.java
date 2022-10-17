package com.example.curso.msclients.infra.repository;

import com.example.curso.msclients.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {

    Optional<Cliente> findByCpf(String cpf);
}
