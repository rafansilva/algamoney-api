package com.algaworks.algamoneyapi.domain.dto;

import com.algaworks.algamoneyapi.domain.model.TipoLancamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoEstatisticaDiaDTO {

    private TipoLancamento tipoLancamento;

    private LocalDate dia;

    private BigDecimal total;


}
