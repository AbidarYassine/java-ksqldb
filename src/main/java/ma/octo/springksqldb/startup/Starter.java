package ma.octo.springksqldb.startup;

import lombok.RequiredArgsConstructor;
import ma.octo.springksqldb.bo.KasqlTableManagement;
import ma.octo.springksqldb.dao.KasqlTableManagementDao;
import ma.octo.springksqldb.kasql.KaSqlApi;
import ma.octo.springksqldb.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


@Component
@RequiredArgsConstructor
public class Starter implements CommandLineRunner {

    @Value("${db.changelog-path}")
    private String changeLogPath;
    private final KaSqlApi api;
    private final KasqlTableManagementDao dao;

    @Override
    public void run(String... args) throws Exception {
        try {
            InputStream inputStream = Starter.class.getResourceAsStream("/V1__create_users_table.sql");
            InputStreamReader isReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isReader);
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            if (dao.findByFileName("V1__create_users_table.sql").isEmpty()) {
                new Thread(() -> {
                    api.executeQuery(sb.toString());
                }).start();
                KasqlTableManagement entity = new KasqlTableManagement();
                entity.setFileName("V1__create_users_table.sql");
                entity.setFileContent(sb.toString());
                entity.setVersion("V1");
                dao.save(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
