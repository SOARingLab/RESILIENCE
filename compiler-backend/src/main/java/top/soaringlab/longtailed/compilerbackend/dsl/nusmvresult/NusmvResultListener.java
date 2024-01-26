// Generated from C:\zjw5962\fudan\long-tailed-3\compiler-backend\src\main\java\top\soaringlab\longtailed\compilerbackend\dsl\nusmvresult\NusmvResult.g4 by ANTLR 4.9.3
package top.soaringlab.longtailed.compilerbackend.dsl.nusmvresult;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link NusmvResultParser}.
 */
public interface NusmvResultListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link NusmvResultParser#trace}.
	 * @param ctx the parse tree
	 */
	void enterTrace(NusmvResultParser.TraceContext ctx);
	/**
	 * Exit a parse tree produced by {@link NusmvResultParser#trace}.
	 * @param ctx the parse tree
	 */
	void exitTrace(NusmvResultParser.TraceContext ctx);
	/**
	 * Enter a parse tree produced by {@link NusmvResultParser#state}.
	 * @param ctx the parse tree
	 */
	void enterState(NusmvResultParser.StateContext ctx);
	/**
	 * Exit a parse tree produced by {@link NusmvResultParser#state}.
	 * @param ctx the parse tree
	 */
	void exitState(NusmvResultParser.StateContext ctx);
	/**
	 * Enter a parse tree produced by {@link NusmvResultParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(NusmvResultParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link NusmvResultParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(NusmvResultParser.AssignmentContext ctx);
}