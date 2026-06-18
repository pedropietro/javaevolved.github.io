///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25

import module java.base;

/**
 * Audits translated content coverage.
 *
 * The source of truth is content/<category>/*.yaml|yml|json.
 * Every supported non-English locale from html-generators/locales.properties
 * should have a matching file under translations/content/<locale>/<category>/.
 *
 * Usage:
 *   jbang html-generators/audit-translations.java
 *   jbang html-generators/audit-translations.java --allow-orphans
 */
static final Path CONTENT_DIR = Path.of("content");
static final Path TRANSLATED_CONTENT_DIR = Path.of("translations", "content");
static final Path CATEGORIES_FILE = Path.of("html-generators", "categories.properties");
static final Path LOCALES_FILE = Path.of("html-generators", "locales.properties");
static final Set<String> CONTENT_EXTENSIONS = Set.of("yaml", "yml", "json");

record AuditResult(
    SequencedMap<String, List<String>> missingByLocale,
    SequencedMap<String, List<String>> orphanedByLocale,
    int sourceCount,
    int expectedTranslationCount
) {
    int missingCount() {
        return missingByLocale.values().stream().mapToInt(List::size).sum();
    }

    int orphanedCount() {
        return orphanedByLocale.values().stream().mapToInt(List::size).sum();
    }
}

void main(String... args) throws IOException {
    var allowOrphans = Arrays.asList(args).contains("--allow-orphans");
    var categories = loadRegistry(CATEGORIES_FILE);
    var locales = loadRegistry(LOCALES_FILE);
    var sourcePaths = loadSourceContentPaths(categories.sequencedKeySet());
    var result = audit(locales.sequencedKeySet(), sourcePaths);

    printReport(result, locales.sequencedKeySet());

    if (result.missingCount() > 0 || (!allowOrphans && result.orphanedCount() > 0)) {
        System.exit(1);
    }
}

SequencedMap<String, String> loadRegistry(Path file) throws IOException {
    var values = new LinkedHashMap<String, String>();
    for (var rawLine : Files.readAllLines(file)) {
        var line = rawLine.strip();
        if (line.isEmpty() || line.startsWith("#")) continue;

        var idx = line.indexOf('=');
        if (idx <= 0) {
            throw new IOException("Invalid registry line in %s: %s".formatted(file, rawLine));
        }

        values.put(line.substring(0, idx).strip(), line.substring(idx + 1).strip());
    }
    return values;
}

List<String> loadSourceContentPaths(SequencedSet<String> categories) throws IOException {
    var sourcePaths = new ArrayList<String>();

    for (var category : categories) {
        var categoryDir = CONTENT_DIR.resolve(category);
        if (!Files.isDirectory(categoryDir)) continue;

        try (var files = Files.list(categoryDir)) {
            files
                .filter(Files::isRegularFile)
                .filter(path -> hasContentExtension(path))
                .map(CONTENT_DIR::relativize)
                .map(Path::toString)
                .map(path -> normalizePath(path))
                .sorted()
                .forEach(sourcePaths::add);
        }
    }

    return List.copyOf(sourcePaths);
}

AuditResult audit(SequencedSet<String> locales, List<String> sourcePaths) throws IOException {
    var missingByLocale = new LinkedHashMap<String, List<String>>();
    var orphanedByLocale = new LinkedHashMap<String, List<String>>();

    for (var locale : locales) {
        if (locale.equals("en")) continue;

        var missing = new ArrayList<String>();
        for (var sourcePath : sourcePaths) {
            var translatedPath = TRANSLATED_CONTENT_DIR.resolve(locale).resolve(sourcePath);
            if (!Files.exists(translatedPath)) {
                missing.add("translations/content/%s/%s".formatted(locale, sourcePath));
            }
        }

        var orphaned = findOrphanedTranslations(locale, sourcePaths);
        missingByLocale.put(locale, List.copyOf(missing));
        orphanedByLocale.put(locale, List.copyOf(orphaned));
    }

    return new AuditResult(
        missingByLocale,
        orphanedByLocale,
        sourcePaths.size(),
        sourcePaths.size() * Math.max(0, locales.size() - 1));
}

List<String> findOrphanedTranslations(String locale, List<String> sourcePaths) throws IOException {
    var localeDir = TRANSLATED_CONTENT_DIR.resolve(locale);
    if (!Files.isDirectory(localeDir)) return List.of();

    var sourceSet = new HashSet<>(sourcePaths);
    var orphaned = new ArrayList<String>();

    try (var files = Files.walk(localeDir)) {
        files
            .filter(Files::isRegularFile)
            .filter(path -> hasContentExtension(path))
            .map(localeDir::relativize)
            .map(Path::toString)
            .map(path -> normalizePath(path))
            .filter(path -> !sourceSet.contains(path))
            .map(path -> "translations/content/%s/%s".formatted(locale, path))
            .sorted()
            .forEach(orphaned::add);
    }

    return List.copyOf(orphaned);
}

boolean hasContentExtension(Path path) {
    var name = path.getFileName().toString();
    var dot = name.lastIndexOf('.');
    return dot > 0 && CONTENT_EXTENSIONS.contains(name.substring(dot + 1));
}

String normalizePath(String path) {
    return path.replace(File.separatorChar, '/');
}

void printReport(AuditResult result, SequencedSet<String> locales) {
    IO.println("Content translation coverage audit");
    IO.println("Source snippets: %d".formatted(result.sourceCount()));
    IO.println("Expected translated files: %d".formatted(result.expectedTranslationCount()));
    IO.println("Missing translated files: %d".formatted(result.missingCount()));
    IO.println("Orphaned translated files: %d".formatted(result.orphanedCount()));
    IO.println();

    for (var locale : locales) {
        if (locale.equals("en")) continue;

        var missing = result.missingByLocale().getOrDefault(locale, List.of());
        var orphaned = result.orphanedByLocale().getOrDefault(locale, List.of());

        if (missing.isEmpty() && orphaned.isEmpty()) {
            IO.println("%s: OK".formatted(locale));
            continue;
        }

        IO.println("%s:".formatted(locale));
        if (!missing.isEmpty()) {
            IO.println("  missing:");
            missing.forEach(path -> IO.println("    - " + path));
        }
        if (!orphaned.isEmpty()) {
            IO.println("  orphaned:");
            orphaned.forEach(path -> IO.println("    - " + path));
        }
    }
}
