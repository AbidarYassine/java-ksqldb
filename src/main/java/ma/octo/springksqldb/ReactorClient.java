package ma.octo.springksqldb;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.ExecuteStatementResult;
import io.confluent.ksql.api.client.InsertAck;
import io.confluent.ksql.api.client.KsqlObject;
import io.confluent.ksql.api.client.QueryInfo;
import io.confluent.ksql.api.client.Row;
import io.confluent.ksql.api.client.StreamInfo;
import io.confluent.ksql.api.client.TableInfo;
import io.confluent.ksql.api.client.TopicInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Map.of;
import static reactor.core.publisher.Mono.fromFuture;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static reactor.core.publisher.Mono.fromFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactorClient {


    private final Client ksqlDbClient;


    /**
     * Creates Reactor wrapper for ksqlDB Client
     */
    public static ReactorClient from(Client ksqlDbClient) {
        return new ReactorClient(ksqlDbClient);
    }

    public static ReactorClient fromOptions(ClientOptions options) {
        return new ReactorClient(Client.create(options));
    }

    public Mono<ExecuteStatementResult> executeStatement(String sql, Map<String, Object> properties) {
        return fromFuture(() -> ksqlDbClient.executeStatement(sql, properties));
    }

    public Mono<ExecuteStatementResult> executeStatement(String sql) {
        return this.executeStatement(sql, Collections.emptyMap());
    }

    public Flux<InsertAck> streamInserts(String streamName, Publisher<KsqlObject> insertsPublisher) {
        return fromFuture(() -> this.ksqlDbClient.streamInserts(streamName, insertsPublisher))
                .flatMapMany(acksPublisher -> acksPublisher);
    }

    public Flux<Row> streamQuery(String sql, Map<String, Object> properties) {
        return fromFuture(() -> this.ksqlDbClient.streamQuery(sql, properties))
                .flatMapMany(streamedQueryResult -> {
                    log.info("Result column names: {}", streamedQueryResult.columnNames());
                    return streamedQueryResult;
                });
    }

    public Flux<Row> streamQueryFromBeginning(String sql) {
        return this.streamQuery(sql, of("auto.offset.reset", "earliest"));
    }

    public Flux<Row> streamQuery(String sql) {
        return this.streamQuery(sql, Collections.emptyMap());
    }

    public Mono<List<Row>> executeQuery(String sql) {
        return this.executeQuery(sql, Collections.emptyMap());
    }

    public Mono<List<Row>> executeQueryFromBeginning(String sql) {
        return this.executeQuery(sql, of("auto.offset.reset", "earliest"));
    }

    public Mono<List<Row>> executeQuery(String sql, Map<String, Object> properties) {
        return fromFuture(() -> this.ksqlDbClient.executeQuery(sql, properties));
    }


    public Mono<List<StreamInfo>> listStreams() {
        return fromFuture(this.ksqlDbClient::listStreams);
    }

    /**
     * Returns the list of ksqlDB tables from the ksqlDB server's metastore
     */
    public Mono<List<TableInfo>> listTables() {
        return fromFuture(this.ksqlDbClient::listTables);
    }

    /**
     * Returns the list of Kafka topics available for use with ksqlDB.
     */
    public Mono<List<TopicInfo>> listTopics() {
        return fromFuture(this.ksqlDbClient::listTopics);
    }

    /**
     * Returns the list of queries currently running on the ksqlDB server.
     */
    public Mono<List<QueryInfo>> listQueries() {
        return fromFuture(this.ksqlDbClient::listQueries);
    }

    /**
     * Inserts a row into a ksqlDB stream.
     *
     * @param streamName name of the target stream
     * @param row        the row to insert. Keys are column names and values are column values.
     * @return a Mono that completes once the server response is received
     */
    public Mono<Void> insertInto(String streamName, KsqlObject row) {
        return fromFuture(() -> this.ksqlDbClient.insertInto(streamName, row))
                .doOnError(throwable -> log.error("Insert failed", throwable));
    }
}
