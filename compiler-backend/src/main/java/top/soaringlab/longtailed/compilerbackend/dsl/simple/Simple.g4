grammar Simple;

inputs : input*;

input : condition* action+;

condition : logical=('WHEN'|'AND'|'OR') operand1=ID operator=('=='|'!='|'<'|'<='|'>'|'>=') operand2=expression;

action : 'SET' operand1=ID '=' operand2=expression;

expression
    : '(' expression ')' # parenthesis
    | NUMBER # number
    | STRING # string
    | ID # id
    | operator=('+'|'-') expression # positiveNegative
    | expression operator=('*'|'/') expression # multiplicationDivision
    | expression operator=('+'|'-') expression # additionSubtraction
    ;

NUMBER
    : [0-9]+
    | [0-9]+ '.' [0-9]+
    ;

STRING : '"' ('\\"'|'\\\\'|.)*? '"';

ID : [_a-zA-Z][_a-zA-Z0-9]*;

WS : [ \t\r\n]+ -> skip;
