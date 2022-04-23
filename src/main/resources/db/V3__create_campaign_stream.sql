CREATE STREAM campaigns WITH (
    kafka_topic = 'duyo.duyo.campaigns',
    value_format = 'avro'
);