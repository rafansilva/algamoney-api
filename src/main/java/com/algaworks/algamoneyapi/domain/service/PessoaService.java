package com.algaworks.algamoneyapi.domain.service;

import com.algaworks.algamoneyapi.domain.model.Contato;
import com.algaworks.algamoneyapi.domain.model.Pessoa;
import com.algaworks.algamoneyapi.domain.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public Pessoa salvar(Pessoa pessoa) {
        pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));

        return pessoaRepository.save(pessoa);
    }

    public Pessoa atualizarPessoa(Long codigo, Pessoa pessoa) {
        Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);

        pessoaSalva.getContatos().clear();
        pessoaSalva.getContatos().addAll(pessoa.getContatos());
        pessoaSalva.getContatos().forEach(c -> c.setPessoa(pessoaSalva));

        BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo", "contatos" );

        return this.pessoaRepository.save(pessoaSalva);
    }

    public Pessoa atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
        Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
        pessoaSalva.setAtivo(ativo);
        return pessoaRepository.save(pessoaSalva);
    }

    private Pessoa buscarPessoaPeloCodigo(Long codigo) {
        return pessoaRepository.findById(codigo)
                .orElseThrow(() -> new EmptyResultDataAccessException(1));
    }
}
