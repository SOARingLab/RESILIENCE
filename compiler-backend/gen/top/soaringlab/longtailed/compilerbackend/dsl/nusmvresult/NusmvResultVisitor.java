// Generated from C:/zjw5962/fudan/long-tailed-3/compiler-backend/src/main/java/top/soaringlab/longtailed/compilerbackend/dsl/nusmvresult\NusmvResult.g4 by ANTLR 4.9.3
package top.soaringlab.longtailed.compilerbackend.dsl.nusmvresult;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link NusmvResultParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface NusmvResultVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link NusmvResultParser#trace}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrace(NusmvResultParser.TraceContext ctx);
	/**
	 * Visit a parse tree produced by {@link NusmvResultParser#state}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitState(NusmvResultParser.StateContext ctx);
	/**
	 * Visit a parse tree produced by {@link NusmvResultParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(NusmvResultParser.AssignmentContext ctx);
}