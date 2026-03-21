grammar PlantUML;

plantuml: STARTUML EOL? diagram ENDUML EOF|;

diagram: activity_diagram;

activity_diagram: statement*;

statement:
      action
    | conditional
    | switch_stmt
    | loop_while
    | loop_repeat
    | parallel
    | keyword_stmt
    | legacy_transition
    | note_stmt
    | break_stmt
    | label_stmt
    | goto_stmt
    | split_stmt
    ;

note_stmt: NOTE (RIGHT | LEFT | TOP | BOTTOM | FLOATING)? (OF identifier)? (':' paragraph_text EOL? | EOL? paragraph_text END_NOTE EOL?);

action: ':' paragraph_text ';' EOL?;

conditional: IF '(' paragraph_text ')' THEN ('(' paragraph_text ')')? EOL?
             statement*
             (ELSEIF '(' paragraph_text ')' THEN ('(' paragraph_text ')')? EOL?
             statement* )*
             (ELSE ('(' paragraph_text ')')? EOL?
             statement* )?
             ENDIF EOL?;

switch_stmt: SWITCH '(' paragraph_text ')' EOL? case_stmt* ENDSWITCH EOL?;
case_stmt: CASE '(' paragraph_text ')' EOL? statement*;

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
          (END_FORK | END_MERGE) EOL?;

break_stmt: BREAK EOL?;
label_stmt: LABEL identifier EOL?;
goto_stmt: GOTO identifier EOL?;

split_stmt: SPLIT EOL?
            statement*
            (SPLIT_AGAIN EOL?
            statement*)*
            END_SPLIT EOL?;

keyword_stmt: (START | STOP | END | KILL | DETACH) EOL?;

legacy_transition: activity_state activity_arrow transition_label? activity_state EOL?
                 | activity_arrow transition_label? activity_state EOL?; 

transition_label: '[' paragraph_text ']';

paragraph_text: (PARAGRAPH | SHORT_IDENTIFIER | LONG_IDENTIFIER | START | STOP | END | IF | THEN | ELSEIF | ELSE | ENDIF | SWITCH | CASE | ENDSWITCH | WHILE | IS | ENDWHILE | REPEAT | REPEAT_WHILE | FORK | FORK_AGAIN | END_FORK | END_MERGE | BREAK | LABEL | GOTO | SPLIT | SPLIT_AGAIN | END_SPLIT | NOTE | END_NOTE | RIGHT | LEFT | TOP | BOTTOM | FLOATING | OF | KILL | DETACH | ARROW | OTHER_CHAR)+;

activity_state: identifier | ACTIVITY_START_END;

activity_arrow: '-->' | activity_arrow_dir | activity_arrow_right;
activity_arrow_right: '->';
activity_arrow_dir: '-' ARROW '->';

identifier: short_identifier | long_identifier;
short_identifier: SHORT_IDENTIFIER;
long_identifier: LONG_IDENTIFIER;

// Tokens
STARTUML: '@startuml';
ENDUML: '@enduml';

START: 'start';
STOP: 'stop';
END: 'end';

IF: 'if';
THEN: 'then';
ELSEIF: 'elseif';
ELSE: 'else';
ENDIF: 'endif';

SWITCH: 'switch';
CASE: 'case';
ENDSWITCH: 'endswitch';

WHILE: 'while';
IS: 'is';
ENDWHILE: 'endwhile';

REPEAT: 'repeat';
REPEAT_WHILE: 'repeat while';

FORK: 'fork';
FORK_AGAIN: 'fork again';
END_FORK: 'end fork';
END_MERGE: 'end merge';

SPLIT: 'split';
SPLIT_AGAIN: 'split again';
END_SPLIT: 'end split';

BREAK: 'break';
LABEL: 'label';
GOTO: 'goto';

NOTE: 'note';
END_NOTE: 'end note';
RIGHT: 'right';
LEFT: 'left';
TOP: 'top';
BOTTOM: 'bottom';
FLOATING: 'floating';
OF: 'of';

KILL: 'kill';
DETACH: 'detach';

UP: 'up';
DOWN: 'down';
ARROW: UP | DOWN | LEFT | RIGHT;
ACTIVITY_START_END: '(*)';

SHORT_IDENTIFIER: [a-zA-Z_][a-zA-Z_0-9]*;
LONG_IDENTIFIER: '"' ~('"'|'\r'|'\n')* '"';

PARAGRAPH: ~('\n'|'\r'|'('|')'|';'|'['|']'|':'|' '|'\t')+ ;

EOL: '\r'? '\n';
WS: [ \t]+ -> skip;

OTHER_CHAR: . ;
