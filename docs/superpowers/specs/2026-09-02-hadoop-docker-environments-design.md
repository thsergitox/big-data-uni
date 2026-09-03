# Diseño de entornos Hadoop 2 y Hadoop 3 con Docker

## Objetivo

Proporcionar dos entornos Hadoop de un solo nodo, reproducibles e independientes,
para ejecutar prácticas de HDFS, YARN y MapReduce en Arch Linux sin instalar Java
ni Hadoop en el sistema anfitrión.

## Alcance

- Hadoop 2 usa la imagen fija `apache/hadoop:2.10.2`.
- Hadoop 3 usa la imagen fija `apache/hadoop:3.5.0`.
- Cada entorno contiene NameNode, DataNode, ResourceManager, NodeManager y
  JobHistory Server.
- HDFS conserva sus metadatos y bloques mediante volúmenes nombrados.
- Cada entorno incluye una prueba de humo que ejecuta MapReduce `wordcount`.
- Solo se ejecuta una versión a la vez para conservar los puertos estándar.
- No se añade Apache Hue: las interfaces web nativas cubren el alcance del curso.

## Fuera de alcance

- Clúster multinodo de producción.
- Alta disponibilidad, Kerberos, TLS o autorización multiusuario.
- Instalación nativa de Hadoop o Java.
- Compatibilidad exacta con Hadoop 2.8.0 y `winutils.exe`.
- Ejecución simultánea de Hadoop 2 y Hadoop 3.

## Arquitectura

Cada versión vive en su propio directorio y su propio proyecto de Docker Compose.
Los contenedores comparten una red privada y archivos XML de configuración
específicos de la versión. El NameNode y el DataNode usan volúmenes nombrados; los
demás servicios son reemplazables y no conservan estado imprescindible.

Los servicios se ejecutan como procesos Hadoop directos, no mediante SSH ni
`start-all.sh`. Esto permite que Docker supervise un proceso principal por
contenedor y muestre sus logs mediante `docker compose logs`.

## Componentes

### NameNode

- Expone el RPC de HDFS dentro de la red Compose.
- Publica la interfaz web en `9870` para Hadoop 3 y `50070` para Hadoop 2.
- Usa un volumen persistente para los metadatos.
- Se formatea una sola vez de manera idempotente antes del primer arranque.

### DataNode

- Se conecta al NameNode mediante el nombre DNS del servicio.
- Usa un volumen persistente independiente para los bloques.
- Informa su estado al NameNode.

### ResourceManager y NodeManager

- Ejecutan trabajos MapReduce sobre YARN.
- ResourceManager publica su interfaz web en `8088`.
- NodeManager publica su interfaz web en `8042`.

### JobHistory Server

- Publica el historial MapReduce en `19888`.
- Depende de que HDFS esté disponible.

## Configuración

Cada entorno mantiene sus propios archivos:

- `core-site.xml`: dirección `fs.defaultFS` del NameNode.
- `hdfs-site.xml`: replicación `1` y rutas persistentes.
- `mapred-site.xml`: MapReduce sobre YARN y JobHistory Server.
- `yarn-site.xml`: servicio de shuffle y variables permitidas.

La configuración se monta en el directorio indicado por `HADOOP_CONF_DIR`. No se
usan propiedades ni rutas específicas de Windows.

## Operación

Cada directorio incluye comandos pequeños con una sola responsabilidad:

- `start.sh`: levanta la versión correspondiente.
- `stop.sh`: detiene esa versión conservando los volúmenes.
- `reset.sh`: destruye contenedores y datos después de una confirmación explícita.
- `smoke-test.sh`: espera a HDFS/YARN y ejecuta `wordcount` con datos aislados.

El usuario también puede operar directamente con `docker compose`.

## Interfaces web

| Servicio | Hadoop 2 | Hadoop 3 |
| --- | ---: | ---: |
| NameNode | `http://localhost:50070` | `http://localhost:9870` |
| ResourceManager | `http://localhost:8088` | `http://localhost:8088` |
| NodeManager | `http://localhost:8042` | `http://localhost:8042` |
| JobHistory | `http://localhost:19888` | `http://localhost:19888` |

Estas interfaces son consolas de administración y observabilidad, no aplicaciones
gráficas de escritorio.

## Manejo de errores

- Los servicios incluyen comprobaciones de salud antes de iniciar dependientes.
- El formateo del NameNode detecta si ya existe metadata para no destruir datos.
- La prueba de humo elimina únicamente sus rutas HDFS de entrada y salida.
- Los scripts terminan al primer error y muestran comandos fallidos.
- `reset.sh` exige una confirmación para evitar pérdida accidental de datos.

## Estrategia de pruebas

1. Pruebas estáticas verifican versiones fijadas, servicios, puertos, volúmenes y
   propiedades XML requeridas.
2. `docker compose config` valida ambos modelos Compose sin descargar ni construir
   imágenes.
3. Validaciones de shell comprueban la sintaxis de cada script.
4. La prueba de humo de ejecución confirma que HDFS acepta archivos, YARN ejecuta
   MapReduce y `wordcount` produce el resultado esperado.

Por la regla del proyecto, la implementación no ejecutará `docker build` ni
`docker compose build`.

## Estructura prevista

```text
bigdata/
├── README.md
├── tests/
│   └── test-hadoop-environments.sh
├── hadoop2/
│   ├── compose.yaml
│   ├── config/
│   │   ├── core-site.xml
│   │   ├── hdfs-site.xml
│   │   ├── mapred-site.xml
│   │   └── yarn-site.xml
│   └── scripts/
│       ├── start.sh
│       ├── stop.sh
│       ├── reset.sh
│       └── smoke-test.sh
└── hadoop3/
    └── ... misma interfaz pública, con configuración específica de Hadoop 3
```

