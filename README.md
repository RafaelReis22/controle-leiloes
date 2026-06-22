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

## 🚀 Como rodar o projeto do jeito mais fácil

Se você está usando o Windows e ferramentas visuais como **IntelliJ, Eclipse, VS Code** e **pgAdmin**, siga estes passos simples:

### Passo 1 — Preparar o Banco de Dados (PostgreSQL)

1. Abra o **pgAdmin** (ou DBeaver).
2. Crie um novo banco de dados vazio chamado `leiloes_db`.
3. Pronto! Não precisa criar as tabelas manualmente. Quando você rodar o projeto, o próprio sistema vai se encarregar de criar todas as tabelas e já preencher com os dados de teste (usuários e leilões) automaticamente!

### Passo 2 — Conferir a senha do Banco no Projeto

1. Dentro da sua IDE, abra o arquivo `src/main/resources/application.properties`.
2. Confirme se as credenciais estão iguais às do seu PostgreSQL. Já deixamos configurado assim para você:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/leiloes_db
spring.datasource.username=postgres
spring.datasource.password=maneger
```
*(Altere a senha `maneger` caso a do seu computador seja diferente)*

### Passo 3 — Rodar o Projeto pela IDE

Como rodar pelo terminal pode gerar erro se o Maven/Java não estiverem configurados no Windows, **o jeito mais garantido é rodar usando a sua própria IDE**:

- **No Eclipse:** Clique com o botão direito no projeto > `Run As` > `Spring Boot App` (ou `Java Application` na classe principal).
- **No IntelliJ:** Encontre o arquivo `ControleLeiloesApplication.java` (na pasta `src/main/java/...`), clique com o botão direito nele e escolha `Run 'ControleLeiloesApplication'`.
- **No VS Code:** Abra a classe `ControleLeiloesApplication.java` e clique no botão de `Run` que aparece acima do método `main`.

Aguarde o console mostrar que a aplicação iniciou (`Started ControleLeiloesApplication`).

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
