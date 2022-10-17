package com.example.mscartoes.application;

import com.example.mscartoes.domain.Cartao;
import com.example.mscartoes.domain.CartaoCliente;
import com.example.mscartoes.dto.CartaoClienteDTO;
import com.example.mscartoes.dto.CartaoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cartoes")
@RequiredArgsConstructor
public class CartoesController {

    private final CartaoService cartaoService;
    private final CartaoClienteService cartaoClienteService;

    @GetMapping("status")
    public String status() {
        return "ok";
    }

    @PostMapping
    public ResponseEntity save(@RequestBody CartaoDTO request) {
        var cartao = request.toModel();
        cartaoService.save(cartao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam("renda") Long renda){
        List<Cartao> cartoes = cartaoService.getCartoesRendaMenorIgual(renda);
        return ResponseEntity.ok(cartoes);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<List<CartaoClienteDTO>> getCartoesByCliente(@RequestParam("cpf") String cpf){
        List<CartaoCliente> listCartaoCliente = cartaoClienteService.listCartoesByCpf(cpf);
        List<CartaoClienteDTO> resultList = listCartaoCliente.stream()
                .map(CartaoClienteDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultList);
    }

}
