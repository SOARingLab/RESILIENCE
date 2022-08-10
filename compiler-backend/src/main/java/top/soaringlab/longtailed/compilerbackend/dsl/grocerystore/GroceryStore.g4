grammar GroceryStore;

inputs: input*;

input: condition+ action+;

condition: operator=('WHEN'|'AND'|'OR') ('region') predicate=('in'|'not in') object=('low-risk region'|'medium-risk region'|'high-risk region');

action: 'SET' ('delivery method') ('to') object=('home delivery'|'contactless locker'|'at store');

WS: [ \t\r\n]+ -> skip;
