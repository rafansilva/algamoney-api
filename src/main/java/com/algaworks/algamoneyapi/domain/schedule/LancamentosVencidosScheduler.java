package com.algaworks.algamoneyapi.domain.schedule;

import com.algaworks.algamoneyapi.config.email.Mailer;
import com.algaworks.algamoneyapi.domain.model.Lancamento;
import com.algaworks.algamoneyapi.domain.model.Usuario;
import com.algaworks.algamoneyapi.domain.repository.LancamentoRepository;
import com.algaworks.algamoneyapi.domain.repository.UsuarioRepository;
import com.algaworks.algamoneyapi.domain.service.LancamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LancamentosVencidosScheduler {

    private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";

    private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private Mailer mailer;

    @Scheduled(cron = "0 0 6 * * *")
    public void avisarSobreLancamentosVencidos() {
        if (logger.isDebugEnabled()){
            logger.debug("Preparando envio de e-mails de aviso de lançamentos vencidos.");
        }

        List<Lancamento> vencidos = lancamentoRepository
                .findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());

        if (vencidos.isEmpty()) {
            logger.info("Sem lançamentos vencidos para aviso.");
            return;
        }

        logger.info("Existem {} lançamentos vencidos.", vencidos.size());

        List<Usuario> destinatarios = usuarioRepository
                .findByPermissoesDescricao(DESTINATARIOS);

        if (destinatarios.isEmpty()) {
            logger.warn("Existem lançamentos vencidos, mas o sistema não encontrou destinatários.");
            return;
        }

        mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);

        logger.info("Envio de e-mail de aviso concluído.");
    }
}
