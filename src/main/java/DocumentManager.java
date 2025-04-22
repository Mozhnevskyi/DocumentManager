import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {
    private final Map<String, Document> fileStorage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        var id = document.id;
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            document.setId(id);
        }
        if (document.getCreated() == null) {
            document.setCreated(Instant.now());
        }
        fileStorage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        if (request == null)
            return List.of();

        List<Document> resultList = new ArrayList<>(fileStorage.values());
        resultList.removeIf(Objects::isNull);

        if (!resultList.isEmpty() && request.getContainsContents() != null &&
                !request.getContainsContents().isEmpty()) {
            List<String> validContents = request.getContainsContents().stream()
                    .filter(s -> s != null && !s.isEmpty()).toList();
            if (!validContents.isEmpty()) {
                resultList = resultList.stream()
                        .filter(doc -> doc.content != null &&
                                validContents.stream().anyMatch(doc.content::contains))
                        .toList();
            }
        }
        if (!resultList.isEmpty() && request.getTitlePrefixes() != null &&
                !request.getTitlePrefixes().isEmpty()) {
            List<String> validTitlePrefixes = request.getTitlePrefixes().stream()
                    .filter(s -> s != null && !s.isEmpty()).toList();
            if (!validTitlePrefixes.isEmpty()) {
                resultList = resultList.stream()
                        .filter(doc -> doc.title != null &&
                                validTitlePrefixes.stream().anyMatch(doc.title::startsWith))
                        .toList();
            }
        }
        if (!resultList.isEmpty() && request.getAuthorIds() != null &&
                !request.getAuthorIds().isEmpty()) {
            List<String> validAuthorIds = request.getAuthorIds().stream()
                    .filter(s -> s != null && !s.isEmpty()).toList();
            if (!validAuthorIds.isEmpty()) {
                resultList = resultList.stream()
                        .filter(doc -> doc.author != null && doc.author.id != null &&
                                validAuthorIds.stream().anyMatch(doc.author.id::equals))
                        .toList();
            }
        }
        if (!resultList.isEmpty() && (request.getCreatedFrom() != null || request.getCreatedTo() != null)) {
            resultList = resultList.stream()
                    .filter(doc -> {
                        Instant created = doc.getCreated();
                        if (created == null) return false;
                        Instant from = request.getCreatedFrom();
                        Instant to = request.getCreatedTo();
                        if (from != null && created.isBefore(from)) return false;
                        return to == null || !created.isAfter(to);
                    })
                    .toList();
        }
        return resultList;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(fileStorage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}