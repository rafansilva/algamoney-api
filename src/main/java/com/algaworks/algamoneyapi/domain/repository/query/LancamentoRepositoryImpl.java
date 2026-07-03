package com.algaworks.algamoneyapi.domain.repository.query;

import com.algaworks.algamoneyapi.domain.dto.LancamentoEstatisticaCategoriaDTO;
import com.algaworks.algamoneyapi.domain.dto.LancamentoEstatisticaDiaDTO;
import com.algaworks.algamoneyapi.domain.dto.LancamentoEstatisticaPessoaDTO;
import com.algaworks.algamoneyapi.domain.model.Lancamento;
import com.algaworks.algamoneyapi.domain.model.Lancamento_;
import com.algaworks.algamoneyapi.domain.repository.filter.LancamentoFilter;
import com.algaworks.algamoneyapi.domain.repository.projection.ResumoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Lancamento> criteriaQuery = builder.createQuery(Lancamento.class);
        Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

        // cria as restrições
        Predicate[] predicates = criaRestricoes(lancamentoFilter, builder, root);
        criteriaQuery.where(predicates);

        TypedQuery<Lancamento> query = entityManager.createQuery(criteriaQuery);
        adicionarRestricoesDePaginacao(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
    }

    @Override
    public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ResumoLancamento> criteriaQuery = builder.createQuery(ResumoLancamento.class);
        Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

        criteriaQuery.select(builder.construct(ResumoLancamento.class
        , root.get(Lancamento_.codigo)
        , root.get(Lancamento_.descricao)
        , root.get(Lancamento_.dataVencimento)
        , root.get(Lancamento_.dataPagamento)
        , root.get(Lancamento_.valor)
        , root.get(Lancamento_.tipoLancamento)
        , root.get(Lancamento_.categoria).get("nome")
        , root.get(Lancamento_.pessoa).get("nome")));

        Predicate[] predicates = criaRestricoes(lancamentoFilter, builder, root);
        criteriaQuery.where(predicates);

        TypedQuery<ResumoLancamento> query = entityManager.createQuery(criteriaQuery);
        adicionarRestricoesDePaginacao(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
    }


    private Predicate[] criaRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(lancamentoFilter.getDescricao())) {
            predicates.add(builder.like(builder.lower(root.get("descricao")), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
        }

        if (lancamentoFilter.getDataVencimentoDe() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("dataVencimento"), lancamentoFilter.getDataVencimentoDe()));
        }

        if (lancamentoFilter.getDataVencimentoAte() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("dataVencimento"), lancamentoFilter.getDataVencimentoAte()));
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }

    private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
        int paginaAtual = pageable.getPageNumber();
        int totalRegistrosPorPagina = pageable.getPageSize();
        int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;

        query.setFirstResult(primeiroRegistroDaPagina);
        query.setMaxResults(totalRegistrosPorPagina);
    }

    private Long total(LancamentoFilter lancamentoFilter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

        Predicate[] predicates = criaRestricoes(lancamentoFilter, builder, root);
        criteriaQuery.where(predicates);

        criteriaQuery.select(builder.count(root));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public List<LancamentoEstatisticaCategoriaDTO> porCategoria(LocalDate mesReferencia) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<LancamentoEstatisticaCategoriaDTO> criteriaQuery = criteriaBuilder
                .createQuery(LancamentoEstatisticaCategoriaDTO.class);

        Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

        criteriaQuery.select(criteriaBuilder.construct(LancamentoEstatisticaCategoriaDTO.class,
                root.get(Lancamento_.categoria),
                criteriaBuilder.sum(root.get(Lancamento_.valor))));

        LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
        LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());

        criteriaQuery.where(
                criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), primeiroDia),
                criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), ultimoDia));

        criteriaQuery.groupBy(root.get(Lancamento_.categoria));

        TypedQuery<LancamentoEstatisticaCategoriaDTO> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getResultList();
    }

    @Override
    public List<LancamentoEstatisticaDiaDTO> porDia(LocalDate mesReferencia) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<LancamentoEstatisticaDiaDTO> criteriaQuery = criteriaBuilder
                .createQuery(LancamentoEstatisticaDiaDTO.class);

        Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

        criteriaQuery.select(criteriaBuilder.construct(LancamentoEstatisticaDiaDTO.class,
                root.get(Lancamento_.tipoLancamento),
                root.get(Lancamento_.dataVencimento),
                criteriaBuilder.sum(root.get(Lancamento_.valor))));

        LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
        LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());

        criteriaQuery.where(
                criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), primeiroDia),
                criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), ultimoDia));

        criteriaQuery.groupBy(
                root.get(Lancamento_.tipoLancamento),
                root.get(Lancamento_.dataVencimento));

        TypedQuery<LancamentoEstatisticaDiaDTO> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getResultList();
    }

    @Override
    public List<LancamentoEstatisticaPessoaDTO> porPessoa(LocalDate inicio, LocalDate fim) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<LancamentoEstatisticaPessoaDTO> criteriaQuery = criteriaBuilder
                .createQuery(LancamentoEstatisticaPessoaDTO.class);

        Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

        criteriaQuery.select(criteriaBuilder.construct(LancamentoEstatisticaPessoaDTO.class,
                root.get(Lancamento_.tipoLancamento),
                root.get(Lancamento_.pessoa),
                criteriaBuilder.sum(root.get(Lancamento_.valor))));

        criteriaQuery.where(
                criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), inicio),
                criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), fim));

        criteriaQuery.groupBy(
                root.get(Lancamento_.tipoLancamento),
                root.get(Lancamento_.pessoa));

        TypedQuery<LancamentoEstatisticaPessoaDTO> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getResultList();
    }
}
