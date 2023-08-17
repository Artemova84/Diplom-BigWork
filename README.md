Для тестирования приложения покупки тура "Путешествие дня - Марракэш" были написаны автотесты, проверяющие позитивные и негативные сценарии покупки тура как обычной дебетовой картой, так и в кредит. 
# Запуск SUT и автотестов 
## Подключение SUT к MySQL
1.	Открыть проект в IntelliJ IDEA;
2.	Запустить Docker Desktop;
3.	Запустить DBeaver;
4.	В терминале в корне проекта запустить контейнер: `docker-compose up –build`;
5.	Запустить сервис с указанием пути к базе данных для mysql:
`java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar`;
6.	Запустить jar-файл: java `-jar artifacts/aqa-shop.jar`;
7.	Настроить соединения с базой данных в DBeaver;
8.	Запустить тесты:
`.\gradlew clean test -DdbUrl=jdbc:mysql://localhost:3306/app`;
9.	Открыть отчёт Gradle в браузере Google Chrome;
10.	Остановить приложение;
11.	Остановить контейнер: docker-compose down.
## Подключение SUT к PostgreSQL
1.	Открыть проект в IntelliJ IDEA;
2.	Запустить Docker Desktop;
3.	Запустить DBeaver;
4.	В терминале в корне проекта запустить контейнер: `docker-compose up –build`;
5.	Запустить сервис с указанием пути к базе данных для postgresql:
`java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar`;
6.	Запустить jar-файл: `java -jar artifacts/aqa-shop.jar`;
7.	Настроить соединения с базой данных в DBeaver;
8.	Запустить тесты:
`.\gradlew clean test -DdbUrl=jdbc:postgresql://localhost:5432/app`;
9.	Открыть отчёт Gradle в браузере Google Chrome;
10.	Остановить приложение;
11.	Остановить контейнер: docker-compose down.
