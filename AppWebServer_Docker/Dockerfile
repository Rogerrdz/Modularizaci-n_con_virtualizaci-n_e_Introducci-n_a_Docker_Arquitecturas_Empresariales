FROM eclipse-temurin:17-jre

WORKDIR /usrapp/bin

ENV PORT=6000

COPY target/classes /usrapp/bin/classes
COPY target/dependency /usrapp/bin/dependency

EXPOSE 6000

CMD ["java", "-cp", "/usrapp/bin/classes:/usrapp/bin/dependency/*", "edu.escuelaing.arep.MicroSpringBoot2"]
