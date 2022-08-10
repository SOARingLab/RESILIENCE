package top.soaringlab.longtailed.compilerbackend.dsl.simple;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class SimpleTranslator {
    public static String translate(String text) {
        ANTLRInputStream input = new ANTLRInputStream(text);
        SimpleLexer lexer = new SimpleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SimpleParser parser = new SimpleParser(tokens);
        ParseTree tree = parser.inputs();
        SimpleTranslatorVisitor simpleTranslatorVisitor = new SimpleTranslatorVisitor();
        return simpleTranslatorVisitor.visit(tree);
    }
}
