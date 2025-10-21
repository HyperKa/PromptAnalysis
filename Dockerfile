FROM maven:3.8-openjdk-17 AS Builder
WORKDIR /app

# кэширование всех зависимостей, если они const, подкачки быть не должно
COPY pom.xml .
RUN mvn dependency:go-offline

# копирование исходного кода
COPY src ./src

RUN mvn clean package -DskipTests

# запуск образа
FROM openjdk:17-jdk-slim
WORKDIR /app
# копирование джарника из билдера
COPY --from=builder /app/target/*.jar app.jar

# запуск, тут ясно. А я хз, надо тесты же прикрутить, да?
ENTRYPOINT ["java", "-jar", "app.jar"]
