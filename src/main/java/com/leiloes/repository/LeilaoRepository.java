package com.leiloes.repository;

import com.leiloes.domain.model.Leilao;
import com.leiloes.dto.output.LeilaoResumo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeilaoRepository extends JpaRepository<Leilao, Long> {

    @Query("""
        SELECT NEW com.leiloes.dto.output.LeilaoResumo(
            l.id, l.tipo, lo.descricao, lo.precoMinimo, u.nome,
            l.dataInicio, l.dataTermino, COUNT(la.id)
        )
        FROM Leilao l
        JOIN l.lote lo JOIN l.responsavel u LEFT JOIN l.lances la
        GROUP BY l.id, l.tipo, lo.descricao, lo.precoMinimo, u.nome,
                 l.dataInicio, l.dataTermino
        ORDER BY l.dataInicio DESC
        """)
    List<LeilaoResumo> listarTodosComoResumo();

    @Query("""
        SELECT NEW com.leiloes.dto.output.LeilaoResumo(
            l.id, l.tipo, lo.descricao, lo.precoMinimo, u.nome,
            l.dataInicio, l.dataTermino, COUNT(la.id)
        )
        FROM Leilao l
        JOIN l.lote lo JOIN l.responsavel u LEFT JOIN l.lances la
        WHERE l.dataInicio <= :agora AND l.dataTermino > :agora
        GROUP BY l.id, l.tipo, lo.descricao, lo.precoMinimo, u.nome,
                 l.dataInicio, l.dataTermino
        ORDER BY l.dataInicio DESC
        """)
    List<LeilaoResumo> listarEmAndamentoComoResumo(@Param("agora") LocalDateTime agora);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Leilao l WHERE l.id = :id")
    Optional<Leilao> findByIdForUpdate(@Param("id") Long id);

}
