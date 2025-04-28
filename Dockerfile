FROM amazoncorretto:21-alpine3.21

ARG APPLICATION_YML
ARG APPLICATION_PROD_YML
WORKDIR /diglog

COPY . ./
RUN mkdir -p ./src/main/resources
RUN echo "${APPLICATION_YML}" > ./src/main/resources/application.yml
RUN echo "${APPLICATION_PROD_YML}" > ./src/main/resources/application-prod.yml

RUN chmod +x gradlew
RUN ./gradlew build -x test

ENTRYPOINT ["java", "-jar", "./build/libs/diglog-0.0.1-SNAPSHOT.jar"]

