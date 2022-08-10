// Generated from C:/zjw5962/fudan/long-tailed-3/compiler-backend/src/main/java/top/soaringlab/longtailed/compilerbackend/dsl/grocerystore\GroceryStore.g4 by ANTLR 4.9.2
package top.soaringlab.longtailed.compilerbackend.dsl.grocerystore;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GroceryStoreParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GroceryStoreVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GroceryStoreParser#inputs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputs(GroceryStoreParser.InputsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GroceryStoreParser#input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput(GroceryStoreParser.InputContext ctx);
	/**
	 * Visit a parse tree produced by {@link GroceryStoreParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(GroceryStoreParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GroceryStoreParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction(GroceryStoreParser.ActionContext ctx);
}