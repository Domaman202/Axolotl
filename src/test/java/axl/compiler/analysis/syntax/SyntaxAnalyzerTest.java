package axl.compiler.analysis.syntax;

import axl.compiler.File;
import axl.compiler.IFile;
import axl.compiler.analysis.lexical.utils.TokenStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SyntaxAnalyzerTest {

    private TokenStream tokenStream;
    private SyntaxAnalyzer syntaxAnalyzer;

    @BeforeEach
    void setUp() {
        String testFilename = "Main.axl";
        // TODO
        String testContent = """
                """;
        IFile file = new File(testFilename, testContent);
        this.tokenStream = file.createTokenStream();
        this.syntaxAnalyzer = SyntaxAnalyzerAgent.createSyntaxAnalyzer();
    }

    @Test
    void analyze() {
        syntaxAnalyzer.analyze(tokenStream);
    }
}