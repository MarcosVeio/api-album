# Spring Security Album API

Uma API RESTful desenvolvida em Spring Boot com Spring Security para gerenciamento de álbuns de fotos com autenticação JWT e integração com AWS S3.

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.4.8**
- **Spring Security** com OAuth2 Resource Server
- **Spring Data JPA** com Hibernate
- **PostgreSQL** como banco de dados
- **Flyway** para migração de banco de dados
- **JWT** para autenticação
- **AWS SDK S3** para armazenamento de imagens
- **Lombok** para redução de boilerplate
- **Maven** para gerenciamento de dependências

## 📋 Pré-requisitos

- Java 21 ou superior
- Maven 3.6+
- PostgreSQL 12+
- Conta AWS com acesso ao S3 (opcional)

## 🏗️ Arquitetura do Projeto

### Estrutura de Módulos

```
src/main/java/tech/marcosmartinelli/springsecurity/
├── config/                    # Configurações da aplicação
│   ├── SecurityConfig.java    # Configuração de segurança
│   ├── AWSConfig.java         # Configuração AWS S3
│   └── AdminUserConfig.java   # Configuração de usuário admin
├── modules/
│   ├── auth/                  # Módulo de autenticação
│   │   ├── TokenController.java
│   │   └── dtos/
│   │       ├── LoginRequestDTO.java
│   │       └── LoginResponseDTO.java
│   ├── users/                 # Módulo de usuários
│   │   ├── User.java
│   │   ├── UserController.java
│   │   ├── UserService.java
│   │   ├── UserRepository.java
│   │   └── dtos/
│   │       ├── UserRequestDTO.java
│   │       └── UserResponseDTO.java
│   ├── album/                 # Módulo de álbuns
│   │   ├── Album.java
│   │   ├── AlbumController.java
│   │   ├── AlbumService.java
│   │   ├── AlbumRepository.java
│   │   └── dtos/
│   │       ├── AlbumRequestDTO.java
│   │       └── AlbumResponseDTO.java
│   ├── image/                 # Módulo de imagens
│   │   ├── Image.java
│   │   ├── ImageService.java
│   │   └── ImageRepository.java
│   └── role/                  # Módulo de roles
│       ├── Role.java
│       ├── RoleRepository.java
│       └── enums/
│           └── RolesEnum.java
```

## 🔐 Sistema de Autenticação

### JWT (JSON Web Token)

- **Algoritmo**: RSA256
- **Duração**: 300 segundos (5 minutos)
- **Claims**: issuer, subject, scope, issuedAt, expiresAt

### Roles e Permissões

- **BASIC**: Acesso básico para criar álbuns
- **ADMIN**: Acesso completo, incluindo listagem de usuários

### Endpoints Públicos

- `POST /login` - Autenticação de usuário
- `POST /users` - Criação de novo usuário

### Endpoints Protegidos

- `GET /api/users` - Listar usuários (requer ADMIN)
- `POST /api/albums` - Criar álbum (requer BASIC)

## 🗄️ Modelo de Dados

### Entidades Principais

#### User

- `userId` (UUID) - Identificador único
- `username` - Nome de usuário
- `password` - Senha hasheada com BCrypt
- `roles` - Conjunto de roles (BASIC/ADMIN)
- `createdAt` / `updatedAt` - Timestamps

#### Album

- `albumId` (UUID) - Identificador único
- `user` - Relacionamento com usuário
- `title` - Título do álbum
- `description` - Descrição do álbum
- `coverImage` - Imagem de capa (opcional)
- `images` - Conjunto de imagens do álbum
- `createdAt` / `updatedAt` - Timestamps

#### Image

- `imageId` (UUID) - Identificador único
- `album` - Relacionamento com álbum
- `imgUrl` - URL da imagem no S3
- `fileName` - Nome original do arquivo
- `fileType` - Tipo do arquivo
- `fileSize` - Tamanho do arquivo
- `dateFile` - Data do arquivo
- `description` - Descrição da imagem
- `createdAt` / `updatedAt` - Timestamps

