# Academy-cli

Aplicação **CLI em Java** para **CRUD de alunos** usando **PostgreSQL via JDBC**.  
Projeto em camadas: **CLI → Service → Repository → Domain**.  
Tabela criada manualmente no banco.

---

## Requisitos

- Java 17+ (ou 21)
- Maven 3.8+
- PostgreSQL 13+
- Dependência do driver no `pom.xml`:
  ```xml
  <dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
  </dependency>

  Banco de dados (criação manual)

## Banco de dados (criação manual)

> Pré-requisitos: PostgreSQL em execução local e acesso ao `psql` (ou a um cliente SQL de sua preferência).

### 1) Conectar ao database postgres
```bash
psql -U postgres -d postgres
```

### 2) (Opcional) Criar usuário da aplicação

>Se preferir usar o próprio usuário `postgres`, pode pular esta etapa.
```sql
CREATE USER academy WITH PASSWORD 'academy';
GRANT CONNECT ON DATABASE postgres TO academy;
GRANT USAGE ON SCHEMA public TO academy;
```

### 3) Criar a tabela `students` no schema `public`
```sql
CREATE TABLE IF NOT EXISTS public.students (
  id          BIGSERIAL PRIMARY KEY,
  name        VARCHAR(120) NOT NULL,
  cpf         VARCHAR(11)  NOT NULL UNIQUE,
  email       VARCHAR(160) NOT NULL,
  birth_date  DATE,
  created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### Configuração da aplicação

> a classe `config.Database` lê váriaveis de ambiente; se não definir usa padrões internos.

## Exemplos de configuração

### Linux / macOS
```bash
export DB_URL="jdbc:postgresql://localhost:5432/postgres"
export DB_USER="postgres"
export DB_PASS=222324     
```
```powershell
# Windows PowerShell
$env:DB_URL="jdbc:postgresql://localhost:5432/postgres"
$env:DB_USER="postgres"
$env:DB_PASS=222324
```

## Build e execução

### Via Maven
```bash
mvn clean package
mvn exec:java -Decec.mainClass="service.Main"
```

>Ou execute a classe `service.Main` pela IDE


## Como usar (menu do CLI)
```bash
=== Students CLI ===
1) Cadastrar
2) Listar
3) Buscar por ID
4) Atualizar (nome/email)
5) Remover
0) Sair
```

- **Cadastrar**: `name`, `cpf` (11 dígitos), `email`, `birth_date` (YYYY-MM-DD, opcional).
- **Listar**: `page` (começa em 0) e `size`.
- **Buscar/Atualizar/Remover**: informe o id.


## Estrutura do projeto
src/main/java
├─ cli/                 # Menu (interface de linha de comando)
├─ config/              # Conexão com o banco (Database)
├─ domain/              # Modelo (Student)
├─ repository/          # Acesso a dados (JDBC)
└─ service/             # Regras de uso (StudentService) + Main


## Paginação

| Parâmetro | Significado        | Observação          |
|-----------|-------------------|---------------------|
| page      | Página (zero-based) | Primeira página é 0 |
| size      | Itens por página    | Ex.: 10, 20, 50     |



### Implementação SQL:
```sql
-- LIMIT = size
-- OFFSET = page * size
SELECT * FROM public.students
ORDER BY created_at DESC
LIMIT :size OFFSET (:page * :size);
```


## Tratamento de erros
- Entradas inválidas no CLI lançam `IllegalArgumentException` → o menu continua rodando.
- Erros de banco são encapsulados como `RuntimeException` preservando a causa `SQLException`.
- CPF duplicado impede cadastro.
