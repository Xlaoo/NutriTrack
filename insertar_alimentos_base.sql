-- ========================================================
-- Script para poblar catálogo de alimentos en NutriTrack
-- Valores aproximados por cada 100g o 100ml
-- No destructivo: Evita duplicados si ya existen por nombre
-- ========================================================

USE nutritrack;

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Manzana', 52.00, 0.30, 14.00, 0.20, 'Frutas', 'Manzana fresca rica en fibra y agua', 10.40, 1.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Manzana');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Plátano', 89.00, 1.10, 22.80, 0.30, 'Frutas', 'Plátano maduro fuente natural de potasio', 12.20, 1.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Plátano');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Arroz blanco cocido', 130.00, 2.70, 28.20, 0.30, 'Cereales', 'Arroz blanco cocido básico en la dieta', 0.10, 1.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Arroz blanco cocido');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Pechuga de pollo', 165.00, 31.00, 0.00, 3.60, 'Carnes', 'Pechuga de pollo magra cocida a la plancha', 0.00, 74.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Pechuga de pollo');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Huevo', 155.00, 13.00, 1.10, 11.00, 'Proteínas', 'Huevo entero de gallina cocido', 1.10, 124.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Huevo');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Leche entera', 61.00, 3.20, 4.80, 3.30, 'Lácteos', 'Leche entera pasteurizada líquida', 5.10, 43.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Leche entera');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Avena', 389.00, 16.90, 66.30, 6.90, 'Cereales', 'Avena en hojuelas integral rica en fibra', 0.00, 2.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Avena');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Papa sancochada', 87.00, 1.90, 20.10, 0.10, 'Tubérculos', 'Papa cocida o hervida sin piel', 0.90, 6.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Papa sancochada');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Pan integral', 247.00, 13.00, 41.30, 3.40, 'Panadería', 'Pan elaborado con harina de trigo integral', 4.30, 400.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Pan integral');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Yogur natural', 61.00, 3.50, 4.70, 3.30, 'Lácteos', 'Yogur natural sin azúcar añadida', 4.70, 46.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Yogur natural');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Pescado', 105.00, 22.00, 0.00, 1.20, 'Pescados', 'Filete de pescado blanco cocido', 0.00, 68.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Pescado');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Zanahoria', 41.00, 0.90, 9.60, 0.20, 'Verduras', 'Zanahoria fresca fuente de betacarotenos', 4.70, 69.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Zanahoria');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Brócoli', 34.00, 2.80, 6.60, 0.40, 'Verduras', 'Brócoli fresco rico en antioxidantes y vitamina C', 1.70, 33.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Brócoli');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Naranja', 47.00, 0.90, 11.80, 0.10, 'Frutas', 'Naranja fresca rica en vitamina C', 9.40, 0.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Naranja');

INSERT INTO alimentos (nombre, calorias, proteinas, carbohidratos, grasas, categoria, descripcion, azucares, sodio)
SELECT 'Lentejas cocidas', 116.00, 9.00, 20.10, 0.40, 'Legumbres', 'Lentejas cocidas ricas en hierro y proteína vegetal', 1.80, 2.00
WHERE NOT EXISTS (SELECT 1 FROM alimentos WHERE nombre = 'Lentejas cocidas');
