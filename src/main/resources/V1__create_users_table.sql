CREATE TABLE USERS
(
    id   VARCHAR PRIMARY KEY,
    name VARCHAR
)
    WITH (KAFKA_TOPIC = 'duyo.duyo.users', VALUE_FORMAT = 'AVRO');
