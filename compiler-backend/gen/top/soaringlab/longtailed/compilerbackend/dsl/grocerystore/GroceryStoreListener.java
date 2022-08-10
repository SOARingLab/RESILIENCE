// Generated from C:/zjw5962/fudan/long-tailed-3/compiler-backend/src/main/java/top/soaringlab/longtailed/compilerbackend/dsl/grocerystore\GroceryStore.g4 by ANTLR 4.9.2
package top.soaringlab.longtailed.compilerbackend.dsl.grocerystore;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GroceryStoreParser}.
 */
public interface GroceryStoreListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GroceryStoreParser#inputs}.
	 * @param ctx the parse tree
	 */
	void enterInputs(GroceryStoreParser.InputsContext ctx);
	/**
	 * Exit a parse tree produced by {@link GroceryStoreParser#inputs}.
	 * @param ctx the parse tree
	 */
	void exitInputs(GroceryStoreParser.InputsContext ctx);
	/**
	 * Enter a parse tree produced by {@link GroceryStoreParser#input}.
	 * @param ctx the parse tree
	 */
	void enterInput(GroceryStoreParser.InputContext ctx);
	/**
	 * Exit a parse tree produced by {@link GroceryStoreParser#input}.
	 * @param ctx the parse tree
	 */
	void exitInput(GroceryStoreParser.InputContext ctx);
	/**
	 * Enter a parse tree produced by {@link GroceryStoreParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(GroceryStoreParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GroceryStoreParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(GroceryStoreParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GroceryStoreParser#action}.
	 * @param ctx the parse tree
	 */
	void enterAction(GroceryStoreParser.ActionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GroceryStoreParser#action}.
	 * @param ctx the parse tree
	 */
	void exitAction(GroceryStoreParser.ActionContext ctx);
}