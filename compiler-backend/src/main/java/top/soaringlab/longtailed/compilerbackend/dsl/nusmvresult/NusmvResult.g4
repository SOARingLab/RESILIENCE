grammar NusmvResult;

states : state*;

state : STATE assignment*;

assignment : variable=ID '=' value=ID;

WS : [ \t\r\n]+ -> skip;

COMMENT_STAR : '***' ~[\r\n]* -> skip;

COMMENT_LINE : '--' ~[\r\n]* -> skip;

TRACE : 'Trace' ~[\r\n]* -> skip;

STATE : '->' ~[\r\n]*;

ID : [_a-zA-Z0-9]+;
