package com.algaworks.algamoneyapi.domain.repository.query;

import com.algaworks.algamoneyapi.domain.dto.LancamentoEstatisticaCategoriaDTO;
import com.algaworks.algamoneyapi.domain.dto.LancamentoEstatisticaDiaDTO;
import com.algaworks.algamoneyapi.domain.dto.LancamentoEstatisticaPessoaDTO;
import com.algaworks.algamoneyapi.domain.model.Lancamento;
import com.algaworks.algamoneyapi.domain.repository.filter.LancamentoFilter;
import com.algaworks.algamoneyapi.domain.repository.projection.ResumoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepositoryQuery {

    Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);

    Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);

    List<LancamentoEstatisticaCategoriaDTO> porCategoria(LocalDate mesReferencia);

    List<LancamentoEstatisticaDiaDTO> porDia(LocalDate mesReferencia);

    List<LancamentoEstatisticaPessoaDTO> porPessoa(LocalDate inicio, LocalDate fim);

}
