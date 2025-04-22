import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DocumentManagerTest {
    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void saveFileWithNullIdTest() {
        var document = DocumentManager.Document.builder()
                .id(null)
                .content("content")
                .created(null)
                .title("title")
                .author(new DocumentManager.Author("authorId", "author"))
                .build();
        DocumentManager.Document savedDocument = documentManager.save(document);
        assertNotNull(savedDocument.getId(), "id should be assigned");
        assertNotNull(savedDocument.getCreated());
        Optional<DocumentManager.Document> foundDocument = documentManager.findById(savedDocument.getId());
        foundDocument.ifPresent(value -> assertEquals(value, savedDocument));
    }

    @Test
    void searchTest() {
        var doc1 = DocumentManager.Document.builder()
                .id(null)
                .content("First Test Content")
                .title("First Title")
                .author(new DocumentManager.Author("author1", "Author One"))
                .build();

        var doc2 = DocumentManager.Document.builder()
                .id(null)
                .content("Second Test Content")
                .title("Second Title")
                .author(new DocumentManager.Author("author2", "Author Two"))
                .build();
        var doc3 = DocumentManager.Document.builder()
                .id(null)
                .content("Third Test Content")
                .title("Third Title")
                .author(new DocumentManager.Author("author3", "Author Three"))
                .build();
        var doc4 = DocumentManager.Document.builder()
                .id(null)
                .content("Fourth Test Content")
                .title("Fourth Title")
                .author(new DocumentManager.Author("author4", "Author Four"))
                .build();
        var doc5 = DocumentManager.Document.builder()
                .id(null)
                .content("Fifth Test Content")
                .title("Fifth Title")
                .author(new DocumentManager.Author("author5", "Author Five"))
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);
        documentManager.save(doc3);
        documentManager.save(doc4);
        documentManager.save(doc5);

        var searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Fir", "Fou"))
                .containsContents(List.of("th"))
                .createdTo(Instant.now())
                .build();

        List<DocumentManager.Document> result = documentManager.search(searchRequest);

        assertEquals(1, result.size());
        assertEquals("Fourth Title", result.get(0).getTitle());
    }

    @Test
    public void searchWithNullRequest() {
        var doc1 = DocumentManager.Document.builder()
                .id(null)
                .content("First Test Content")
                .title("First Title")
                .author(new DocumentManager.Author("author1", "Author One"))
                .build();

        var doc2 = DocumentManager.Document.builder()
                .id(null)
                .content("Second Test Content")
                .title("Second Title")
                .author(new DocumentManager.Author("author2", "Author Two"))
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        var searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(null)
                .containsContents(null)
                .createdTo(null)
                .authorIds(null)
                .createdFrom(null)
                .build();

        List<DocumentManager.Document> result = documentManager.search(searchRequest);

        assertEquals(2, result.size());
    }
}