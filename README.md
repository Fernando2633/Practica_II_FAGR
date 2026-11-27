# Explorador de Libros (Práctica II)

App Android en **Java** que consulta el catálogo de la API REST de Biblioteca con **Retrofit**, guarda libros favoritos en **Room** para verlos sin internet y recuerda las preferencias del usuario con **SharedPreferences**.

- **Alumno:** Fernando Antonio García Ruiz
- **Unidad de aprendizaje:** Desarrollo de aplicaciones móviles nativas
- **API:** https://ejercicio-08-documentacion-y-endpoints.onrender.com/swagger-ui/index.html

## Pantallas

1. **Bienvenida y configuración:** nombre o alias, tema claro / oscuro / sistema, última sección visitada y preferencias guardadas.
2. **Catálogo:** libros de `GET /api/v1/libros` con búsqueda, filtro de disponibles y botón para actualizar.
3. **Detalle:** libro de `GET /api/v1/libros/{id}` con botón para agregar o quitar de favoritos.
4. **Favoritos:** libros guardados en Room; funciona en modo avión.

## Tecnologías

| Librería | Versión | Uso |
| --- | --- | --- |
| Retrofit + converter-gson | 2.9.0 | Consumo de la API REST |
| Gson | 2.10.1 | Mapeo del JSON a los DTOs |
| Room | 2.6.1 | Base de datos local de favoritos |
| SharedPreferences | Android | Nombre, tema y última sección |

## Estructura

```
app/src/main/java/com/example/practica_ii_fagr/
├── models/    Libro, LibrosResponse, ErrorResponse
├── api/       LibrosApi, RetrofitClient
├── db/        LibroEntity, LibroDao, AppDatabase, FavoritosRepository
├── adapters/  LibrosAdapter, FavoritosAdapter
├── ui/        BienvenidaActivity, CatalogoActivity, DetalleActivity, FavoritosActivity
├── utils/     Preferencias, Errores, Estilos, Pantalla
└── ExploradorApp.java
```

## Cómo correrlo

1. Abrir la carpeta del proyecto en Android Studio y esperar el Gradle Sync.
2. Elegir la configuración **app** y un emulador o celular con internet.
3. Run ▶ (Shift + F10).

> La API está en Render y se duerme cuando no se usa, así que la primera carga puede tardar hasta un minuto.
