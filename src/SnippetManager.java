import java.util.ArrayList;
import java.util.List;

public class SnippetManager {
    private List<Snippet> snippets;

    public SnippetManager() {
        snippets = new ArrayList<>();
    }

    public void addSnippet(Snippet snippet) {
        snippets.add(snippet);
    }

    public List<Snippet> getSnippets() {
        return snippets;
    }

    public Snippet getSnippetByName(String name) {
        for (Snippet snippet : snippets) {
            if (snippet.getName().equals(name)) {
                return snippet;
            }
        }
        return null;
    }
}
