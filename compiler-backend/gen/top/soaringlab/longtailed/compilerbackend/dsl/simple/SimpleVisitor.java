// Generated from C:/zjw5962/fudan/long-tailed-3/compiler-backend/src/main/java/top/soaringlab/longtailed/compilerbackend/dsl/simple\Simple.g4 by ANTLR 4.9.2
package top.soaringlab.longtailed.compilerbackend.dsl.simple;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SimpleParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SimpleVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SimpleParser#inputs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputs(SimpleParser.InputsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput(SimpleParser.InputContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(SimpleParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction(SimpleParser.ActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code number}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(SimpleParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code additionSubtraction}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditionSubtraction(SimpleParser.AdditionSubtractionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multiplicationDivision}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicationDivision(SimpleParser.MultiplicationDivisionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code string}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(SimpleParser.StringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code positiveNegative}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPositiveNegative(SimpleParser.PositiveNegativeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code id}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(SimpleParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesis(SimpleParser.ParenthesisContext ctx);
}