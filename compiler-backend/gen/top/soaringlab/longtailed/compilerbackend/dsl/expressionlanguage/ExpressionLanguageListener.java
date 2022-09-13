// Generated from C:/zjw5962/fudan/long-tailed-3/compiler-backend/src/main/java/top/soaringlab/longtailed/compilerbackend/dsl/expressionlanguage\ExpressionLanguage.g4 by ANTLR 4.9.3
package top.soaringlab.longtailed.compilerbackend.dsl.expressionlanguage;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExpressionLanguageParser}.
 */
public interface ExpressionLanguageListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpressionLanguageParser#input}.
	 * @param ctx the parse tree
	 */
	void enterInput(ExpressionLanguageParser.InputContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionLanguageParser#input}.
	 * @param ctx the parse tree
	 */
	void exitInput(ExpressionLanguageParser.InputContext ctx);
	/**
	 * Enter a parse tree produced by the {@code negation}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterNegation(ExpressionLanguageParser.NegationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negation}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitNegation(ExpressionLanguageParser.NegationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterComparison(ExpressionLanguageParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitComparison(ExpressionLanguageParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code conjunction}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterConjunction(ExpressionLanguageParser.ConjunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code conjunction}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitConjunction(ExpressionLanguageParser.ConjunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code disjunction}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterDisjunction(ExpressionLanguageParser.DisjunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code disjunction}
	 * labeled alternative in {@link ExpressionLanguageParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitDisjunction(ExpressionLanguageParser.DisjunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code number}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNumber(ExpressionLanguageParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code number}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNumber(ExpressionLanguageParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code additionSubtraction}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAdditionSubtraction(ExpressionLanguageParser.AdditionSubtractionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code additionSubtraction}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAdditionSubtraction(ExpressionLanguageParser.AdditionSubtractionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multiplicationDivision}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicationDivision(ExpressionLanguageParser.MultiplicationDivisionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multiplicationDivision}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicationDivision(ExpressionLanguageParser.MultiplicationDivisionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code string}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterString(ExpressionLanguageParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code string}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitString(ExpressionLanguageParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code positiveNegative}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPositiveNegative(ExpressionLanguageParser.PositiveNegativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code positiveNegative}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPositiveNegative(ExpressionLanguageParser.PositiveNegativeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code id}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterId(ExpressionLanguageParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code id}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitId(ExpressionLanguageParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesis(ExpressionLanguageParser.ParenthesisContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link ExpressionLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesis(ExpressionLanguageParser.ParenthesisContext ctx);
}