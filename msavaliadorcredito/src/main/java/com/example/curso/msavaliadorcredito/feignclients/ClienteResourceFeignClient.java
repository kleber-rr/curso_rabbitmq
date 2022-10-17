package com.example.curso.msavaliadorcredito.feignclients;

import com.example.curso.msavaliadorcredito.domain.DadosCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "msclients", path = "/clientes")
public interface ClienteResourceFeignClient {
    @GetMapping(params = "cpf")
    ResponseEntity<DadosCliente> dadosDoCliente(@RequestParam("cpf") String cpf);
}
