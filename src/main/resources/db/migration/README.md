# Database Migrations

Este diretório contém as migrations do Flyway para o projeto Spring Security.

## Estrutura das Migrations

### V1\_\_create_roles_table.sql

- Cria a tabela `tb_roles` para armazenar os roles do sistema
- Insere os roles padrão: BASIC (ID: 1) e ADMIN (ID: 2)
- Cria índices para otimização de performance

### V2\_\_create_users_table.sql

- Cria a tabela `tb_users` para armazenar os usuários
- Utiliza UUID como chave primária
- Campos: user_id, username, password, created_at, updated_at
- Cria índices para otimização de performance

### V3\_\_create_users_roles_table.sql

- Cria a tabela de relacionamento `tb_users_roles`
- Implementa relacionamento many-to-many entre users e roles
- Chave primária composta por user_id e role_id
- Foreign keys com CASCADE DELETE

### V4\_\_create_albums_table.sql

- Cria a tabela `tb_album` para armazenar os álbuns
- Relacionamento com usuário (many-to-one)
- Campos: album_id, user_id, title, description, cover_image_id, created_at, updated_at
- Foreign key para user_id com CASCADE DELETE

### V5\_\_create_images_table.sql

- Cria a tabela `tb_image` para armazenar as imagens
- Relacionamento com álbum (many-to-one)
- Campos: image_id, album_id, img_url, file_name, file_type, file_size, date_file, description, created_at, updated_at
- Foreign key para album_id com CASCADE DELETE

### V6\_\_add_foreign_key_cover_image.sql

- Adiciona foreign key constraint para cover_image_id na tabela de álbuns
- Permite que um álbum tenha uma imagem de capa
- SET NULL em caso de exclusão da imagem

### V7\_\_insert_sample_data.sql

- Insere dados de exemplo para testes
- Cria usuários de teste (admin, user1, user2)
- Atribui roles aos usuários
- Cria álbuns de exemplo

## Configuração do Flyway

As configurações do Flyway estão no `application.properties`:

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
```

## Execução das Migrations

As migrations são executadas automaticamente quando a aplicação é iniciada. O Flyway:

1. Verifica se o banco de dados está na versão esperada
2. Executa as migrations pendentes em ordem sequencial
3. Valida a integridade das migrations
4. Registra o histórico de execução na tabela `flyway_schema_history`

## Estrutura do Banco de Dados

```
tb_roles (role_id, name, created_at, updated_at)
    ↓ (many-to-many)
tb_users_roles (user_id, role_id)
    ↓ (many-to-many)
tb_users (user_id, username, password, created_at, updated_at)
    ↓ (one-to-many)
tb_album (album_id, user_id, title, description, cover_image_id, created_at, updated_at)
    ↓ (one-to-many)
tb_image (image_id, album_id, img_url, file_name, file_type, file_size, date_file, description, created_at, updated_at)
    ↑ (one-to-one)
tb_album.cover_image_id
```

## Notas Importantes

1. **Senhas**: As senhas nos dados de exemplo devem ser substituídas por senhas hasheadas em produção
2. **UUIDs**: O projeto utiliza UUIDs para as chaves primárias das entidades principais
3. **Timestamps**: Todas as tabelas incluem campos created_at e updated_at
4. **Índices**: Foram criados índices estratégicos para otimizar consultas frequentes
5. **Foreign Keys**: Todas as relações são protegidas por foreign keys com ações apropriadas
