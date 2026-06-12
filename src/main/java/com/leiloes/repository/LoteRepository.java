package com.leiloes.repository;

import com.leiloes.domain.model.Lote;
import com.leiloes.dto.output.LoteResumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    @Query("""
        SELECT NEW com.leiloes.dto.output.LoteResumo(
            lo.id, lo.descricao, lo.precoMinimo, u.nome, SIZE(lo.bens)
        )
        FROM Lote lo JOIN lo.responsavel u
        """)
    List<LoteResumo> listarTodosComoResumo();
}
