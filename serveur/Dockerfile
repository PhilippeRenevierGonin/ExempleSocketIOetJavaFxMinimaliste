FROM openjdk:17-jdk-slim
ARG JAR_FILE=/target/serveur-1.0-jar-with-dependencies.jar
COPY ${JAR_FILE} serveur.jar
ENTRYPOINT java -jar /serveur.jar "nolocal"