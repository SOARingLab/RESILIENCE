package top.soaringlab.longtailed.compilerbackend.dsl.expressionlanguage;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class ExpressionLanguageTranslator {
    public static String translate(String text) {
        ANTLRInputStream input = new ANTLRInputStream(text);
        ExpressionLanguageLexer lexer = new ExpressionLanguageLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExpressionLanguageParser parser = new ExpressionLanguageParser(tokens);
        ParseTree tree = parser.input();
        ExpressionLanguageTranslatorVisitor simpleTranslatorVisitor = new ExpressionLanguageTranslatorVisitor();
        return simpleTranslatorVisitor.visit(tree);
    }
}
