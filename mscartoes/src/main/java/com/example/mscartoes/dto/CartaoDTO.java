package com.example.mscartoes.dto;

import com.example.mscartoes.domain.BandeiraCartao;
import com.example.mscartoes.domain.Cartao;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartaoDTO {

    private String nome;
    private BandeiraCartao bandeira;
    private BigDecimal renda;
    private BigDecimal limiteBasico;

    public Cartao toModel(){
        return new Cartao(nome,bandeira,renda,limiteBasico);
    }

}
