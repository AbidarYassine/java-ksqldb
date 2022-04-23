package ma.octo.springksqldb.api;

import io.confluent.ksql.api.client.StreamedQueryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.octo.springksqldb.kasql.KaSqlApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api/campaings")
@RequiredArgsConstructor
@Slf4j
public class CampaingController {

    private final KaSqlApi kaSqlApi;

    @GetMapping("/")
    public StreamedQueryResult getAllUsers() throws ExecutionException, InterruptedException {
        var pushQuery = "SELECT * FROM CAMPAIGNS_VIEW_STATUS EMIT CHANGES;";
        return kaSqlApi.streamQuery(pushQuery).get();

    }

    @GetMapping("/flux")
    public Flux<String> getStreamString() {
        return Flux.just("Hello", "World");
    }
}
