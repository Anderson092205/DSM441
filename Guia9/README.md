# Guía 09 - SQLiteApp

Aplicación Android en Kotlin con vistas XML, SDK mínimo 24, `SQLiteOpenHelper` y base local `tienda.sqlite`.

## Abrir y ejecutar

1. Abrir la carpeta `Guia9` en Android Studio.
2. Sincronizar Gradle y ejecutar el módulo `app` en un dispositivo API 24 o superior.
3. En la primera pantalla, pulsar **No tengo cuenta: registrarme**.
4. Registrar un nick y una contraseña de al menos 6 caracteres. Volver al login e ingresar.
5. Administrar los productos con los botones **Agregar**, **Actualizar**, **Eliminar** y **Buscar**.

La aplicación funciona sin Internet. Las cuentas y los productos se guardan en SQLite y se conservan al cerrar la app. Después de cerrar su proceso se solicita de nuevo el login.

## Correspondencia con la práctica

| Parte de la guía | Implementación |
| --- | --- |
| Proyecto Empty Views Activity | `SQLiteApp`, paquete `com.example.sqliteapp`, Kotlin y API mínima 24 |
| Paquetes db y model | `db/HelperDB.kt`, `model/Categoria.kt`, `model/Productos.kt` |
| Categorías | Las 10 categorías indicadas en la guía, cargadas una sola vez y ordenadas en el Spinner |
| Tabla productos | idproductos, idcategoria, descripcion, precio y cantidad, con clave foránea |
| Formulario | Código, producto, precio, cantidad inicial y categoría; los cuatro botones en el orden indicado |
| 10 registros de práctica | Ingresados a través de la app durante la prueba completa; incluidos en la copia exportada |
| Exportación | `output/base_de_datos/tienda.sqlite`, copiada de la base real del emulador |
| Buscar | Consulta por código, llena los campos y selecciona la categoría correspondiente |
| Actualizar y eliminar | Verifican cuántas filas se afectaron y notifican cuando el código no existe |
| Login como primera pantalla | `LoginActivity`, configurada como LAUNCHER en AndroidManifest.xml |
| Registro | `RegistroActivity`, tabla `Usuario` con nick único y contraseña |

La primera instalación no crea usuarios ni productos automáticamente: se registran desde las pantallas, como en la guía. Las categorías sí se cargan una sola vez al entrar al formulario.

## Uso del CRUD

- **Agregar:** llenar producto, precio, cantidad y categoría. El código se genera automáticamente y se muestra en el formulario.
- **Buscar:** escribir un código existente y pulsar Buscar. No es necesario llenar los demás campos.
- **Actualizar:** escribir o buscar el código, editar los datos y pulsar Actualizar.
- **Eliminar:** escribir o buscar el código y confirmar la eliminación.
- **Cerrar sesión:** regresar al login.

Los códigos deben ser enteros positivos. El precio admite hasta dos decimales y la cantidad debe ser un entero no negativo. Los mensajes de éxito de actualización y eliminación solo se muestran cuando el registro realmente existe. Un código inexistente no cierra la app.

## Base exportada y DB Browser for SQLite

La copia está en `output/base_de_datos/tienda.sqlite`. Abrirla con **Open Database** en DB Browser for SQLite y consultar **Database Structure** y **Browse Data**.

Para exportar otra copia desde Android Studio, ir a **View > Tool Windows > Device Explorer**, abrir `/data/data/com.example.sqliteapp/databases/tienda.sqlite` y usar **Save As**. Es una copia: los cambios realizados en ella no alteran la base del dispositivo.

La base de ejemplo corresponde exclusivamente al emulador de pruebas. Cuenta de prueba de esa copia: nick `estudiante`, contraseña `Guia09#2026`. En una instalación nueva debe registrarse una cuenta propia.

Las contraseñas se almacenan como hashes PBKDF2 con sal; la columna `contrasena` no guarda el texto original. La versión 2 de la base añade la tabla Usuario sin borrar los productos de la versión 1.

## Verificación

- `gradlew.bat :app:assembleDebug`: genera `app/build/outputs/apk/debug/app-debug.apk`.
- `gradlew.bat :app:testDebugUnitTest`: valida precios, cantidades, códigos y contraseñas.
- `BaseDatosTest`: verifica categorías sin duplicados, CRUD, persistencia, claves foráneas, login, nicks duplicados y migración.
- `FlujoGuiaTest`: recorre registro, login, ingreso de 10 productos, búsqueda, actualización, eliminación y cierre de sesión. Usa únicamente datos del emulador.
- `gradlew.bat :app:lintDebug`: revisión estática.

Las evidencias y el resultado de la base exportada se guardan en `output/` después de ejecutar las pruebas.

### Resultado de la entrega

Compilación y revisión estática finalizadas sin errores. Se aprobaron 3 pruebas unitarias, 5 pruebas SQLite en Android y 1 prueba completa de pantallas. La copia exportada contiene 10 categorías y 10 productos; la integridad y las claves foráneas se verificaron correctamente.

Ver `output/verificacion.md` para consultar los productos registrados y las evidencias del login y del formulario.
