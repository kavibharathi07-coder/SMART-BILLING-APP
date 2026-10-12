package com.supermarket.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SQLQueryLoader {

    private static final Map<String, String> queries = new HashMap<>();

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

        try (Stream<Path> paths = Files.walk(Path.of(SQL_FOLDER))) {

            paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".sql"))
                .forEach(SQLQueryLoader::loadFile);

        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to load SQL files from: " + SQL_FOLDER, e
            );
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
            String query = parts[1].trim();

            if (!queryName.isEmpty()) {
                queries.put(queryName, query);
            }
        }
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

        Path filePath = Path.of(sqlFile);
        try {
            String content = Files.readString(filePath);
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
                    return parts[1].trim();
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