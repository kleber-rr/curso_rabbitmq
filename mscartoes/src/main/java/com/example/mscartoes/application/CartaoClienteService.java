package com.example.mscartoes.application;

import com.example.mscartoes.domain.CartaoCliente;
import com.example.mscartoes.infra.repository.CartaoClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartaoClienteService {

    private final CartaoClienteRepository repository;

    public List<CartaoCliente> listCartoesByCpf(String cpf){
        return repository.findByCpf(cpf);
    }
}
