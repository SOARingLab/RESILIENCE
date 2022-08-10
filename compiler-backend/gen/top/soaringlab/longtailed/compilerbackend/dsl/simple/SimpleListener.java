// Generated from C:/zjw5962/fudan/long-tailed-3/compiler-backend/src/main/java/top/soaringlab/longtailed/compilerbackend/dsl/simple\Simple.g4 by ANTLR 4.9.2
package top.soaringlab.longtailed.compilerbackend.dsl.simple;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SimpleParser}.
 */
public interface SimpleListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SimpleParser#inputs}.
	 * @param ctx the parse tree
	 */
	void enterInputs(SimpleParser.InputsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#inputs}.
	 * @param ctx the parse tree
	 */
	void exitInputs(SimpleParser.InputsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#input}.
	 * @param ctx the parse tree
	 */
	void enterInput(SimpleParser.InputContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#input}.
	 * @param ctx the parse tree
	 */
	void exitInput(SimpleParser.InputContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(SimpleParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(SimpleParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#action}.
	 * @param ctx the parse tree
	 */
	void enterAction(SimpleParser.ActionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#action}.
	 * @param ctx the parse tree
	 */
	void exitAction(SimpleParser.ActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code number}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNumber(SimpleParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code number}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNumber(SimpleParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code additionSubtraction}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAdditionSubtraction(SimpleParser.AdditionSubtractionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code additionSubtraction}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAdditionSubtraction(SimpleParser.AdditionSubtractionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multiplicationDivision}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicationDivision(SimpleParser.MultiplicationDivisionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multiplicationDivision}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicationDivision(SimpleParser.MultiplicationDivisionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code string}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterString(SimpleParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code string}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitString(SimpleParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code positiveNegative}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPositiveNegative(SimpleParser.PositiveNegativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code positiveNegative}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPositiveNegative(SimpleParser.PositiveNegativeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code id}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterId(SimpleParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code id}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitId(SimpleParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesis(SimpleParser.ParenthesisContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link SimpleParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesis(SimpleParser.ParenthesisContext ctx);
}