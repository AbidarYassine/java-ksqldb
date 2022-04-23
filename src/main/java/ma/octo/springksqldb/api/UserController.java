package ma.octo.springksqldb.api;

import lombok.RequiredArgsConstructor;
import ma.octo.springksqldb.kasql.KaSqlApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;



@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final KaSqlApi kaSqlApi;

    @GetMapping("/")
    public Flux<Object> getAllUsers() {
        return Flux.just(kaSqlApi.executeQuery("SELECT * FROM USERS EMIT CHANGES;"));
    }
}
