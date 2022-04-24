package ma.octo.springksqldb.api;

import io.confluent.ksql.api.client.Row;
import io.confluent.ksql.api.client.StreamedQueryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.octo.springksqldb.ReactorClient;
import ma.octo.springksqldb.kasql.KaSqlApi;
import ma.octo.springksqldb.reactive.RowSubscriber;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api/campaings")
@RequiredArgsConstructor
@Slf4j
public class CampaingController {
    private static final Map<String, Object> properties = Map.of("auto.offset.reset", "earliest");
    private final KaSqlApi kaSqlApi;
    private final ReactorClient reactorClient;

    @GetMapping("/")
    public Flux<StreamedQueryResult> getAllUsers() {

        var pushQuery = "SELECT * FROM CAMPAIGNS_VIEW_STATUS EMIT CHANGES ";
        List<String> data = new ArrayList<>();
        kaSqlApi.streamQuery(pushQuery).thenAccept(streamedQueryResult -> {

            System.out.println("Query has started.Query ID: " + streamedQueryResult.queryID());

            RowSubscriber subscriber = new RowSubscriber(data);

            streamedQueryResult.subscribe(subscriber);
            log.info("stream {}", streamedQueryResult);
            if (streamedQueryResult.isComplete()) {
                log.info("data {}", subscriber.getData());
            }
        }).exceptionally(e -> {

            System.out.println("Request failed: " + e);

            return null;

        });
        return null;
    }

    @GetMapping("/reactive")
    public void test() {
        reactorClient.streamQueryFromBeginning("SELECT * FROM users EMIT CHANGES ;")
                .subscribe(row -> log.info("row= {}", row.toString()), error -> log.error("Push query request failed: " + error));
    }
}
