grammar ExpressionLanguage;

input : '$' '{' condition '}';

condition
    : operand1=expression operator=('=='|'!='|'<'|'<='|'>'|'>=') operand2=expression # comparison
    | '!' operand1=condition # negation
    | operand1=condition '&&' operand2=condition # conjunction
    | operand1=condition '||' operand2=condition # disjunction
    ;

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
