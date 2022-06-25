CREATE TABLE usuario
(
    id integer NOT NULL DEFAULT nextval('usuario_id_seq'::regclass),
    nome character varying(50) NOT NULL,
    email character varying(255) NOT NULL,
    senha character varying(255) NOT NULL,
    logradouro character varying(120) NOT NULL,
    numero integer NOT NULL,
    bairro character varying(50) NOT NULL,
    cidade character varying(30) NOT NULL,
    uf character(2) NOT NULL,
    cep character(9) NOT NULL,
    complemento character varying(20),
    CONSTRAINT usuario_pkey PRIMARY KEY (id),
    CONSTRAINT usuario_email_key UNIQUE (email)
)

CREATE TABLE categoria
(
    id integer NOT NULL DEFAULT nextval('categoria_id_seq'::regclass),
    nome character varying(30) NOT NULL,
    descricao character varying(150) NOT NULL,
    CONSTRAINT categoria_pkey PRIMARY KEY (id),
    CONSTRAINT categoria_nome_key UNIQUE (nome)
)

CREATE TABLE ramo
(
    id integer NOT NULL DEFAULT nextval('ramo_id_seq'::regclass),
    nome character varying(30) NOT NULL,
    descricao character varying(150) NOT NULL,
    CONSTRAINT ramo_pkey PRIMARY KEY (id),
    CONSTRAINT ramo_nome_key UNIQUE (nome)
)

CREATE TABLE produto
(
    id integer NOT NULL DEFAULT nextval('produto_id_seq'::regclass),
    nome character varying(150) NOT NULL,
    marca character varying(50) NOT NULL,
    tamanho character varying(20) NOT NULL,
    cor character varying(20),
    "criadoPor" integer NOT NULL,
    "categoriaId" integer NOT NULL,
    CONSTRAINT produto_pkey PRIMARY KEY (id),
    CONSTRAINT produto_categoriaid_fkey FOREIGN KEY ("categoriaId")
        REFERENCES categoria (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT produto_criadopor_fkey FOREIGN KEY ("criadoPor")
        REFERENCES usuario (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE mercado
(
    id integer NOT NULL DEFAULT nextval('mercado_id_seq'::regclass),
    nome character varying(50) NOT NULL,
    logradouro character varying(120) NOT NULL,
    numero integer NOT NULL,
    complemento character varying(20) NOT NULL,
    bairro character varying(50) NOT NULL,
    cidade character varying(30) NOT NULL,
    uf character(2) NOT NULL,
    cep character(9) NOT NULL,
    "criadoPor" integer NOT NULL,
    "ramoId" integer NOT NULL,
    CONSTRAINT mercado_pkey PRIMARY KEY (id),
    CONSTRAINT mercado_nome_key UNIQUE (nome),
    CONSTRAINT mercado_criadopor_fkey FOREIGN KEY ("criadoPor")
        REFERENCES usuario (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT mercado_ramoid_fkey FOREIGN KEY ("ramoId")
        REFERENCES ramo (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE estoque
(
    id integer NOT NULL DEFAULT nextval('estoque_id_seq'::regclass),
    "criadoPor" integer NOT NULL,
    "produtoId" integer NOT NULL,
    "mercadoId" integer NOT NULL,
    CONSTRAINT estoque_pkey PRIMARY KEY (id),
    CONSTRAINT estoque_criadopor_fkey FOREIGN KEY ("criadoPor")
        REFERENCES usuario (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT estoque_mercadoid_fkey FOREIGN KEY ("mercadoId")
        REFERENCES mercado (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT estoque_produtoid_fkey FOREIGN KEY ("produtoId")
        REFERENCES produto (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE sugestao
(
    id integer NOT NULL DEFAULT nextval('sugestao_id_seq'::regclass),
    preco integer NOT NULL,
    "timestamp" timestamp without time zone,
    "estoqueId" integer NOT NULL,
    "criadoPor" integer NOT NULL,
    CONSTRAINT sugestao_pkey PRIMARY KEY (id),
    CONSTRAINT "sugestao_estoqueId_fkey" FOREIGN KEY (id)
        REFERENCES estoque (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "sugestao_criadoPor_fkey" FOREIGN KEY ("criadoPor")
        REFERENCES usuario (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)