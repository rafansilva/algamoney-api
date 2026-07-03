CREATE TABLE contato (
	codigo BIGINT IDENTITY,
	codigo_pessoa BIGINT NOT NULL,
	nome VARCHAR(50) NOT NULL,
	email VARCHAR(100) NOT NULL,
	telefone VARCHAR(20) NOT NULL,

	CONSTRAINT pk_contato PRIMARY KEY (codigo)
)

ALTER TABLE contato ADD CONSTRAINT fk_contato_pessoa
FOREIGN KEY (codigo_pessoa) REFERENCES pessoa(codigo)

INSERT INTO contato  (codigo_pessoa, nome, email, telefone) VALUES (1, 'Marcos Henrique', 'marcos@algamoney.com', '00 0000-0000')