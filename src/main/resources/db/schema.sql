-- =============================================================
-- Controle de Leilões — DDL do Banco de Dados
-- Execute no PostgreSQL antes de subir a aplicação
-- =============================================================

CREATE TABLE IF NOT EXISTS usuarios (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(150) NOT NULL,
    cpf_cnpj   VARCHAR(20)  NOT NULL UNIQUE,
    email      VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categorias (
    id   BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS bens (
    id                  BIGSERIAL PRIMARY KEY,
    descricao_breve     VARCHAR(200) NOT NULL,
    descricao_completa  TEXT,
    id_categoria        BIGINT NOT NULL REFERENCES categorias(id)
);

CREATE TABLE IF NOT EXISTS lotes (
    id                     BIGSERIAL PRIMARY KEY,
    descricao              TEXT NOT NULL,
    preco_minimo           NUMERIC(12,2) NOT NULL CHECK (preco_minimo > 0),
    id_usuario_responsavel BIGINT NOT NULL REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS lotes_bens (
    id_lote BIGINT NOT NULL REFERENCES lotes(id),
    id_bem  BIGINT NOT NULL REFERENCES bens(id),
    PRIMARY KEY (id_lote, id_bem)
);

CREATE TABLE IF NOT EXISTS leiloes (
    id                     BIGSERIAL PRIMARY KEY,
    tipo                   VARCHAR(10) NOT NULL CHECK (tipo IN ('ABERTO', 'FECHADO')),
    data_inicio            TIMESTAMP NOT NULL,
    data_termino           TIMESTAMP NOT NULL,
    id_usuario_responsavel BIGINT NOT NULL REFERENCES usuarios(id),
    id_lote                BIGINT NOT NULL REFERENCES lotes(id),
    CONSTRAINT datas_validas CHECK (data_inicio < data_termino)
);

CREATE TABLE IF NOT EXISTS lances (
    id          BIGSERIAL PRIMARY KEY,
    data_hora   TIMESTAMP     NOT NULL DEFAULT NOW(),
    valor       NUMERIC(12,2) NOT NULL CHECK (valor > 0),
    id_usuario  BIGINT NOT NULL REFERENCES usuarios(id),
    id_leilao   BIGINT NOT NULL REFERENCES leiloes(id)
);
