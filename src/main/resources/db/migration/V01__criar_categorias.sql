CREATE TABLE categoria (
    codigo BIGINT IDENTITY,
    nome VARCHAR(50) NOT NULL,

    CONSTRAINT pk_categoria PRIMARY KEY (codigo)
)

INSERT INTO categoria (nome) values ('Lazer');
INSERT INTO categoria (nome) values ('Alimentação');
INSERT INTO categoria (nome) values ('Supermercado');
INSERT INTO categoria (nome) values ('Farmácia');
INSERT INTO categoria (nome) values ('Outros');