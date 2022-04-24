## ******Streaming ETL demo - Enriching event stream data with CDC data from MySQL******

## ****Pre-reqs=****

- Docker

## **Pre-Flight Setup**

Start the environment=

`docker-compose -f docker-compose.yml up -d`

## ****Run ksqlDB CLI=****

`docker exec -it ksqldb ksql http=//0.0.0.0=8088`

<aside>
💡 **KSQL** is the streaming SQL engine that enables real-time-data-processing

</aside>

## ****Part 00 - ingesting state from a database as an event stream****

Launch the MySQL CLI=

`docker exec -it mysql2 bach -c 'mysql -u $MYSQL_USER -p$MYSQL_PASSWORD demo'`

In MySQL CLI=

`SHOW TABLES;`

result =

```jsx
+----------------+
| Tables_in_demo |
+----------------+
| CUSTOMERS |
+----------------+
    1
row in set(0.00
sec
)
```

```jsx
SELECT
ID, FIRST_NAME, LAST_NAME, EMAIL, CLUB_STATUS
FROM
CUSTOMERS
LIMIT
5;
```

## ****Part 01 - Ingest the data (plus any new changes) into Kafka =****

```jsx
CREATE
SOURCE
CONNECTOR
SOURCE_MYSQL_03
WITH(
    'connector.class' = 'io.debezium.connector.mysql.MySqlConnector',
    'database.hostname' = 'mysql',
    'database.port' = '3306',
    'database.user' = 'debezium',
    'database.password' = 'dbz',
    '[database.server.name](http=//database.server.name/)' = 'asgard',
    'table.whitelist' = 'demo.customers',
    'database.history.kafka.bootstrap.servers' = 'kafka=29092',
    'database.history.kafka.topic' = 'dbhistory.demo',
    'include.schema.changes' = 'false',
    'transforms' = 'unwrap,extractkey',
    'transforms.unwrap.type' = 'io.debezium.transforms.ExtractNewRecordState',
    'transforms.extractkey.type' = 'org.apache.kafka.connect.transforms.ExtractField$Key',
    'transforms.extractkey.field' = 'id',
    'key.converter' = 'org.apache.kafka.connect.storage.StringConverter',
    'value.converter' = 'io.confluent.connect.avro.AvroConverter',
    'value.converter.schema.registry.url' = '[http=//schema-registry=8081](http=//schema-registry=8081/)'
);
```

**Check if the connector is created successfuly with =**

`ksql> SHOW CONNECTORS;`

<aside>
💡 The **source connector** allows us to import data from any relational database with jdbc into Apache Kafka.

</aside>

<aside>
💡 The **sink connector** delivers data from Kafka topics into other systems or any kind of database

</aside>

**Check if the topic is created too with =**

`ksql> SHOW TOPICS;`

**Create ksqlDB stream and table =**

```jsx
CREATE
TABLE
USERS(CUSTOMER_ID
VARCHAR
PRIMARY
KEY
)
WITH(KAFKA_TOPIC = 'asgard.demo.CUSTOMERS', VALUE_FORMAT = 'AVRO');
```

**Query the ksqlDB table=**

```jsx
SET
'auto.offset.reset' = 'earliest';
SELECT
CUSTOMER_ID, FIRST_NAME, LAST_NAME, EMAIL, COMMENTS
FROM
USERS
EMIT
CHANGES
LIMIT
5;
```

****Make changes in MySQL, observe it in Kafka****

In MySQL terminal , you can make any changes in the table CUSTOMERS (INSERT,DELETE,UPDATE) and observe it with =

```jsx
SELECT
TIMESTAMPTOSTRING(ROWTIME, 'HH=mm=ss')
AS
EVENT_TIME,
    ID,
    FIRST_NAME,
    LAST_NAME,
    EMAIL,
    COMMENTS
FROM
USERS_STREAM
WHERE
ID = 190
EMIT
CHANGES;
```

```jsx
+-----------+-------------+-----------+----------+-----------------+------------+
| EVENT_TS | CUSTOMER_ID | FIRST_NAME | LAST_NAME | EMAIL | COMMENTS |
+-----------+-------------+-----------+----------+-----------------+------------+
| 0
9
    =
    20
        =
        15 | 190 | Wiam | Mossalli | WIAM
@example.com |
great |
^ CQuery
terminated
```

