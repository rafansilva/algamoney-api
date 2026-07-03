package com.algaworks.algamoneyapi.domain.dto;

import com.algaworks.algamoneyapi.domain.model.Categoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoEstatisticaCategoriaDTO {

    private Categoria categoria;

    private BigDecimal total;
}
