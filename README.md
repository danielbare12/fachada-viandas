# Sosa, Ezequiel

Template para TP DDS 2024 - Entrega 2

Para ejecutar las pruebas:
- Ejecutamos la clase Webapp 
  por default levanta en el puerto 8080, esto puede cambiarse modificando la variable de entorno PORT
  por otro lado, trata de conecarse viandas proxy, por defecto tambien en la direccion http://localhost:8081/
  esto puede cambiarse modificando la variable de entorno URL_VIANDAS
```bash
# se puede correr así también
mvn clean compile exec:java -Dexec.mainClass="ar.edu.utn.dds.k3003.app.WebApp"
```
- Luego hay que ejecutar ViandaTestServer, por defecto levanta en el puerto 8081. 
  Este NO se puede correr por linea de comando
- Despues de esto, ya pueden usar cualquier cliente HTTP y probar los endpoins implementados por Webapp

```
# Completar con dirección donde despĺegaron en Render.com
https://{miproyecto}.onrender.com
```

