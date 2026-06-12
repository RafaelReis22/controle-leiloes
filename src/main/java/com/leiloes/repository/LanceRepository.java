package com.leiloes.repository;

import com.leiloes.domain.model.Lance;
import com.leiloes.dto.output.LanceDetalhe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LanceRepository extends JpaRepository<Lance, Long> {

    @Query("""
        SELECT NEW com.leiloes.dto.output.LanceDetalhe(la.id, u.nome, la.valor, la.dataHora)
        FROM Lance la JOIN la.usuario u
        WHERE la.leilao.id = :idLeilao
        ORDER BY la.valor DESC, la.dataHora ASC
        """)
    List<LanceDetalhe> findDetalhesByLeilaoId(@Param("idLeilao") Long idLeilao);

    @Query(value = """
        SELECT la.id, u.nome, la.valor, la.data_hora
        FROM lances la JOIN usuarios u ON u.id = la.id_usuario
        WHERE la.id_leilao = :idLeilao
        ORDER BY la.valor DESC, la.data_hora ASC LIMIT 1
        """, nativeQuery = true)
    Optional<LanceVencedorProjection> findVencedor(@Param("idLeilao") Long idLeilao);
}
