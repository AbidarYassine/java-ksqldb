package ma.octo.springksqldb.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.octo.springksqldb.bo.KasqlTableManagement;
import ma.octo.springksqldb.dao.KasqlTableManagementDao;
import ma.octo.springksqldb.kasql.KaSqlApi;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;


@Component
@RequiredArgsConstructor
@Slf4j
public class Starter implements CommandLineRunner {

    private final KaSqlApi kasqlApi;
    private final KasqlTableManagementDao kasqlTableManagementDao;
    @Value("${db.changelog-path}")
    private String pathToKsqlScript;
    @Value("${db.changelog-folder-name}")
    private String ksqlFolderName;

    @Override
    public void run(String... args) {
        final File folder = new File(pathToKsqlScript);
        if (!folder.exists()) return;
        processAllFiles(folder);
    }

    public void processAllFiles(final File folder) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            processFile(fileEntry);
        }
    }

    private void processFile(File fileEntry) {
        String content = getContentOfFile(fileEntry.getName());
        if (kasqlTableManagementDao.findByFileName(fileEntry.getName()).isEmpty()) {
            kasqlApi.executeStatement(content);
            addExecutedFileToDb(fileEntry.getName(), content);
        } else {
            String newHash = getHash(content);
            if (kasqlTableManagementDao.findByHashFile(newHash).isEmpty()) {
                log.info("FileCHanged : {}", fileEntry.getName());
            } else {
                log.info("File not changed : {}", fileEntry.getName());
            }
        }

    }

    private void addExecutedFileToDb(String name, String content) {
        KasqlTableManagement kasqlTableManagement = new KasqlTableManagement();
        setData(kasqlTableManagement, name, content);
        kasqlTableManagementDao.save(kasqlTableManagement);
    }

    private void setData(KasqlTableManagement entity, String name, String content) {
        entity.setFileName(name);
        entity.setFileContent(content);
        String hash = getHash(content);
        entity.setHashFile(hash);
        entity.setVersion(name.split("__")[0]);
    }

    private String getHash(String content) {
        return DigestUtils.md5Hex(content).toUpperCase();
    }

    public String getContentOfFile(String fileName) {
        log.info("processing file : {}", fileName);
        InputStream inputStream = Starter.class.getResourceAsStream(String.format("/%s/%s", ksqlFolderName, fileName));
        if (inputStream == null) throw new RuntimeException("file not found");
        InputStreamReader isReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String str;
        try {
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}


