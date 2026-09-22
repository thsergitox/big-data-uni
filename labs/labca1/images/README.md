# Evidencias de ejecución de LABCA1

Las capturas se tomaron el 22 de septiembre de 2026 sobre el clúster Hadoop
local de un solo nodo. Se usó el mismo entorno que ejecutó las catorce
consultas.

| Archivo | Servicio o medición | Evidencia visible |
| --- | --- | --- |
| `namenode-overview.png` | NameNode, puerto 9870 | NameNode activo, versión 3.5.0, uso de HDFS y estado del sistema de archivos. |
| `resourcemanager-applications.png` | ResourceManager, puerto 8088 | 33 aplicaciones completadas y trabajos reales de `query09` a `query14`. |
| `nodemanager-resources.png` | NodeManager, puerto 8042 | Nodo saludable, 2 GB de memoria física y 8 vcores configurados. |
| `jobhistory-query.png` | JobHistory, puerto 19888 | Job `job_1790102054372_0033` de `query14`, estado `SUCCEEDED`, dos mapas y un reducer. |
| `docker-stats.png` | Docker CLI | Uso real de CPU, memoria y E/S durante la ejecución de `query13`. |

La medición de Docker se tomó a las **2026-09-22 14:41:56 -05:00**, mientras
`query13` ejecutaba la regresión lineal. El archivo `docker-stats.txt`
conserva la salida original usada para producir la imagen. El valor de CPU del
NodeManager supera 100 % porque Docker expresa el consumo con relación a un
solo núcleo y el contenedor usó varios núcleos al mismo tiempo.
