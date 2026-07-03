package com.algaworks.algamoneyapi.domain.dto;

import com.algaworks.algamoneyapi.domain.model.Pessoa;
import com.algaworks.algamoneyapi.domain.model.TipoLancamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoEstatisticaPessoaDTO {

    private TipoLancamento tipo;

    private Pessoa pessoa;

    private BigDecimal total;



}
