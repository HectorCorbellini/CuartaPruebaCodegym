# Resumen del Proyecto

Este proyecto es un sistema de gestión de datos de ciudades, diseñado para facilitar la interacción y análisis de datos relacionados con ciudades utilizando Java, Hibernate ORM y Redis. La aplicación sigue principios de diseño limpio y SOLID, asegurando un código mantenible y escalable.

## Valores de Configuración
Todos los valores de configuración están centralizados en el archivo `Constants.java` ubicado en:
```
final/src/main/java/com/codegym/util/Constants.java
```

Este archivo contiene todos los valores predeterminados para:
- Conexiones de base de datos (MySQL, Redis)
- Configuración de paginación
- Tiempos de caché
- Umbrales de población para categorías de ciudades
- Claves de variables de entorno

Si necesitas modificar alguno de estos valores, este es el único punto de referencia para la configuración de la aplicación.

## Ejecución de la Clase Principal

Para ejecutar la clase principal de la aplicación, utiliza el siguiente comando en la terminal:

```bash
mvn clean compile exec:java -Dexec.mainClass="com.javarush.Main"
```

Esto compilará el proyecto y ejecutará la aplicación, permitiéndote interactuar con el sistema de gestión de datos de ciudades.
