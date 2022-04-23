package ma.octo.springksqldb.consume;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoadIntoMaterialize {

    private final Gson jsonConverter;

    @KafkaListener(topics = "CAMPAIGNS_VIEW_STATUS",groupId = "group-1")
    public void listen(String message) {
//		MessageKafka messageKafka = jsonConverter.fromJson(message, MessageKafka.class);
//        hexdump
        log.info("campaings_comments_view message: {}", message);
    }

}
