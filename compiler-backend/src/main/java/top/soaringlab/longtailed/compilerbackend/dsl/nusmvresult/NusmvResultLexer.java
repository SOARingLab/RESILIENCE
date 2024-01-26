// Generated from C:\zjw5962\fudan\long-tailed-3\compiler-backend\src\main\java\top\soaringlab\longtailed\compilerbackend\dsl\nusmvresult\NusmvResult.g4 by ANTLR 4.9.3
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
		T__0=1, WS=2, COMMENT_STAR=3, COMMENT_LINE=4, TRACE_DESCRIPTION=5, TRACE_TYPE=6, 
		STATE=7, ID=8;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "WS", "COMMENT_STAR", "COMMENT_LINE", "TRACE_DESCRIPTION", "TRACE_TYPE", 
			"STATE", "ID"
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
			null, null, "WS", "COMMENT_STAR", "COMMENT_LINE", "TRACE_DESCRIPTION", 
			"TRACE_TYPE", "STATE", "ID"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\nl\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\3\6\3"+
		"\27\n\3\r\3\16\3\30\3\3\3\3\3\4\3\4\3\4\3\4\3\4\7\4\"\n\4\f\4\16\4%\13"+
		"\4\3\4\3\4\3\5\3\5\3\5\3\5\7\5-\n\5\f\5\16\5\60\13\5\3\5\3\5\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\7\6H\n\6\f\6\16\6K\13\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\7\7Z\n\7\f\7\16\7]\13\7\3\b\3\b\3\b\3\b\7\bc\n\b\f\b\16\bf\13\b"+
		"\3\t\6\ti\n\t\r\t\16\tj\2\2\n\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\3\2\5"+
		"\5\2\13\f\17\17\"\"\4\2\f\f\17\17\7\2//\62;C\\aac|\2r\2\3\3\2\2\2\2\5"+
		"\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2"+
		"\2\21\3\2\2\2\3\23\3\2\2\2\5\26\3\2\2\2\7\34\3\2\2\2\t(\3\2\2\2\13\63"+
		"\3\2\2\2\rL\3\2\2\2\17^\3\2\2\2\21h\3\2\2\2\23\24\7?\2\2\24\4\3\2\2\2"+
		"\25\27\t\2\2\2\26\25\3\2\2\2\27\30\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2"+
		"\31\32\3\2\2\2\32\33\b\3\2\2\33\6\3\2\2\2\34\35\7,\2\2\35\36\7,\2\2\36"+
		"\37\7,\2\2\37#\3\2\2\2 \"\n\3\2\2! \3\2\2\2\"%\3\2\2\2#!\3\2\2\2#$\3\2"+
		"\2\2$&\3\2\2\2%#\3\2\2\2&\'\b\4\2\2\'\b\3\2\2\2()\7/\2\2)*\7/\2\2*.\3"+
		"\2\2\2+-\n\3\2\2,+\3\2\2\2-\60\3\2\2\2.,\3\2\2\2./\3\2\2\2/\61\3\2\2\2"+
		"\60.\3\2\2\2\61\62\b\5\2\2\62\n\3\2\2\2\63\64\7V\2\2\64\65\7t\2\2\65\66"+
		"\7c\2\2\66\67\7e\2\2\678\7g\2\289\7\"\2\29:\7F\2\2:;\7g\2\2;<\7u\2\2<"+
		"=\7e\2\2=>\7t\2\2>?\7k\2\2?@\7r\2\2@A\7v\2\2AB\7k\2\2BC\7q\2\2CD\7p\2"+
		"\2DE\7<\2\2EI\3\2\2\2FH\n\3\2\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2"+
		"\2J\f\3\2\2\2KI\3\2\2\2LM\7V\2\2MN\7t\2\2NO\7c\2\2OP\7e\2\2PQ\7g\2\2Q"+
		"R\7\"\2\2RS\7V\2\2ST\7{\2\2TU\7r\2\2UV\7g\2\2VW\7<\2\2W[\3\2\2\2XZ\n\3"+
		"\2\2YX\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\\16\3\2\2\2][\3\2\2\2^"+
		"_\7/\2\2_`\7@\2\2`d\3\2\2\2ac\n\3\2\2ba\3\2\2\2cf\3\2\2\2db\3\2\2\2de"+
		"\3\2\2\2e\20\3\2\2\2fd\3\2\2\2gi\t\4\2\2hg\3\2\2\2ij\3\2\2\2jh\3\2\2\2"+
		"jk\3\2\2\2k\22\3\2\2\2\n\2\30#.I[dj\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}