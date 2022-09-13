// Generated from C:/zjw5962/fudan/long-tailed-3/compiler-backend/src/main/java/top/soaringlab/longtailed/compilerbackend/dsl/expressionlanguage\ExpressionLanguage.g4 by ANTLR 4.9.3
package top.soaringlab.longtailed.compilerbackend.dsl.expressionlanguage;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExpressionLanguageParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExpressionLanguageVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExpressionLanguageParser#input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput(ExpressionLanguageParser.InputContext ctx);
	/**
	 * Visit a parse tree produced by the {@code negation}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegation(ExpressionLanguageParser.NegationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(ExpressionLanguageParser.ComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conjunction}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConjunction(ExpressionLanguageParser.ConjunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code disjunction}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDisjunction(ExpressionLanguageParser.DisjunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code number}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(ExpressionLanguageParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code additionSubtraction}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditionSubtraction(ExpressionLanguageParser.AdditionSubtractionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multiplicationDivision}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicationDivision(ExpressionLanguageParser.MultiplicationDivisionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code string}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(ExpressionLanguageParser.StringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code positiveNegative}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPositiveNegative(ExpressionLanguageParser.PositiveNegativeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code id}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(ExpressionLanguageParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesis(ExpressionLanguageParser.ParenthesisContext ctx);
}