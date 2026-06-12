package com.leiloes.domain.specification;

/**
 * Interface genérica do Specification Pattern.
 *
 * Isola uma regra de negócio em uma classe com único método de verificação,
 * tornando cada regra testável e reutilizável independentemente.
 *
 * @param <T> O tipo do objeto candidato que será avaliado pela regra.
 */
public interface Specification<T> {

    /**
     * Avalia se a regra de negócio é satisfeita pelo candidato.
     *
     * @param candidate Objeto sob avaliação.
     * @return true se satisfeito, false caso contrário.
     */
    boolean isSatisfiedBy(T candidate);

    /**
     * Retorna a mensagem de erro a ser exibida caso a condição falhe.
     */
    default String mensagemDeErro() {
        return "Condição de negócio não satisfeita.";
    }
}
