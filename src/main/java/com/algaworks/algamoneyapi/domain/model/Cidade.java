package com.algaworks.algamoneyapi.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@EqualsAndHashCode
@Getter
@Setter
@Entity
@Table(name = "cidade")
public class Cidade {

    @Id
    private Long codigo;

    private String nome;

    @ManyToOne
    @JoinColumn(name = "codigo_estado")
    private Estado estado;
}
