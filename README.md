# Controle de Leilões
> Trabalho Final · Programação de Software Aplicado · 2026/1

Sistema web para controle de leilões com lances abertos e fechados.  
**Stack**: Java 21 · Spring Boot 3.3 · Thymeleaf · PostgreSQL 16 · Maven

---

## Pré-requisitos

Antes de começar, verifique se você tem instalado:

| Software | Versão mínima | Download |
|----------|---------------|----------|
| Java (JDK) | 21 | https://adoptium.net |
| Maven | 3.9+ | https://maven.apache.org/download.cgi |
| PostgreSQL | 14+ | https://www.postgresql.org/download |

Para verificar se estão instalados, abra o terminal e rode:
```bash
java -version
mvn -version
psql --version
```

---

## Passo a Passo para Rodar

### Passo 1 — Criar o banco de dados

Abra o **terminal** e execute:

```bash
psql -U postgres -c "CREATE DATABASE leiloes_db;"
```

> **Dica (Windows):** se o comando `psql` não for reconhecido, abra o **pgAdmin** e execute o SQL acima no Query Tool.

---

### Passo 2 — Criar as tabelas e popular com dados de teste

Dentro da pasta raiz do projeto, execute os dois comandos abaixo em ordem:

```bash
psql -U postgres -d leiloes_db -f db/schema.sql
psql -U postgres -d leiloes_db -f db/seed.sql
```

> **Dica (pgAdmin / DBeaver):** selecione o banco `leiloes_db`, abra o Query Tool e execute o conteúdo dos arquivos `db/schema.sql` e depois `db/seed.sql`.

O `seed.sql` insere automaticamente:
- 4 usuários (compradores e leiloeiro)
- Categorias, bens e lotes (incluindo lote com múltiplos bens)
- 4 leilões com lances já cadastrados (aberto/fechado, em andamento/encerrado)

---

### Passo 3 — Configurar a senha do PostgreSQL

Abra o arquivo `src/main/resources/application.properties` e ajuste a senha:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/leiloes_db
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_AQUI
```

---

### Passo 4 — Iniciar a aplicação

Na pasta raiz do projeto, execute:

```bash
mvn spring-boot:run
```

Aguarde a mensagem `Started ControleLeiloesApplication` no terminal.

---

### Passo 5 — Acessar no navegador

Abra o navegador e acesse:

**http://localhost:8080/leiloes**

---

## Telas disponíveis

| URL | Descrição |
|-----|-----------|
| `/leiloes` | Lista todos os leilões com status (Aguardando / Em Andamento / Encerrado) |
| `/leiloes/novo` | Formulário para criar novo leilão |
| `/leiloes/{id}` | Detalhes do leilão, histórico de lances e vencedor |
| `/leiloes/{id}/lance` | Formulário para dar lance em um leilão ativo |
| `/lotes` | Lista todos os lotes cadastrados |
| `/lotes/novo` | Formulário para criar novo lote de bens |
| `/lotes/{id}` | Detalhes do lote e seus bens |

---

## Regras de negócio implementadas

- **Leilão Aberto**: lances visíveis a qualquer momento durante o andamento.
- **Leilão Fechado**: lances ocultos durante o andamento; revelados apenas após o encerramento.
- **Validações de lance** (Specification Pattern):
  - Leilão precisa estar em andamento (entre data de início e término).
  - Valor do lance deve ser maior que o preço mínimo do lote.
  - Em leilões abertos, o valor deve superar o maior lance anterior.
  - O responsável pelo leilão e pelo lote não pode dar lances.
- **Vencedor**: o lance com maior valor enviado primeiro (data/hora mais antiga em caso de empate).

---

## Arquitetura e Padrões de Projeto

```
src/main/java/com/leiloes/
├── controller/       → Camada de Apresentação (Padrão MVC)
├── service/          → Camada de Serviço (regras de negócio)
├── domain/
│   ├── model/        → Camada de Domínio (Padrão Domain Model)
│   ├── enums/        → Enumerações (StatusLeilao, TipoLeilao)
│   └── specification/→ Regras de lance desacopladas (Padrão Specification)
├── repository/       → Camada de Persistência (Padrão Repository via Spring Data JPA)
├── dto/              → Objetos de transferência (Records imutáveis)
└── exception/        → Exceções de domínio
```

---

## Documentação

A pasta `docs/` contém:
- `diagrama-banco.png` — Diagrama do banco de dados relacional
- `PADROES.md` — Descrição detalhada dos padrões de projeto utilizados
- `FAQ.pdf` — Perguntas frequentes sobre o projeto
- `Historico de Implementação.pdf` — Histórico de implementação

---

## Problemas comuns

**Erro ao iniciar: `password authentication failed`**  
→ Verifique a senha no `application.properties` (Passo 3).

**Erro ao iniciar: `database "leiloes_db" does not exist`**  
→ Execute o Passo 1 novamente.

**Erro ao iniciar: `Schema-validation failed`**  
→ As tabelas não existem ou estão desatualizadas. Execute o Passo 2 novamente (o `schema.sql` usa `CREATE TABLE IF NOT EXISTS`).

**Porta 8080 em uso**  
→ Adicione `server.port=8081` ao `application.properties` e acesse `http://localhost:8081/leiloes`.
