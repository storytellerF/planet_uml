grammar PlantUML;

plantuml: STARTUML EOL? diagram ENDUML EOF|;

diagram: activity_diagram;

activity_diagram: statement*;

statement:
      action
    | conditional
    | loop_while
    | loop_repeat
    | parallel
    | keyword_stmt
    | legacy_transition
    ;

action: ':' paragraph_text ';' EOL?;

conditional: IF '(' paragraph_text ')' THEN ('(' paragraph_text ')')? EOL?
             statement*
             (ELSE ('(' paragraph_text ')')? EOL?
             statement* )?
             ENDIF EOL?;

loop_while: WHILE '(' paragraph_text ')' (IS '(' paragraph_text ')')? EOL?
            statement*
            ENDWHILE ('(' paragraph_text ')')? EOL?;

loop_repeat: REPEAT EOL?
             statement*
             REPEAT_WHILE '(' paragraph_text ')' (IS '(' paragraph_text ')')? EOL?;

parallel: FORK EOL?
          statement*
          (FORK_AGAIN EOL?
          statement*)*
          END_FORK EOL?;

keyword_stmt: (START | STOP | END) EOL?;

legacy_transition: activity_state activity_arrow transition_label? activity_state EOL?
                 | activity_arrow transition_label? activity_state EOL?; 

transition_label: '[' paragraph_text ']';

paragraph_text: (PARAGRAPH | SHORT_IDENTIFIER | LONG_IDENTIFIER | START | STOP | END | IF | THEN | ELSE | ENDIF | WHILE | IS | ENDWHILE | REPEAT | REPEAT_WHILE | FORK | FORK_AGAIN | END_FORK | ARROW | OTHER_CHAR)+;

activity_state: identifier | ACTIVITY_START_END;

activity_arrow: '-->' | activity_arrow_dir | activity_arrow_right;
activity_arrow_right: '->';
activity_arrow_dir: '-' ARROW '->';

identifier: short_identifier | long_identifier;
short_identifier: SHORT_IDENTIFIER;
long_identifier: '"' LONG_IDENTIFIER '"';

// Tokens
STARTUML: '@startuml';
ENDUML: '@enduml';

START: 'start';
STOP: 'stop';
END: 'end';

IF: 'if';
THEN: 'then';
ELSE: 'else';
ENDIF: 'endif';

WHILE: 'while';
IS: 'is';
ENDWHILE: 'endwhile';

REPEAT: 'repeat';
REPEAT_WHILE: 'repeat while';

FORK: 'fork';
FORK_AGAIN: 'fork again';
END_FORK: 'end fork';

ARROW: 'up' | 'down' | 'left' | 'right';
ACTIVITY_START_END: '(*)';

SHORT_IDENTIFIER: [a-zA-Z_][a-zA-Z_0-9]*;
LONG_IDENTIFIER: [a-zA-Z_][a-zA-Z_0-9 ]*[a-zA-Z_0-9];

PARAGRAPH: ~('\n'|'\r'|'('|')'|';'|'['|']'|':'|' '|'\t')+ ;

EOL: '\r'? '\n';
WS: [ \t]+ -> skip;

OTHER_CHAR: . ;
