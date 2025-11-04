# Logging Starter

Стартер для централизованного логирования HTTP-запросов, ошибок, метрик и кэширования данных из БД.

---

## Основные возможности

1. **Логирование входящих HTTP-запросов** (`@HttpIncomeRequestLog`)
   Автоматическое логирование всех входящих HTTP-запросов с деталями: URL, метод, параметры, тело запроса.

2. **Логирование исходящих HTTP-запросов** (`@HttpOutcomeRequestLog`)
   Логирование запросов, отправляемых вашим сервисом другим сервисам, включая статус ответа и тело.

3. **Логирование ошибок** (`@LogDatasourceError`)
   Логирование исключений.
   Можно указать уровень логирования через параметр аннотации (`Level.ERROR`, `Level.WARNING`, `Level.INFO`).
   Все ошибки могут отправляться в Kafka и/или сохраняться в базу данных `error_log`.

4. **Метрики методов** (`@Metric`)
   Измеряет время работы метода.
   Если время работы метода превышает лимит, установленный в конфиге, отправляется событие в Kafka (`service_logs`) с информацией о сигнатуре метода, типом — WARNING, временем работы и входными параметрами.

5. **Кэширование запросов к БД** (`@Cached`)

   * Проверяет кэш перед выполнением запроса в БД.
   * Ключ кэша — primary key, если хранилище индивидуально под сущность, или hashCode объекта для общего кэша.
   * Время хранения в кэше задаётся в `application.yml`.
   * После истечения времени запись удаляется из кэша.

6. **Kafka Integration**
   Все логи и метрики могут отправляться в Kafka для централизованного анализа.

7. **Хранение ошибок в базе данных**
   Все ошибки сохраняются в таблице `error_log`.

---

## Конфигурационные проперти

### Кэш

```
cache:
  ttl-seconds: 60 # TTL по умолчанию в секундах
```

### Ошибки

```
logging:
  error:
    enabled: true       # Включить/выключить логирование ошибок
    send-to-kafka: true # Отправлять ли ошибки в Kafka
    save-to-database: true # Сохранять ли ошибки в БД
```

### HTTP входящие запросы

```
logging:
  http:
    income:
      enabled: true
      send-to-kafka: true
```

### HTTP исходящие запросы

```
logging:
  http:
    outcome:
      enabled: true
      send-to-kafka: true
```

### Метрики

```
logging:
  metric:
    enabled: true
    limit-ms: 500 # Лимит времени выполнения метода (мс)
```

---

## Зависимости проекта

Для работы стартера требуются следующие зависимости:

```
<!-- Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
