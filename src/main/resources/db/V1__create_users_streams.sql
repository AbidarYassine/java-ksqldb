CREATE STREAM users WITH (
    kafka_topic = 'duyo.duyo.users',
    value_format = 'avro'
);