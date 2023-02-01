package top.soaringlab.longtailed.compilerbackend.dsl.simple;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class SimpleTransformer {

    public static List<String> transform(String text) {
        ANTLRInputStream input = new ANTLRInputStream(text);
        SimpleLexer lexer = new SimpleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SimpleParser parser = new SimpleParser(tokens);
        ParseTree tree = parser.inputs();
        SimpleTransformerVisitor simpleTransformerVisitor = new SimpleTransformerVisitor();
        return simpleTransformerVisitor.visit(tree);
    }
}
