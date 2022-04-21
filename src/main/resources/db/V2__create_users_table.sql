Update TABLE USERS (id VARCHAR PRIMARY KEY,name VARCHAR,age integer ) WITH (KAFKA_TOPIC = 'duyo.duyo.users', VALUE_FORMAT = 'AVRO');
