grammar NusmvResult;

trace : TRACE_DESCRIPTION TRACE_TYPE state*;

state : STATE assignment*;

assignment : variable=ID '=' value=ID;

WS : [ \t\r\n]+ -> skip;

COMMENT_STAR : '***' ~[\r\n]* -> skip;

COMMENT_LINE : '--' ~[\r\n]* -> skip;

TRACE_DESCRIPTION : 'Trace Description:' ~[\r\n]*;

TRACE_TYPE : 'Trace Type:' ~[\r\n]*;

STATE : '->' ~[\r\n]*;

ID : [_a-zA-Z0-9]+;
