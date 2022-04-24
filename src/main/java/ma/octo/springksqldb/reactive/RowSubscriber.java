package ma.octo.springksqldb.reactive;

import io.confluent.ksql.api.client.Row;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RowSubscriber implements Subscriber<Row> {

    List<String> data;
    private boolean completed = false;
    private Subscription subscription;

    public RowSubscriber(List<String> data) {
        this.data = data;
    }

    @Override
    public synchronized void onSubscribe(Subscription subscription) {
        log.info("Subscriber is subscribed.");
        this.subscription = subscription;

        // Request the first row
        subscription.request(1);
    }

    @Override
    public synchronized void onNext(Row row) {
        log.info("Received a row!");
        log.info("Row: " + row.values());
        data.add(row.values().toString());
        // Request the next row
        subscription.request(1);
    }

    @Override
    public synchronized void onError(Throwable t) {
        log.info("Received an error: " + t);
    }

    @Override
    public synchronized void onComplete() {
        log.info("Query has ended.");
    }

    public List<String> getData() {
        return data;
    }
}
