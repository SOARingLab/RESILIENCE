package top.soaringlab.longtailed.compilerbackend.dsl.simple;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class SimpleVerifier {

    public static List<String> verify(String text) {
        ANTLRInputStream input = new ANTLRInputStream(text);
        SimpleLexer lexer = new SimpleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SimpleParser parser = new SimpleParser(tokens);
        ParseTree tree = parser.inputs();
        SimpleVerifierVisitor simpleVerifierVisitor = new SimpleVerifierVisitor();
        return simpleVerifierVisitor.visit(tree);
    }
}
