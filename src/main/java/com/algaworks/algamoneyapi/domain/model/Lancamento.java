package com.algaworks.algamoneyapi.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "lancamento")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lancamento {

    @EqualsAndHashCode.Include
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

    @JsonIgnoreProperties("contatos")
    @NotNull
    @ManyToOne
    @JoinColumn(name = "codigo_pessoa")
    private Pessoa pessoa;


    @JsonIgnore
    public boolean isReceita() {
        return TipoLancamento.RECEITA.equals(tipoLancamento);
    }
}
