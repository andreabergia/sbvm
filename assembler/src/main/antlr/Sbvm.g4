grammar Sbvm;

// A program is a sequence of lines
program: line*;

// A line is either a label, or an instruction, followed by a newline
line: (label | instruction | emptyLine) NEWLINE;

emptyLine: ;

// Labels are simply identifiers, followed by colons
label: IDENTIFIER ':';

// An instruction can be of many kinds
instruction: halt |
             push |
             add |
             sub |
             mul |
             div |
             not |
             and |
             or |
             pop |
             dup |
             iseq |
             isge |
             isgt |
             jmp |
             jif |
             load |
             store |
             call |
             ret
             ;
halt: 'HALT';
push: 'PUSH' NUMBER;
add: 'ADD';
sub: 'SUB';
mul: 'MUL';
div: 'DIV';
not: 'NOT';
and: 'AND';
or: 'OR';
pop: 'POP';
dup: 'DUP';
iseq: 'ISEQ';
isge: 'ISGE';
isgt: 'ISGT';
jmp: 'JMP' IDENTIFIER;
jif: 'JIF' IDENTIFIER;
load: 'LOAD' NUMBER;
store: 'STORE' NUMBER;
call: 'CALL' IDENTIFIER;
ret: 'RET';


IDENTIFIER: [a-zA-Z][a-zA-Z0-9_]*;
NUMBER: [0-9]+;
NEWLINE: '\r'? '\n';

// Skip all whitespaces
WHITESPACE: [ \t]+ -> skip;

// Comments
COMMENT: '//' ~('\r' | '\n')* -> skip;
