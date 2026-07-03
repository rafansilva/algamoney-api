package com.algaworks.algamoneyapi.api.controller;

import com.algaworks.algamoneyapi.api.event.RecursoCriadoEvent;
import com.algaworks.algamoneyapi.api.exceptionhandler.AlgamoneyExceptionHandler;
import com.algaworks.algamoneyapi.domain.dto.LancamentoEstatisticaCategoriaDTO;
import com.algaworks.algamoneyapi.domain.dto.LancamentoEstatisticaDiaDTO;
import com.algaworks.algamoneyapi.domain.model.Lancamento;
import com.algaworks.algamoneyapi.domain.repository.LancamentoRepository;
import com.algaworks.algamoneyapi.domain.repository.filter.LancamentoFilter;
import com.algaworks.algamoneyapi.domain.repository.projection.ResumoLancamento;
import com.algaworks.algamoneyapi.domain.service.LancamentoService;
import com.algaworks.algamoneyapi.domain.service.exception.PessoaInexistenteOuInativaException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

    @Autowired
    MessageSource messageSource;
    @Autowired
    private LancamentoRepository lancamentoRepository;
    @Autowired
    private LancamentoService lancamentoService;
    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public Page<Lancamento> pesquisarLancamento(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return lancamentoRepository.filtrar(lancamentoFilter, pageable);
    }

    @GetMapping(params = "resumo")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public Page<ResumoLancamento> resumirLancamento(LancamentoFilter lancamentoFilter, Pageable pageable) {
        return lancamentoRepository.resumir(lancamentoFilter, pageable);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public ResponseEntity<Lancamento> buscarLancamentoPeloCodigo(@PathVariable Long codigo) {
        Optional<Lancamento> lancamento = lancamentoRepository.findById(codigo);
        return lancamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("estatistica/por-categoria")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public List<LancamentoEstatisticaCategoriaDTO> porCategoria() {
        return lancamentoRepository.porCategoria(LocalDate.now());
    }

    @GetMapping("estatistica/por-dia")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public List<LancamentoEstatisticaDiaDTO> porDia() {
        return lancamentoRepository.porDia(LocalDate.now());
    }

    @GetMapping("relatorio/por-pessoa")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
    public ResponseEntity<byte[]> relatorioPorPessoa(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim) throws Exception {

        byte[] relatorio = lancamentoService.relatorioPorPessoa(inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .body(relatorio);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and hasAuthority('SCOPE_write')")
    public ResponseEntity<Lancamento> criarLancamento(@Valid @RequestBody Lancamento lancamento,
                                                      HttpServletResponse response) {
        Lancamento lancamentoSalvo = lancamentoService.salvarLancamento(lancamento);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
    }

    @PutMapping("/atualizar/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and hasAuthority('SCOPE_write')")
    public ResponseEntity<Lancamento> atualizarLancamento(@PathVariable Long codigo,
                                                          @RequestBody @Valid Lancamento lancamento,
                                                          HttpServletResponse response) {
        Lancamento lancamentoAtualizado = lancamentoService.atualizarLancamento(codigo, lancamento);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoAtualizado.getCodigo()));
        return ResponseEntity.status(HttpStatus.OK).body(lancamentoAtualizado);
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and hasAuthority('SCOPE_write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarLancamento(@PathVariable Long codigo) {
        lancamentoRepository.deleteById(codigo);
    }

    @PostMapping("/anexo")
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and hasAuthority('SCOPE_write')")
    public String uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {
        OutputStream out = new FileOutputStream(
                "C:\\Users\\Win\\Desktop\\anexo--" + anexo.getOriginalFilename());
        out.write(anexo.getBytes());
        out.close();
        return "ok";
    }

    @ExceptionHandler({PessoaInexistenteOuInativaException.class})
    public ResponseEntity<Object> handleDataIntegrityViolationException(PessoaInexistenteOuInativaException ex) {
        String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
        List<AlgamoneyExceptionHandler.Erro> erros = List.of(new AlgamoneyExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }
}
