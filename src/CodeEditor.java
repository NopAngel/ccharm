import javax.swing.JTextPane;
import javax.swing.text.StyleContext;

public class CodeEditor extends JTextPane {
    private SyntaxHighlighter highlighter;

    public CodeEditor() {
        super();
        highlighter = new SyntaxHighlighter();
        setDocument(highlighter);
        setBackground(new java.awt.Color(39, 40, 34)); // Fondo Monokai Dimmed
        setForeground(new java.awt.Color(248, 248, 242)); // Texto Monokai Dimmed
    }

    public void insertSnippet(String snippet) {
        this.setText(this.getText() + snippet);
    }

    public String getCode() {
        return this.getText();
    }

    public void highlightSyntax() {
        highlighter.highlightSyntax(getText());
    }
}