## 🚀 Como Executar

### 1. Configuração do Banco de Dados

Crie um banco PostgreSQL e configure as propriedades no `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/album_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 2. Configuração das Chaves JWT

Gere um par de chaves RSA e configure no `application.properties`:

```properties
jwt.public.key=classpath:rsa/public.key
jwt.private.key=classpath:rsa/private.key
```

### 3. Configuração AWS S3 (Opcional)

Para usar o S3, configure as credenciais:

```properties
aws.region=us-east-2
aws.accessKey=sua_access_key
aws.secretKey=sua_secret_key
```

### 4. Executando a Aplicação

```bash
# Clone o repositório
git clone <url-do-repositorio>
cd springsecurity

# Execute a aplicação
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## 📚 API Endpoints

### Autenticação

#### POST /login

Autentica um usuário e retorna um JWT.

**Request:**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**

```json
{
  "username": "admin",
  "userId": "uuid-do-usuario",
  "token": "jwt-token",
  "expiresIn": 300
}
```

### Usuários

#### POST /users

Cria um novo usuário.

**Request:**

```json
{
  "username": "novo_usuario",
  "password": "senha123"
}
```

#### GET /api/users

Lista todos os usuários (requer role ADMIN).

**Headers:**

```
Authorization: Bearer <jwt-token>
```

### Álbuns

#### POST /api/albums

Cria um novo álbum com imagem de capa (requer role BASIC).

**Request (multipart/form-data):**

- `title`: Título do álbum
- `description`: Descrição do álbum
- `userId`: ID do usuário
- `coverImage`: Arquivo de imagem

**Headers:**

```
Authorization: Bearer <jwt-token>
```

## 🔧 Configurações

### application.properties

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/album_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# JWT
jwt.public.key=classpath:rsa/public.key
jwt.private.key=classpath:rsa/private.key

# AWS S3
aws.region=us-east-2
aws.accessKey=sua_access_key
aws.secretKey=sua_secret_key

# Server
server.port=8080
```

## 🗄️ Migrações do Banco

O projeto utiliza Flyway para gerenciar as migrações do banco de dados. As migrações estão em `src/main/resources/db/migration/`:

1. **V1** - Criação da tabela de roles
2. **V2** - Criação da tabela de usuários
3. **V3** - Tabela de relacionamento users-roles
4. **V4** - Criação da tabela de álbuns
5. **V5** - Criação da tabela de imagens
6. **V6** - Foreign key para imagem de capa
7. **V7** - Dados de exemplo

## 🔒 Segurança

### Medidas Implementadas

- **JWT Authentication**: Tokens seguros com RSA256
- **Password Hashing**: BCrypt para senhas
- **Role-based Access Control**: Controle de acesso baseado em roles
- **CSRF Protection**: Desabilitado apenas em desenvolvimento
- **Stateless Sessions**: Sem armazenamento de sessão
- **Input Validation**: Validação de entrada nos DTOs

### Boas Práticas

- Senhas sempre hasheadas com BCrypt
- Tokens JWT com tempo de expiração
- Validação de entrada em todos os endpoints
- Logs de auditoria para operações sensíveis
- Configurações sensíveis em variáveis de ambiente

## 🧪 Testes

Para executar os testes:

```bash
./mvnw test
```

## 📦 Build

Para gerar o JAR executável:

```bash
./mvnw clean package
```

O arquivo será gerado em `target/springsecurity-0.0.1-SNAPSHOT.jar`

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👨‍💻 Autor

**Marco Martinelli**

## 📞 Suporte

Para dúvidas ou suporte, entre em contato através dos issues do GitHub.

---

**Nota**: Este projeto é uma demonstração de implementação de Spring Security com JWT e gerenciamento de álbuns. Para uso em produção, considere implementar medidas adicionais de segurança e configurações específicas do ambiente.
