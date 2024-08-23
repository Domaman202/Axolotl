package axl.compiler;

import axl.compiler.analysis.lexical.DefaultTokenizer;
import axl.compiler.analysis.lexical.IToken;
import axl.compiler.analysis.lexical.Tokenizer;
import axl.compiler.analysis.lexical.utils.DefaultTokenStream;
import axl.compiler.analysis.lexical.utils.TokenStream;
import axl.compiler.analysis.syntax.SyntaxAnalyzer;
import axl.compiler.analysis.syntax.SyntaxAnalyzerAgent;
import axl.compiler.analysis.syntax.ast.Node;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static IFile file;

    @SneakyThrows
    public static void main(String[] args) {
        File fileAXL = new File(args[0]);
        String filename = fileAXL.getName();
        String content = Files.readString(fileAXL.toPath());
        file = new axl.compiler.File(filename, content);

        TokenStream stream = file.createTokenStream();
        SyntaxAnalyzer syntaxAnalyzer = SyntaxAnalyzerAgent.createSyntaxAnalyzer();
        long point = System.currentTimeMillis();
        Node add = syntaxAnalyzer.analyze(stream);
        long time = ((int) (System.currentTimeMillis() - point));

        System.out.println(formatString(add.toString()));

        System.out.println((int) time + " ms");

//        while (stream.hasNext())
//            System.out.println(stream.next().getType());
    }

    public static String formatString(String input) {
        input = input.replace(" ", "");
        StringBuilder formatted = new StringBuilder();
        int indentLevel = 0;

        for (char c : input.toCharArray()) {
            if (c == '{') {
                formatted.append(" ").append(c).append("\n");
                formatted.append(" ".repeat(++indentLevel*4));
            } else if (c == '}') {
                formatted.append("\n").append(" ".repeat(--indentLevel*4));
                formatted.append(c);
            } else if (c == ',') {
                formatted.append(c).append('\n').append(" ".repeat(indentLevel*4));
            } else {
                formatted.append(c);
            }
        }

        return formatted.toString();
    }

}