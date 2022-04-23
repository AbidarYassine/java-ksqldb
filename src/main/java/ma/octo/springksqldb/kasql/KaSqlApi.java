package ma.octo.springksqldb.kasql;

import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ExecuteStatementResult;
import io.confluent.ksql.api.client.StreamedQueryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class KaSqlApi {
    /*
     * Create Drop Terminate Statement
     * */
    private final Client client;

    /*
    String sql = "CREATE STREAM ORDERS (ORDER_ID BIGINT, PRODUCT_ID VARCHAR, USER_ID VARCHAR)"
            + "WITH (KAFKA_TOPIC='orders', VALUE_FORMAT='json');";
    */
    public CompletableFuture<ExecuteStatementResult> executeStatement(String sql) {
        return client.executeStatement(sql);
    }

    public BatchedQueryResult executeQuery(String sql) {
        return client.executeQuery(sql);
    }

    public CompletableFuture<StreamedQueryResult> streamQuery(String sql) {
        return client.streamQuery(sql);
    }

/*
*
* /*
    Map<String, String> connectorProperties = ImmutableMap.of(
            "connector.class", "io.confluent.connect.jdbc.JdbcSourceConnector",
            "connection.url", "jdbc:postgresql://localhost:5432/my.db",
            "mode", "bulk",
            "topic.prefix", "jdbc-",
            "table.whitelist", "users",
            "key", "username"
    );
client.createConnector("jdbc-connector", true, connectorProperties).get();
/*
* */
}
