package com.algaworks.algamoneyapi.service;

import com.algaworks.algamoneyapi.domain.model.Lancamento;
import com.algaworks.algamoneyapi.domain.model.Pessoa;
import com.algaworks.algamoneyapi.domain.repository.LancamentoRepository;
import com.algaworks.algamoneyapi.domain.repository.PessoaRepository;
import com.algaworks.algamoneyapi.service.exception.PessoaInexistenteOuInativaException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@Service
public class LancamentoService {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    public Lancamento buscarLancamentoPeloCodigo(Long codigo) {
        return lancamentoRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
    }

    public Lancamento salvarLancamento(Lancamento lancamento) {
        validaPessoa(lancamento);
        return lancamentoRepository.save(lancamento);
    }

    public Lancamento atualizarLancamento(Long codigo, Lancamento lancamento) {
        Lancamento lancamentoSalvo = buscarLancamentoPeloCodigo(codigo);
        if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
            validaPessoa(lancamento);
        }

        BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

        return lancamentoRepository.save(lancamentoSalvo);
    }

    private void validaPessoa(Lancamento lancamento) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
        if (pessoa.isEmpty() || pessoa.get().isInativo()) {
            throw new PessoaInexistenteOuInativaException();
        }
    }
}
