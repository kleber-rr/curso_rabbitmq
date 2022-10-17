package com.example.curso.msclients.application.representation;

import com.example.curso.msclients.domain.Cliente;
import lombok.Data;

@Data
public class ClienteDTO {
    private String cpf;
    private String nome;
    private Integer idade;

    public Cliente toModel(){
        return new Cliente(cpf,nome,idade);
    }
}
