package com.algaworks.algamoneyapi.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lancamento")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @NotNull
    private String descricao;

    @NonNull
    @Column(name = "data_vencimento")
    //@JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
   // @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataPagamento;

    @NotNull
    private BigDecimal valor;

    private String observacao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoLancamento tipoLancamento;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_categoria")
    private Categoria categoria;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_pessoa")
    private Pessoa pessoa;

}
