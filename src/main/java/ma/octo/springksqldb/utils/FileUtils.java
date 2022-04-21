package ma.octo.springksqldb.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class FileUtils {
    public static List<String> mapFolder(String path) {
        var map = new ArrayList<String>();
        var unmappedDirs = new ArrayList<String>();
        File[] items = new File(path).listFiles();
        log.info("path: {}", path);
        try {
            Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .forEach(System.out::println);
        } catch (Exception e) {
            log.error("Error while mapping folder", e);
        }

        assert items != null;
        log.info("mapFolder lenth: {}", items.length);
        return map;

    }

    public static void doSomethingWithResourcesFolder(String inResourcesPath) throws URISyntaxException {
        URI uri = FileUtils.class.getResource(inResourcesPath).toURI();
        try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            Path folderRootPath = fileSystem.getPath(inResourcesPath);
            Stream<Path> walk = Files.walk(folderRootPath, 1);
            walk.forEach(childFileOrFolder -> {
                log.info("childFileOrFolder: {}", childFileOrFolder);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
