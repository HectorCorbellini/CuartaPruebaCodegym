# Sistema de Gestión de Datos de Ciudades

Una robusta aplicación Java para gestionar datos de ciudades utilizando Hibernate ORM y Redis, siguiendo los principios SOLID y prácticas de código limpio. La aplicación proporciona una interfaz de consola interactiva para explorar y analizar datos de ciudades.

## Características

- Interfaz interactiva basada en menú con indicadores claros de caché Redis
- Opciones de recuperación de datos:
  - Datos de ciudades paginados con caché Redis para un rendimiento óptimo
  - Filtrado basado en población con acceso directo a la base de datos
  - Categorización de ciudades por tamaño de población
  - Comparación de rendimiento entre caché Redis y acceso directo a la base de datos
- Categorización automática de ciudades:
  - Metrópolis (población de 1,000,000+)
  - Grande (población de 500,000 - 999,999)
  - Mediana (población de 100,000 - 499,999)
  - Pequeña (población < 100,000)
- Implementación de caché Redis:
  - Caché automática de resultados paginados
  - Expiración de caché después de 5 minutos
  - Comparación de métricas de rendimiento

## Arquitectura

La aplicación sigue una arquitectura en capas con separación clara de responsabilidades:

### Capa de Dominio
- `Ciudad`: Entidad que representa datos de ciudad
- `País`: Entidad que representa datos de país
- `Idioma del País`: Entidad que representa datos de idioma del país

### Capa de Acceso a Datos
- `ICiudadRepositorio`: Interfaz que define operaciones de acceso a datos
- `CiudadDAO`: Implementación de operaciones de acceso a datos utilizando Hibernate

### Capa de Servicio
- `ICiudadServicio`: Interfaz que define operaciones de negocio
- `CiudadServicio`: Implementación de lógica de negocio y gestión de transacciones

### Objetos de Transferencia de Datos
- `CiudadDTO`: Maneja la transformación de datos y categorización con categorías de población

## Dependencias

- Java 17 o superior
- Maven 3.6 o superior
- Docker y Docker Compose
- MySQL 8.0 (se ejecuta en Docker)
- Redis 7.0 (se ejecuta en Docker)
- Hibernate 5.6.14.Final
- SLF4J y Logback para registro

## Configuración de Docker

La aplicación utiliza Docker para ejecutar sus dependencias:

```bash
# Iniciar contenedor Redis
docker run -d --name hibernate_redis -p 6379:6379 redis:7.0

# Iniciar contenedor MySQL (si no está ejecutándose)
docker run -d --name hibernate_mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=mundo \
  -e MYSQL_USER=hibernate_user \
  -e MYSQL_PASSWORD=hibernate_password \
  -p 3306:3306 \
  mysql:8.0
```

## Configuración

### Variables de Entorno

La aplicación utiliza variables de entorno para configuración:

```bash
MYSQL_URL=jdbc:mysql://localhost:3306/mundo
MYSQL_USER=hibernate_user
MYSQL_PASSWORD=hibernate_password
REDIS_URL=redis://localhost:6379/0
```

### Base de Datos

La aplicación espera una base de datos MySQL llamada 'mundo' con el siguiente esquema:

- Tabla Ciudad con campos:
  - ID
  - Nombre
  - Distrito
  - Población
  - País (clave foránea)

- Tabla País con campos:
  - Código
  - Nombre
  - Continente
  - Región
  - Población
  - Capital (clave foránea a Ciudad)

- Tabla Idioma del País con campos:
  - Código del País (clave foránea a País)
  - Idioma
  - Es Oficial
  - Porcentaje

## Compilación y Ejecución

1. Clonar el repositorio
2. Iniciar contenedores Docker (ver sección de Configuración de Docker)
3. Establecer variables de entorno
4. Compilar el proyecto:
   ```bash
   mvn clean compile
   ```
5. Ejecutar la aplicación:
   ```bash
   mvn exec:java -Dexec.mainClass="com.javarush.Main"
   ```

## Organización del Código

La base de código sigue los principios SOLID y prácticas de arquitectura limpia:

