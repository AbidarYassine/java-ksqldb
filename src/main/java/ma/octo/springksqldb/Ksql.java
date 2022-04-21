package ma.octo.springksqldb;

import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Ksql {
    public static void main(String[] args) {
        ClientOptions options = ClientOptions.create()
                .setHost("localhost")
                .setPort(8088);
        Client client = Client.create(options);
        String pullQuery = "SELECT CUSTOMER_ID, FIRST_NAME, LAST_NAME, EMAIL, COMMENTS FROM USERS EMIT CHANGES LIMIT 5;";
        // Terminate any open connections and close the client
        BatchedQueryResult batchedQueryResult = client.executeQuery(pullQuery);
        List<Row> resultRows = null;
        try {
            resultRows = batchedQueryResult.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("Received results. Num rows: " + resultRows.size());
        for (Row row : resultRows) {
            System.out.println("Row: " + row.values());
        }

    }
}
