# product-service

Microservicio para la gestión del catálogo de productos, construido con **Spring Boot 4**, **Java 17**,
**arquitectura hexagonal** (puertos y adaptadores) y enfoque **contract-first** con OpenAPI.

## Requisitos

| Herramienta | Versión |
|-------------|---------|
| JDK         | 17 o superior |
| Maven       | Incluido (`mvnw`) |
| IDE         | Con soporte de Lombok (IntelliJ y la extensión *Language Support for Java* de VS Code ya lo incluyen) |

## Ejecución

```bash
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

| Recurso       | URL |
|---------------|-----|
| API           | http://localhost:8088/api/product |
| Swagger UI    | http://localhost:8088/swagger-ui.html |
| Contrato YAML | http://localhost:8088/api.yml |

La colección de Postman está en [postman/Product-Service.postman_collection.json](postman/Product-Service.postman_collection.json).
Ejecútala en orden con la aplicación recién arrancada (el repositorio es en memoria).

## Endpoints

| Método | Ruta                 | Respuestas |
|--------|----------------------|------------|
| GET    | `/api/product`       | 200 lista, 204 sin productos |
| POST   | `/api/product`       | 200 producto creado, 400 datos inválidos |
| GET    | `/api/product/{id}`  | 200 producto, 204 no existe |

Cada producto se identifica por su **SKU**, expuesto en el campo `_id` y generado de forma correlativa al registrarlo
(`SKU-000001`, `SKU-000002`, ...). Es el valor que se usa en `GET /api/product/{id}`.

Reglas de validación al registrar: `nombre` y `categoria` obligatorios, `precio` obligatorio y mayor o igual a 0,
`stock` opcional y no negativo. `nombre`, `categoria` y `descripcion` se normalizan (trim).

## Arquitectura hexagonal

```
com.nttdata.product
├── domain                          ← Núcleo. Java puro, sin Spring.
│   ├── model/Product               Entidad de dominio inmutable (Lombok @Value @Builder)
│   ├── exception/                  Excepciones de negocio
│   └── port
│       ├── in/                     Puertos de entrada: *UseCase + comandos (*Command, record)
│       └── out/                    Puertos de salida: *Port
├── application                     ← Casos de uso. Java puro, sin Spring.
│   └── service/ProductApplicationService
└── infrastructure                  ← Todo lo que depende de frameworks
    ├── adapter
    │   ├── in/rest                 Adaptador REST (implementa el delegate generado de api.yml)
    │   │   ├── api/ , dto/         Código GENERADO por openapi-generator (no se edita)
    │   │   ├── mapper/             DTO ⇄ dominio
    │   │   └── exception/          Traducción de excepciones a ErrorResponse
    │   └── out/persistence         Adaptador de persistencia en memoria (+ entity y mapper)
    └── config/BeanConfiguration    Ensambla los casos de uso como beans de Spring
```

Flujo de una petición:

```
HTTP → ApiApiController (generado) → ProductRestAdapter → *UseCase (puerto in)
     → ProductApplicationService → ProductRepositoryPort (puerto out) → InMemoryProductRepositoryAdapter
```

### Reglas de dependencia

1. Las dependencias apuntan **hacia dentro**: `infrastructure → application → domain`. Nunca al revés.
2. `domain` y `application` **no** importan Spring ni clases de `infrastructure`.
3. Los adaptadores no se conocen entre sí (REST no usa persistencia y viceversa); se comunican a través de puertos.
4. Los DTO generados del contrato (`ProductoRequest`, `ProductoResponse`, `ErrorResponse`) no salen del adaptador REST.
5. La entidad de persistencia (`ProductEntity`) no sale del adaptador de persistencia.

Estas reglas se **verifican automáticamente** en [HexagonalArchitectureTest](src/test/java/com/nttdata/product/HexagonalArchitectureTest.java)
con ArchUnit: si se rompen, el build falla.

### Cómo añadir una funcionalidad nueva

1. Definir el endpoint en [api.yml](src/main/resources/api.yml) y compilar (`./mvnw compile`) para regenerar el delegate.
2. Crear el puerto de entrada `XxxUseCase` (y su `XxxCommand` si recibe datos) en `domain/port/in`.
3. Si se necesita un nuevo acceso a datos, ampliar `ProductRepositoryPort` e implementarlo en el adaptador.
4. Implementar el caso de uso en `application/service`.
5. Implementar el método del delegate en `ProductRestAdapter` usando el mapper.
6. Añadir tests en cada capa (ver siguiente sección).

## Convenciones de código

| Elemento                    | Convención                             | Ejemplo |
|-----------------------------|----------------------------------------|---------|
| Puerto de entrada           | `<Acción><Entidad>UseCase`             | `RegisterProductUseCase` |
| Comando de entrada          | `<Acción><Entidad>Command`             | `RegisterProductCommand` |
| Puerto de salida            | `<Entidad><Recurso>Port`               | `ProductRepositoryPort` |
| Adaptador                   | `<Tecnología><Entidad>...Adapter`      | `InMemoryProductRepositoryAdapter` |
| Mapper                      | `<Entidad><Capa>Mapper`                | `ProductRestMapper` |
| Tests                       | `<ClaseProbada>Test`, métodos en español describiendo el comportamiento | `rechazaDatosInvalidos` |

Uso de **Lombok**:

- Modelo de dominio: `@Value` + `@Builder` (inmutable, igualdad por SKU).
- Comandos de entrada: `record` de Java (inmutables, igualdad por todos los campos).
- Entidades de persistencia: `@Getter` + `@Builder`.
- Inyección de dependencias: `@RequiredArgsConstructor` con campos `private final` (sin `@Autowired`).
- Logs: `@Slf4j`.
- [lombok.config](lombok.config) marca el código generado con `@Generated`, por lo que JaCoCo no lo cuenta en la cobertura.

## Tests y cobertura

```bash
./mvnw verify
```

Ejecuta todos los tests, genera el informe de cobertura y **falla si la cobertura de líneas o ramas baja del 90 %**
(propiedad `jacoco.minimum.coverage` del `pom.xml`).

Informe HTML: `target/site/jacoco/index.html`

| Tipo de test           | Clase | Qué valida |
|------------------------|-------|------------|
| Unitario dominio       | `ProductTest` | Igualdad por SKU, builder |
| Unitario aplicación    | `ProductApplicationServiceTest` | Reglas de negocio y validaciones (Mockito + `Clock` fijo) |
| Unitario adaptadores   | `ProductRestAdapterTest`, `ProductRestMapperTest`, `ProductExceptionHandlerTest`, `InMemoryProductRepositoryAdapterTest` | Traducciones y códigos HTTP |
| Integración            | `ProductApiIntegrationTest` | Contrato HTTP de extremo a extremo con MockMvc |
| Arquitectura           | `HexagonalArchitectureTest` | Reglas de dependencia hexagonales |

Se excluyen de la cobertura únicamente el código generado por openapi-generator y la clase `main` de Spring Boot.

## Checklist de entrega

- [ ] `./mvnw verify` termina en `BUILD SUCCESS` (tests, arquitectura y cobertura ≥ 90 %).
- [ ] La colección de Postman pasa completa contra la aplicación recién arrancada.
- [ ] Cualquier cambio de API se hizo primero en `api.yml`.
- [ ] No hay lógica de negocio en controladores, adaptadores ni mappers.
- [ ] `domain` y `application` no importan Spring.
- [ ] Sin código comentado, `System.out` ni imports sin usar.
- [ ] No se versionan `target/`, ficheros del IDE ni credenciales.
