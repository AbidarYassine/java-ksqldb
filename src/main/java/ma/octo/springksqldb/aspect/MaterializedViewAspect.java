package ma.octo.springksqldb.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MaterializedViewAspect {


    @After("@annotation(ma.octo.springksqldb.aspect.annotations.MaterializedView)")
    public void test() throws Throwable {
        System.out.println("test ");
    }
}
