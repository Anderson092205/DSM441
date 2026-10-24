# Verificación de la guía 09

- Proyecto SQLiteApp compilado correctamente.
- Análisis estático: 0 errores.
- Pruebas unitarias: 3 aprobadas.
- Pruebas de base de datos en Android: 5 aprobadas.
- Prueba completa de pantallas en Android: 1 aprobada.
- Total: 9 pruebas aprobadas.
- Base exportada desde el emulador Android API 36.1: `base_de_datos/tienda.sqlite`.
- `PRAGMA integrity_check`: ok.
- `PRAGMA foreign_key_check`: sin infracciones.
- Versión de la base: 2.
- Registros: 10 categorías, 10 productos y 1 cuenta de prueba.

## Productos ingresados desde la aplicación

| Código | Producto | Precio | Cantidad | Categoría |
| --- | --- | --- | --- | --- |
| 1 | Arroz | 4.50 | 25 | Abarrotes |
| 2 | Frijoles | 2.50 | 10 | Abarrotes |
| 3 | Azúcar | 2.50 | 10 | Abarrotes |
| 4 | Sal | 2.50 | 10 | Abarrotes |
| 5 | Aceite | 2.50 | 10 | Abarrotes |
| 6 | Harina | 2.50 | 10 | Abarrotes |
| 7 | Café | 2.50 | 10 | Abarrotes |
| 8 | Pasta | 2.50 | 10 | Abarrotes |
| 9 | Avena | 2.50 | 10 | Abarrotes |
| 10 | Maíz | 2.50 | 10 | Abarrotes |

## Flujo comprobado

Se registró la cuenta de prueba, se inició sesión y se agregaron diez productos mediante el formulario. Se creó y eliminó un producto temporal para comprobar la confirmación y conservar los diez registros finales. Se buscó el producto 1 y se actualizó su precio a 4.50 y su cantidad a 25. Se comprobó la búsqueda de un código inexistente, el cierre de sesión y el rechazo de una contraseña incorrecta.

Las capturas están en `evidencias/01-login.png` y `evidencias/02-productos.png`. La base exportada es una copia independiente, lista para abrir con DB Browser for SQLite.
