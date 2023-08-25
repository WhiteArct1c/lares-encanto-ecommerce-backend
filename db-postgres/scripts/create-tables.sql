-- Criação da tabela 'users'
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- Criação da tabela 'address'
CREATE TABLE address (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100),
    cep VARCHAR(10),
    residenceType VARCHAR(50),
    addressType VARCHAR(50),
    streetName VARCHAR(100),
    addressNumber VARCHAR(20),
    neighborhoods VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(50),
    country VARCHAR(50),
    observations TEXT
);

-- Criação da tabela 'gender'
CREATE TABLE gender (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Criação da tabela 'customer'
CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    fullName VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    birthDate DATE,
    cellphone VARCHAR(20),
    userId INT REFERENCES users(id)
);

-- Tabela de relacionamento entre 'customer' e 'address'
CREATE TABLE customer_address (
    customerId INT REFERENCES customer(id),
    addressId INT REFERENCES address(id),
    PRIMARY KEY (customerId, addressId)
);

