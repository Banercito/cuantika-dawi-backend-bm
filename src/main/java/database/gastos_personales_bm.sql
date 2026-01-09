-- Asegúrate de que la base de datos existe
DROP DATABASE IF EXISTS gastos_personales_bm;
CREATE DATABASE gastos_personales_bm;
USE gastos_personales_bm;

-- Tabla de usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    rol ENUM('ADMINISTRADOR', 'USUARIO', 'AUDITORIA') NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla de categorías
CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    tipo ENUM('Ingreso', 'Gasto') NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla de cuentas
CREATE TABLE cuentas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    saldo DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tipo_cuenta VARCHAR(255),
    usuario_id INT,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla de transacciones
CREATE TABLE transacciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    monto DECIMAL(10, 2) NOT NULL,
    descripcion TEXT,
    fecha DATETIME NOT NULL,
    tipo ENUM('Ingreso', 'Gasto') NOT NULL,
    cuenta_id INT NOT NULL,
    categoria_id INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (cuenta_id) REFERENCES cuentas(id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Insertar usuarios iniciales (contraseñas encriptadas con BCrypt)
-- Las contraseñas son 'admin', 'user' y 'audit' respectivamente
INSERT INTO usuarios (nombre, apellido, username, contraseña, telefono, rol, activo)
VALUES
('Elon', 'Musk', 'admin', '$2a$10$Ep0CuKKowfPArxdYRv/bOOGNjduOvthuY4ylCcBvlP5hhK7sQKN1W', '999988777', 'ADMINISTRADOR', true),
('Baner', 'Murga Guimaray', 'baner', '$2a$10$aDRlHGMH5aLr4xgW8IW7UeVr544dT//HWSO0wGdAiDITtYhf/F7ga', '999777666', 'USUARIO', true),
('Marytere', 'Benavides Pezo', 'marytere', '$2a$10$E0NX18nAzlQ88gsegihQQ.pWvdEQC5plL6fEBSr0c8PnXlb7kyQnC', '998777666', 'USUARIO', true),
('Alan', 'Turing', 'audit', '$2a$10$9X5LZmRifF7AftbMq6JjoeuZJzFjeX5lY28Ar6vR.I8ogJrxqC0zi', '977666555', 'AUDITORIA', true);


-- Insertar algunas categorías iniciales
INSERT INTO categorias (nombre, descripcion, tipo, activo)
VALUES
('Salario', 'Ingresos por salario', 'Ingreso', true),
('Alquiler', 'Pago de alquiler', 'Gasto', true),
('Comida', 'Gastos en alimentación', 'Gasto', true),
('Transporte', 'Gastos en transporte', 'Gasto', true),
('Entretenimiento', 'Gastos en ocio', 'Gasto', true),
('Inversiones', 'Ingresos por inversiones', 'Ingreso', true);

-- Insertar cuentas iniciales
INSERT INTO cuentas (nombre, saldo, tipo_cuenta, usuario_id, activo)
VALUES
('Cuenta Principal', 1500.00, 'Corriente', 1, true),
('Ahorros', 5000.00, 'Ahorro', 1, true),
('Cuenta Personal', 800.00, 'Corriente', 2, true),
('Cuenta Auditoría', 2500.00, 'Corriente', 3, true);

-- Insertar algunas transacciones iniciales
INSERT INTO transacciones (monto, descripcion, fecha, tipo, cuenta_id, categoria_id, activo)
VALUES
(1000.00, 'Salario quincenal', '2025-04-15 00:00:00', 'Ingreso', 1, 1, true),
(500.00, 'Pago de alquiler', '2025-04-05 00:00:00', 'Gasto', 1, 2, true),
(150.00, 'Compra en supermercado', '2025-04-10 00:00:00', 'Gasto', 1, 3, true),
(50.00, 'Gasolina', '2025-04-12 00:00:00', 'Gasto', 1, 4, true),
(200.00, 'Inversión en acciones', '2025-04-01 00:00:00', 'Ingreso', 2, 6, true),
(75.00, 'Cine y cena', '2025-04-16 00:00:00', 'Gasto', 3, 5, true);