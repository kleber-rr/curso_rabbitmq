package com.example.mscartoes.infra.repository;

import com.example.mscartoes.domain.CartaoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartaoClienteRepository extends JpaRepository<CartaoCliente, Long> {
    List<CartaoCliente> findByCpf(String cpf);
}
