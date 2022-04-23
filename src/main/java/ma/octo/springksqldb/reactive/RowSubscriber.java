package ma.octo.springksqldb.reactive;

import io.confluent.ksql.api.client.Row;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class RowSubscriber  implements Subscriber<Row> {
    private Subscription subscription;

    public RowSubscriber() {
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
}
