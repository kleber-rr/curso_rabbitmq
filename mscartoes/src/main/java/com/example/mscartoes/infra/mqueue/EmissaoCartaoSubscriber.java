package com.example.mscartoes.infra.mqueue;

import com.example.mscartoes.domain.Cartao;
import com.example.mscartoes.domain.CartaoCliente;
import com.example.mscartoes.domain.DadosSolicitacaoEmissaoCartao;
import com.example.mscartoes.infra.repository.CartaoClienteRepository;
import com.example.mscartoes.infra.repository.CartaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmissaoCartaoSubscriber {

    private final CartaoRepository repository;
    private final CartaoClienteRepository cartaoClienteRepository;

    @RabbitListener(queues = "${mq.queues.emissao-cartoes}")
    public void receberSolicitacaoEmissao(@Payload String payload){
        var mapper = new ObjectMapper();
        try {
            DadosSolicitacaoEmissaoCartao dadosSolicitacaoEmissaoCartao = mapper.readValue(payload, DadosSolicitacaoEmissaoCartao.class);
            Cartao cartao = repository.findById(dadosSolicitacaoEmissaoCartao.getIdCartao()).orElseThrow();
            CartaoCliente cartaoCliente = new CartaoCliente();
            cartaoCliente.setCartao(cartao);
            cartaoCliente.setCpf(dadosSolicitacaoEmissaoCartao.getCpf());
            cartaoCliente.setLimite(dadosSolicitacaoEmissaoCartao.getLimiteLiberado());

            cartaoClienteRepository.save(cartaoCliente);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
