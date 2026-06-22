-- =============================================================
-- Controle de Leilões — Script de Popular Banco (Seed)
-- Execute este script após criar a estrutura com o schema.sql
-- =============================================================

-- Limpar dados anteriores para evitar duplicados
TRUNCATE TABLE lances CASCADE;
TRUNCATE TABLE leiloes CASCADE;
TRUNCATE TABLE lotes_bens CASCADE;
TRUNCATE TABLE lotes CASCADE;
TRUNCATE TABLE bens CASCADE;
TRUNCATE TABLE categorias CASCADE;
TRUNCATE TABLE usuarios CASCADE;

-- Resetar as sequências dos IDs
ALTER SEQUENCE usuarios_id_seq RESTART WITH 1;
ALTER SEQUENCE categorias_id_seq RESTART WITH 1;
ALTER SEQUENCE bens_id_seq RESTART WITH 1;
ALTER SEQUENCE lotes_id_seq RESTART WITH 1;
ALTER SEQUENCE leiloes_id_seq RESTART WITH 1;
ALTER SEQUENCE lances_id_seq RESTART WITH 1;

-- 1. Inserir Usuários
INSERT INTO usuarios (nome, cpf_cnpj, email) VALUES
('Ana Silva', '123.456.789-00', 'ana.silva@email.com'),
('Bruno Costa', '234.567.890-11', 'bruno.costa@email.com'),
('Carlos Souza', '345.678.901-22', 'carlos.souza@email.com'),
('Leiloeira Oficial S/A', '12.345.678/0001-90', 'contato@leiloeira.com');

-- 2. Inserir Categorias
INSERT INTO categorias (nome) VALUES
('Veículos'),
('Eletrônicos'),
('Imóveis');

-- 3. Inserir Bens
INSERT INTO bens (descricao_breve, descricao_completa, id_categoria) VALUES
-- Eletrônicos (para o Lote 1 - Múltiplos bens)
('iPhone 15 Pro Max', 'Aparelho seminovo, 256GB, cor Titânio Natural, com caixa e carregador original.', 2),
('MacBook Pro M3', 'Notebook em perfeito estado, 16GB RAM, 512GB SSD, bateria com 100% de saúde.', 2),
('iPad Air', 'Tablet seminovo, 64GB, Wi-Fi, cor Azul, com capa protetora.', 2),
-- Veículos (para o Lote 2)
('Honda Civic Sedan 2022', 'Modelo LX 2.0 flex, automático, cor preta, único dono, 30.000km, IPVA pago.', 1),
-- Imóveis (para o Lote 3)
('Apartamento Centro 2Q', 'Apartamento residencial de 2 quartos, 70m², no centro da cidade, mobiliado e com vaga de garagem.', 3),
-- Eletrônicos avulsos (para o Lote 4)
('Console PlayStation 5', 'Edição com leitor de disco, 825GB SSD, acompanha 2 controles DualSense.', 2);

-- 4. Inserir Lotes (Preço mínimo, Responsável)
INSERT INTO lotes (descricao, preco_minimo, id_usuario_responsavel) VALUES
('Lote de Eletrônicos Premium (Combo Apple)', 6000.00, 4), -- Lote 1
('Honda Civic LX 2022', 90000.00, 4),                      -- Lote 2
('Apartamento Central 2 Quartos', 250000.00, 4),            -- Lote 3
('Console PS5 Mídia Física', 1500.00, 4);                     -- Lote 4

-- 5. Associar Bens aos Lotes (lotes_bens)
-- Lote 1 possui 3 bens (iPhone, MacBook e iPad) -> Lote com múltiplos bens exigido no trabalho
INSERT INTO lotes_bens (id_lote, id_bem) VALUES
(1, 1),
(1, 2),
(1, 3),
-- Lote 2 possui 1 bem (Civic)
(2, 4),
-- Lote 3 possui 1 bem (Apartamento)
(3, 5),
-- Lote 4 possui 1 bem (PS5)
(4, 6);

-- 6. Inserir Leilões (com datas relativas usando INTERVAL para que o status esteja sempre correto em demonstrações)
INSERT INTO leiloes (tipo, data_inicio, data_termino, id_usuario_responsavel, id_lote) VALUES
-- Leilão 1: ABERTO em andamento (Início há 2 dias, Término daqui a 2 dias)
('ABERTO', NOW() - INTERVAL '2 days', NOW() + INTERVAL '2 days', 4, 1),

-- Leilão 2: ABERTO encerrado (Início há 5 dias, Término há 1 dia)
('ABERTO', NOW() - INTERVAL '5 days', NOW() - INTERVAL '1 day', 4, 2),

-- Leilão 3: FECHADO em andamento (Início há 1 dia, Término daqui a 3 dias)
('FECHADO', NOW() - INTERVAL '1 day', NOW() + INTERVAL '3 days', 4, 3),

-- Leilão 4: FECHADO encerrado (Início há 4 dias, Término há 2 dias)
('FECHADO', NOW() - INTERVAL '4 days', NOW() - INTERVAL '2 days', 4, 4);

-- 7. Inserir Lances
-- Lances para Leilão 1 (ABERTO em andamento, preço mínimo R$ 6000.00)
INSERT INTO lances (data_hora, valor, id_usuario, id_leilao) VALUES
(NOW() - INTERVAL '1 day', 6200.00, 1, 1),
(NOW() - INTERVAL '12 hours', 6500.00, 2, 1),
(NOW() - INTERVAL '2 hours', 7100.00, 3, 1);

-- Lances para Leilão 2 (ABERTO encerrado, preço mínimo R$ 90000.00)
-- Ana Silva (ID 1) ofertou R$ 91.000, mas Bruno Costa (ID 2) cobriu com R$ 93.500 e venceu.
INSERT INTO lances (data_hora, valor, id_usuario, id_leilao) VALUES
(NOW() - INTERVAL '4 days', 91000.00, 1, 2),
(NOW() - INTERVAL '3 days', 93500.00, 2, 2);

-- Lances para Leilão 3 (FECHADO em andamento, preço mínimo R$ 250000.00)
-- Lances registrados no banco, mas ocultos na tela de detalhes pois está em andamento.
INSERT INTO lances (data_hora, valor, id_usuario, id_leilao) VALUES
(NOW() - INTERVAL '18 hours', 255000.00, 2, 3),
(NOW() - INTERVAL '5 hours', 258000.00, 3, 3);

-- Lances para Leilão 4 (FECHADO encerrado, preço mínimo R$ 1500.00)
-- Lances revelados pois o leilão fechado já terminou. Carlos Souza (ID 3) venceu com R$ 1850.
INSERT INTO lances (data_hora, valor, id_usuario, id_leilao) VALUES
(NOW() - INTERVAL '3 days', 1600.00, 1, 4),
(NOW() - INTERVAL '3 days' + INTERVAL '1 hour', 1850.00, 3, 4);
