package com.supermarket.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SQLQueryLoader {

    private static final Map<String, String> queries = new LinkedHashMap<>();

    private static final String SQL_FOLDER = "sql";

    static {
        loadAllQueries();
    }

    private static String normalizeQueryName(String queryName) {
        if (queryName == null) {
            return "";
        }

        String normalized = queryName.trim();
        normalized = normalized.replaceFirst("^QUERY\\s*:\\s*", "");
        return normalized.trim();
    }

    private static void loadAllQueries() {

        Path sqlPath = findSqlFolder();

        if (sqlPath == null) {
            loadClasspathQueries();
            return;
        }

        try (Stream<Path> paths = Files.walk(sqlPath)) {

            paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".sql"))
                .forEach(SQLQueryLoader::loadFile);

        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to load SQL files from: " + sqlPath, e
            );
        }
    }

    private static Path findSqlFolder() {
        Path[] candidates = {
            Path.of(SQL_FOLDER),
            Path.of("src", "main", "resources", SQL_FOLDER),
            Path.of("target", "classes", SQL_FOLDER)
        };

        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }

        return null;
    }

    private static void loadClasspathQueries() {
        String[] resources = {
            "sql/04_crud_queries/application_queries.sql",
            "sql/04_crud_queries/product_queries.sql",
            "sql/04_crud_queries/customer_queries.sql",
            "sql/04_crud_queries/user_queries.sql"
        };

        ClassLoader classLoader = SQLQueryLoader.class.getClassLoader();

        for (String resource : resources) {
            try (InputStream stream = classLoader.getResourceAsStream(resource)) {
                if (stream == null) {
                    throw new IllegalStateException("SQL resource not found: " + resource);
                }

                String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                parseSqlSections(content, resource);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to read SQL resource: " + resource,
                        exception);
            }
        }
    }

    private static void loadFile(Path filePath) {
        try {
            String content = Files.readString(filePath);
            parseSqlSections(content, filePath.toString());
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to read SQL file: " + filePath, e
            );
        }
    }

    private static void parseSqlSections(String content, String filePath) {
        String[] sections = content.split("-- ");

        for (String section : sections) {
            if (section.trim().isEmpty()) {
                continue;
            }

            String[] parts = section.split("\\R", 2);
            if (parts.length < 2) {
                continue;
            }

            String queryName = normalizeQueryName(parts[0]);
            String query = normalizeSql(parts[1]);

            if (!queryName.isEmpty()) {
                queries.put(queryName, query);
            }
        }
    }

    private static String normalizeSql(String query) {
        String normalized = query.trim();

        while (normalized.endsWith(";")) {
            normalized = normalized.substring(0, normalized.length() - 1).trim();
        }

        return normalized;
    }

    public static String load(String sqlFile, String queryName) {
        String normalizedName = normalizeQueryName(queryName);

        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("SQL query name is required.");
        }

        String cached = queries.get(normalizedName);
        if (cached != null) {
            return cached;
        }

        try {
            String content = readSql(sqlFile);
            String[] sections = content.split("-- ");

            for (String section : sections) {
                if (section.trim().isEmpty()) {
                    continue;
                }

                String[] parts = section.split("\\R", 2);
                if (parts.length < 2) {
                    continue;
                }

                String candidateName = normalizeQueryName(parts[0]);
                if (candidateName.equals(normalizedName)) {
                    return normalizeSql(parts[1]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to read SQL file: " + sqlFile, e
            );
        }

        throw new RuntimeException(
            "SQL query not found: " + normalizedName + " in " + sqlFile
        );
    }

    private static String readSql(String sqlFile) throws IOException {
        Path filePath = Path.of(sqlFile);

        if (Files.exists(filePath)) {
            return Files.readString(filePath);
        }

        try (InputStream stream = SQLQueryLoader.class.getClassLoader()
                .getResourceAsStream(sqlFile.replace('\\', '/'))) {
            if (stream == null) {
                throw new IOException("SQL file or classpath resource not found: " + sqlFile);
            }

            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static String getQuery(String queryName) {
        String normalizedName = normalizeQueryName(queryName);
        String query = queries.get(normalizedName);

        if (query == null) {
            throw new RuntimeException(
                "SQL query not found: " + normalizedName
            );
        }

        return query;
    }
}