**Create stream CLUB_STATUS=**

```jsx
CREATE
STREAM
CLUB_STATUS
AS
SELECT
CLUB_STATUS, EMAIL
FROM
USERS_STREAM
WHERE
CLUB_STATUS = 'checked'
PARTITION
BY
EMAIL;
```

**Aggregations=**

this is a simple aggragation = count of ratings per person , per 15 minutes=

```jsx
CREATE
TABLE
RATINGS_CLUBS_PER_15MIN
AS
SELECT
FIRST_NAME, COUNT( *
)
AS
COUNT_CLUBS
FROM
CLUB_STATUS
WINDOW
TUMBLING(SIZE
15
MINUTE
)
GROUP
BY
FIRST_NAME
EMIT
CHANGES;
```

**Push Query =**

```jsx
SELECT
FIRST_NAME , COUNT_CLUBS
FROM
RATINGS_CLUBS_PER_15MIN
WHERE
FIRST_NAME = 'Wiam'
EMIT
CHANGES;
```

**Pull Query =**

```jsx
SELECT
TIMESTAMPTOSTRING(WINDOWSTART, 'yyyy-MM-dd
HH = mm = ss
') AS WINDOW_START_TS,
FIRST_NAME,
    COUNT_CLUBS
FROM
RATINGS_CLUBS_PER_15MIN
WHERE
FIRST_NAME = 'Wiam'
AND
WINDOWSTART > '2020-07-06T15=30=00.000';
```

We can also create joins between tables

## Part 03 = Show REST API with [Postman](https=//github.com/confluentinc/demo-scene/blob/master/build-a-streaming-pipeline/ksqlDB.postman_collection.json)

![My Image](postam.png)

```jsx
{
    'ksql'
        =
        'SELECT TIMESTAMPTOSTRING(WINDOWSTART, '
    yyyy - MM - dd
    HH = mm = ss
    ')
    AS
    WINDOW_START_TS, FIRST_NAME, COUNT_CLUBS
    FROM
    RATINGS_CLUBS_PER_15MIN;
    ',
    'streamProperties'
        =
        {
            'ksql.streams.auto.offset.reset'
            =
            'earliest'
}
}
```

CREATE SOURCE CONNECTOR SOURCE_MY WITH (
'connector.class' = 'io.debezium.connector.mysql.MySqlConnector',
'database.hostname' = 'mysql',
'database.port' = '3306',
'database.user' = 'debezium',
'database.password' = 'dbz',
'database.server.name' = 'duyo',
'table.whitelist' = 'duyo.campaigns,duyo.users,duyo.comments',
'database.history.kafka.bootstrap.servers' = 'kafka=29092',
'database.history.kafka.topic' = 'dbhistory.duyo' ,
'include.schema.changes' = 'false',
'transforms'= 'unwrap,extractkey',
'transforms.unwrap.type'= 'io.debezium.transforms.ExtractNewRecordState',
'transforms.extractkey.type'= 'org.apache.kafka.connect.transforms.ExtractField$Key',
'transforms.extractkey.field'= 'id',
'key.converter'= 'org.apache.kafka.connect.storage.StringConverter',
'value.converter'= 'io.confluent.connect.avro.AvroConverter',
'value.converter.schema.registry.url'= 'http=//schema-registry=8081'
);

docker exec -it mysql bash -c 'mysql -u root -p duyo'

CREATE TABLE NUMBER_OF_TESTS AS
SELECT ID, COUNT(1) AS COUNT
FROM STUDENTS
WINDOW TUMBLING(SIZE 1 SECOND)
GROUP BY ID;

CREATE TABLE count_table AS
SELECT ID,COUNT(*) AS COUNT
FROM USERS
WINDOW TUMBLING(SIZE 1 SECOND)
GROUP BY ID;



CREATE TABLE NUMBER_OF_TESTS AS
SELECT ID, COUNT(1) AS COUNT
FROM STUDENTS
WINDOW TUMBLING(SIZE 1 SECOND)
GROUP BY ID;





