1. **Principio de Responsabilidad Única**: Cada clase tiene un propósito único y bien definido
   - `CiudadDAO`: Maneja operaciones de acceso a datos
   - `CiudadServicio`: Maneja lógica de negocio y transacciones
   - `CiudadDTO`: Maneja transformación de datos y presentación

2. **Principio de Segregación de Interfaces**: Interfaces limpias para diferentes responsabilidades
   - `ICiudadRepositorio`: Contrato de acceso a datos
   - `ICiudadServicio`: Contrato de operaciones de negocio

3. **Principio de Inversión de Dependencias**: Módulos de alto nivel dependen de abstracciones
   - Servicios dependen de interfaces de repositorio
   - Clase principal depende de interfaces de servicio

4. **Principio de Abierto/Cerrado**: Diseño extensible
   - Nuevas categorías de ciudad se pueden agregar sin modificar código existente
   - Nuevas operaciones de datos se pueden agregar extendiendo interfaces

## Uso

Después de iniciar la aplicación, se presentará una interfaz de menú interactiva:

1. Ver Ciudades (Paginadas) - Utiliza caché Redis
   - Ingrese desplazamiento y límite para ver un rango específico de ciudades
   - Los resultados se almacenan en caché en Redis durante 5 minutos
   - Consultas idénticas posteriores se sirven desde la caché
2. Encontrar Ciudades por Rango de Población
   - Ingrese población mínima y máxima para filtrar ciudades
   - Acceso directo a la base de datos sin caché
3. Ver Ciudades por Categoría
   - Ver ciudades agrupadas por tamaño de población
   - Acceso directo a la base de datos sin caché
4. Comparar Rendimiento de Caché vs Base de Datos
   - Comparar tiempos de respuesta entre caché Redis y acceso directo a la base de datos
   - Ver número de ciudades recuperadas en cada método
   - Ver diferencia de tiempo y factor de velocidad entre métodos
   - Entender cuándo usar caché para un rendimiento óptimo

La aplicación maneja conflictos de puertos automáticamente y garantiza un cierre limpio de recursos.

### Comparación de Rendimiento

La aplicación incluye una característica de comparación de rendimiento que permite:
- Comparar tiempos de respuesta entre caché Redis y acceso directo a la base de datos
- Ver número de ciudades recuperadas en cada método
- Ver diferencia de tiempo y factor de velocidad entre métodos
- Entender cuándo usar caché para un rendimiento óptimo

2. **Principio de Abierto/Cerrado**: Componentes están abiertos para extensión pero cerrados para modificación
   - Interfaces definen contratos
   - Nuevas implementaciones se pueden agregar sin cambiar código existente

3. **Principio de Sustitución de Liskov**: Implementaciones cumplen adecuadamente con sus contratos de interfaz
   - `CiudadDAO` implementa `ICiudadRepositorio`
   - `CiudadServicio` implementa `ICiudadServicio`

4. **Principio de Segregación de Interfaces**: Interfaces enfocadas para propósitos específicos
   - `ICiudadRepositorio` para acceso a datos
   - `ICiudadServicio` para operaciones de negocio

5. **Principio de Inversión de Dependencias**: Módulos de alto nivel dependen de abstracciones
   - Dependencias se inyectan a través de constructores
   - Componentes de tiempo de ejecución se configuran en la clase `Main`

## Categorías de Ciudades

Las ciudades se categorizan según su población:
- Pequeña: < 100,000
- Mediana: 100,000 - 500,000
- Grande: 500,000 - 1,000,000
- Metrópolis: > 1,000,000

## Manejo de Errores

- Validación exhaustiva para parámetros de entrada
- Manejo de errores con mensajes de error adecuados
- Gestión de transacciones para operaciones de base de datos

## Registro

- Configuración de Logback para registro estructurado
- Diferentes niveles de registro para componentes variados
- Apéndices de consola y archivo disponibles

## Contribución

1. Clonar el repositorio
2. Crear una rama de características
3. Confirmar cambios
4. Empujar a la rama
5. Crear una Solicitud de Extracción

## Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo LICENSE para detalles
