import javax.swing.text.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.*;

public class SyntaxHighlighter extends DefaultStyledDocument {
    private Style defaultStyle;
    private Style keywordStyle;
    private Style commentStyle;
    private Style stringStyle;

    private Set<String> keywords;

    public SyntaxHighlighter() {
        // Estilos de Monokai Dimmed
        StyleContext context = StyleContext.getDefaultStyleContext();
        defaultStyle = context.getStyle(StyleContext.DEFAULT_STYLE);

        keywordStyle = context.addStyle("KeywordStyle", null);
        StyleConstants.setForeground(keywordStyle, new java.awt.Color(249, 38, 114));

        commentStyle = context.addStyle("CommentStyle", null);
        StyleConstants.setForeground(commentStyle, new java.awt.Color(117, 113, 94));

        stringStyle = context.addStyle("StringStyle", null);
        StyleConstants.setForeground(stringStyle, new java.awt.Color(230, 219, 116));

        keywords = new HashSet<>();
        // Añade aquí las palabras clave para los lenguajes soportados
        String[] javaKeywords = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
                "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
                "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new",
                "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while" };
        for (String keyword : javaKeywords) {
            keywords.add(keyword);
        }
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        super.insertString(offset, str, attr);
        highlightSyntax(getText(0, getLength()));
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        highlightSyntax(getText(0, getLength()));
    }

    public void highlightSyntax(String text) {
        clearHighlights(text);
        highlightPattern(text, Pattern.compile("//.*"), commentStyle); // Comentarios
        highlightPattern(text, Pattern.compile("\".*?\""), stringStyle); // Cadenas de texto
        highlightPattern(text, Pattern.compile("\\b(" + String.join("|", keywords) + ")\\b"), keywordStyle); // Palabras
                                                                                                             // clave
    }

    private void clearHighlights(String text) {
        setCharacterAttributes(0, text.length(), defaultStyle, true);
    }

    private void highlightPattern(String text, Pattern pattern, Style style) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), style, false);
        }
    }
}
