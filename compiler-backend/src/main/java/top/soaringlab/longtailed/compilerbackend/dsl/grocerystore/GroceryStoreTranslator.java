package top.soaringlab.longtailed.compilerbackend.dsl.grocerystore;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class GroceryStoreTranslator {

    public static String translate(String text) {
        ANTLRInputStream input = new ANTLRInputStream(text);
        GroceryStoreLexer lexer = new GroceryStoreLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GroceryStoreParser parser = new GroceryStoreParser(tokens);
        ParseTree tree = parser.inputs();
        GroceryStoreTranslatorVisitor groceryStoreTranslatorVisitor = new GroceryStoreTranslatorVisitor();
        return groceryStoreTranslatorVisitor.visit(tree);
    }
}
