// Generated from C:/zjw5962/fudan/long-tailed-3/compiler-backend/src/main/java/top/soaringlab/longtailed/compilerbackend/dsl/nusmvresult\NusmvResult.g4 by ANTLR 4.9.3
package top.soaringlab.longtailed.compilerbackend.dsl.nusmvresult;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NusmvResultLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, WS=2, COMMENT_STAR=3, COMMENT_LINE=4, TRACE=5, STATE=6, ID=7;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "WS", "COMMENT_STAR", "COMMENT_LINE", "TRACE", "STATE", "ID"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "WS", "COMMENT_STAR", "COMMENT_LINE", "TRACE", "STATE", "ID"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public NusmvResultLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "NusmvResult.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\tM\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\3\2\3\3\6\3\25\n\3"+
		"\r\3\16\3\26\3\3\3\3\3\4\3\4\3\4\3\4\3\4\7\4 \n\4\f\4\16\4#\13\4\3\4\3"+
		"\4\3\5\3\5\3\5\3\5\7\5+\n\5\f\5\16\5.\13\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\7\69\n\6\f\6\16\6<\13\6\3\6\3\6\3\7\3\7\3\7\3\7\7\7D\n\7\f\7"+
		"\16\7G\13\7\3\b\6\bJ\n\b\r\b\16\bK\2\2\t\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\3\2\5\5\2\13\f\17\17\"\"\4\2\f\f\17\17\6\2\62;C\\aac|\2R\2\3\3\2\2\2"+
		"\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2"+
		"\2\2\3\21\3\2\2\2\5\24\3\2\2\2\7\32\3\2\2\2\t&\3\2\2\2\13\61\3\2\2\2\r"+
		"?\3\2\2\2\17I\3\2\2\2\21\22\7?\2\2\22\4\3\2\2\2\23\25\t\2\2\2\24\23\3"+
		"\2\2\2\25\26\3\2\2\2\26\24\3\2\2\2\26\27\3\2\2\2\27\30\3\2\2\2\30\31\b"+
		"\3\2\2\31\6\3\2\2\2\32\33\7,\2\2\33\34\7,\2\2\34\35\7,\2\2\35!\3\2\2\2"+
		"\36 \n\3\2\2\37\36\3\2\2\2 #\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\"$\3\2\2\2"+
		"#!\3\2\2\2$%\b\4\2\2%\b\3\2\2\2&\'\7/\2\2\'(\7/\2\2(,\3\2\2\2)+\n\3\2"+
		"\2*)\3\2\2\2+.\3\2\2\2,*\3\2\2\2,-\3\2\2\2-/\3\2\2\2.,\3\2\2\2/\60\b\5"+
		"\2\2\60\n\3\2\2\2\61\62\7V\2\2\62\63\7t\2\2\63\64\7c\2\2\64\65\7e\2\2"+
		"\65\66\7g\2\2\66:\3\2\2\2\679\n\3\2\28\67\3\2\2\29<\3\2\2\2:8\3\2\2\2"+
		":;\3\2\2\2;=\3\2\2\2<:\3\2\2\2=>\b\6\2\2>\f\3\2\2\2?@\7/\2\2@A\7@\2\2"+
		"AE\3\2\2\2BD\n\3\2\2CB\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3\2\2\2F\16\3\2\2"+
		"\2GE\3\2\2\2HJ\t\4\2\2IH\3\2\2\2JK\3\2\2\2KI\3\2\2\2KL\3\2\2\2L\20\3\2"+
		"\2\2\t\2\26!,:EK\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}