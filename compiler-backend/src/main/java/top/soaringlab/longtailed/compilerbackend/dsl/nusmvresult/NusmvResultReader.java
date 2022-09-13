package top.soaringlab.longtailed.compilerbackend.dsl.nusmvresult;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class NusmvResultReader {

    public static List<String> read(String text) {
        ANTLRInputStream input = new ANTLRInputStream(text);
        NusmvResultLexer lexer = new NusmvResultLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        NusmvResultParser parser = new NusmvResultParser(tokens);
        ParseTree tree = parser.states();
        NusmvResultReaderVisitor visitor = new NusmvResultReaderVisitor();
        return visitor.visit(tree);
    }
}
