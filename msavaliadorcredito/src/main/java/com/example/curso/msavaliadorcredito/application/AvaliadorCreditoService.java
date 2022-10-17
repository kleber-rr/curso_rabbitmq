package com.example.curso.msavaliadorcredito.application;

import com.example.curso.msavaliadorcredito.application.exception.DadosClienteNotFoundException;
import com.example.curso.msavaliadorcredito.application.exception.ErroComunicacaoMicroservicesException;
import com.example.curso.msavaliadorcredito.application.exception.ErroSolicitacaoCartaoException;
import com.example.curso.msavaliadorcredito.domain.*;
import com.example.curso.msavaliadorcredito.feignclients.CartoesResourceFeignClient;
import com.example.curso.msavaliadorcredito.feignclients.ClienteResourceFeignClient;
import com.example.curso.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceFeignClient clienteResourceFeignClient;
    private final CartoesResourceFeignClient cartoesResourceFeignClient;

    private final SolicitacaoEmissaoCartaoPublisher publisher;

    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {

        try {
            ResponseEntity<DadosCliente> dadosDoClienteResponse = clienteResourceFeignClient.dadosDoCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesResourceFeignClient.getCartoesByCliente(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosDoClienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();
        } catch (FeignException.FeignClientException e){
            int status = e.status();
            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            } else {
                throw new ErroComunicacaoMicroservicesException(e.getMessage(),status);
            }
        } catch (Exception e){
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), HttpStatus.NOT_FOUND.value());
        }

    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {
        try{

            ResponseEntity<DadosCliente> dadosClienteResponse = clienteResourceFeignClient.dadosDoCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesRendaAte = cartoesResourceFeignClient.getCartoesRendaAte(renda);
            List<Cartao> cartoes = cartoesRendaAte.getBody();

            var listaCartoesAprovados = cartoes.stream().map(cartao -> {

                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosClienteResponse.getBody().getIdade());
                BigDecimal fator = idadeBD.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);


                CartaoAprovado cartaoAprovado = new CartaoAprovado();
                cartaoAprovado.setBandeira(cartao.getBandeira());
                cartaoAprovado.setCartao(cartao.getNome());
                cartaoAprovado.setLimiteAprovado(limiteAprovado);
                return cartaoAprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listaCartoesAprovados);
        } catch (FeignException.FeignClientException e){
            int status = e.status();
            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            } else {
                throw new ErroComunicacaoMicroservicesException(e.getMessage(),status);
            }
        } catch (Exception e){
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try {
            publisher.solicitarCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        } catch (Exception e){
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }
}
