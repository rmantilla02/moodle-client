# moodle-client
Proyecto para crear usuarios y asignarlos a cursos en la plataforma de Moodle

## Iniciar la aplicación
- Agregar las siguientes variables de entorno

      MOODLE_TOKEN
      JNEXT_CERT_GT_PASSWORD
      JNEXT_CERT_IS_PASSWORD

- Asegurarse de tener Java 17 + y Maven
- Ejecutar:

      mvn clean install

## Para generar el jar y ejecutarlo

      mvn clean package -DskipTests
      java -jar target/moodle-client-0.0.1-SNAPSHOT-jar-with-dependencies.jar

## Testing
  - Para invocar directamente a los servicios de Moodle usar api_moodle.postman_collection.json
  - Para correr los test con maven:

          mvn test
