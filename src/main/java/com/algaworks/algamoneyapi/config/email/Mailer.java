package com.algaworks.algamoneyapi.config.email;

import com.algaworks.algamoneyapi.domain.model.Lancamento;
import com.algaworks.algamoneyapi.domain.model.Usuario;
import com.algaworks.algamoneyapi.domain.repository.LancamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Mailer {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine thymeleaf;

//    @Autowired
//    private LancamentoRepository repo;
//
//    @EventListener
//    private void teste(ApplicationReadyEvent event) {
//        String template = "mail/aviso-lancamentos-vencidos";
//
//        List<Lancamento> lista = repo.findAll();
//
//        Map<String, Object> variaveis = new HashMap<>();
//        variaveis.put("lancamentos", lista);
//
//        this.enviarEmail(
//                "testes.algaworks@gmail.com",
//                Arrays.asList("testes.algaworks@outlook.com"),
//                "Testando Envio de email",
//                template,
//                variaveis);
//
//        System.out.println("Terminado o envio de e-mail...");
//    }

    public void avisarSobreLancamentosVencidos(List<Lancamento> vencidos, List<Usuario> destinatarios) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("lancamentos", vencidos);

        List<String> emails = destinatarios.stream()
                .map(Usuario::getEmail)
                .toList();

        enviarEmail("testes.algaworks@gmail.com",
                emails,
                "Lançamentos vencidos",
                "mail/aviso-lancamentos-vencidos",
                variaveis);
    }

    public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String template,
                            Map<String, Object> variaveis) {
        Context context = new Context(new Locale("pt", "BR"));

        variaveis.entrySet().forEach(
                e -> context.setVariable(e.getKey(), e.getValue()));

        String mensagem = thymeleaf.process(template, context);

        this.enviarEmail(remetente, destinatarios, assunto, mensagem);
    }

    public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String mensagem) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
            helper.setSubject(assunto);
            helper.setText(mensagem, true);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail", e);
        }
    }
}
