grammar PlantUML;

plantuml: STARTUML EOL? diagram ENDUML EOF|;

diagram: activity_diagram | class_diagram;

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
    | connector_stmt
    | partition_stmt
    | swimlane_stmt
    | arrow_stmt
    | short_action_stmt
    ;

class_diagram: class_statement*;

class_statement:
      class_declaration EOL?
    | class_relation EOL?
    | note_stmt EOL?
    ;

class_declaration: (CLASS | INTERFACE | ABSTRACT CLASS? | ANNOTATION | CIRCLE | DATACLASS | DIAMOND | ENTITY | ENUM | EXCEPTION | METACLASS | PROTOCOL | RECORD | STEREOTYPE | STRUCT | '()' | '<>') identifier stereotype? ('{' EOL? class_body_element* '}')? ;

class_body_element: paragraph_text EOL?;

class_relation: identifier class_relation_arrow identifier (':' paragraph_text)?;

class_relation_arrow: 
      '--' | '..' | '-->' | '<--' | '..>' | '<..' 
    | '<|--' | '--|>' | '<|..' | '..|>' 
    | '*--' | '--*' | 'o--' | '--o'
    | '-' ARROW '-' | '.' ARROW '.' 
    | '-' ARROW '->' | '<-' ARROW '-' 
    | '.' ARROW '.>' | '<.' ARROW '.' 
    | '<|' '-' ARROW '-' | '-' ARROW '-|>' 
    | '<|' '.' ARROW '.' | '.' ARROW '.|>' 
    | '*' '-' ARROW '-' | '-' ARROW '-*' 
    | 'o' '-' ARROW '-' | '-' ARROW '-o'
    ;

note_stmt: NOTE (RIGHT | LEFT | TOP | BOTTOM | FLOATING)? (OF identifier)? (':' paragraph_text EOL? | EOL? paragraph_text END_NOTE EOL?);

color_spec: COLOR_SPEC;
stereotype: '<<' paragraph_text '>>';
action: color_spec? stereotype? ':' paragraph_text ';' stereotype? EOL?;
short_action_stmt: '-' stereotype? paragraph_text EOL?;

connector_stmt: '(' identifier ')' color_spec? EOL?;
partition_stmt: PARTITION paragraph_text '{' EOL? statement* '}' EOL?;
swimlane_stmt: '|' paragraph_text '|' EOL?;

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

arrow_stmt: activity_arrow transition_label? EOL?;

transition_label: '[' paragraph_text ']';

paragraph_text: (PARAGRAPH | SHORT_IDENTIFIER | LONG_IDENTIFIER | START | STOP | END | IF | THEN | ELSEIF | ELSE | ENDIF | SWITCH | CASE | ENDSWITCH | WHILE | IS | ENDWHILE | REPEAT | REPEAT_WHILE | FORK | FORK_AGAIN | END_FORK | END_MERGE | BREAK | LABEL | GOTO | SPLIT | SPLIT_AGAIN | END_SPLIT | NOTE | END_NOTE | RIGHT | LEFT | TOP | BOTTOM | FLOATING | OF | KILL | DETACH | ARROW | PARTITION | EMOJI | CLASS | INTERFACE | ABSTRACT | ANNOTATION | CIRCLE | DATACLASS | DIAMOND | ENTITY | ENUM | EXCEPTION | METACLASS | PROTOCOL | RECORD | STEREOTYPE | STRUCT | OTHER_CHAR)+;

activity_state: identifier | ACTIVITY_START_END;

arrow_style: ARROW_STYLE;
activity_arrow: '-->' | '--' arrow_style '->' | activity_arrow_dir | activity_arrow_right | activity_line;
activity_arrow_right: '->' | '-' arrow_style '->';
activity_arrow_dir: '-' ARROW '->' | '-' arrow_style ARROW '->';
activity_line: '--' | '-' arrow_style '-';

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

PARTITION: 'partition';

KILL: 'kill';
DETACH: 'detach';

CLASS: 'class';
INTERFACE: 'interface';
ABSTRACT: 'abstract';
ANNOTATION: 'annotation';
CIRCLE: 'circle';
DATACLASS: 'dataclass';
DIAMOND: 'diamond';
ENTITY: 'entity';
ENUM: 'enum';
EXCEPTION: 'exception';
METACLASS: 'metaclass';
PROTOCOL: 'protocol';
RECORD: 'record';
STEREOTYPE: 'stereotype';
STRUCT: 'struct';

UP: 'up';
DOWN: 'down';
ARROW: UP | DOWN | LEFT | RIGHT;
ACTIVITY_START_END: '(*)';

COLOR_SPEC: '#' [a-zA-Z0-9_]+;
ARROW_STYLE: '[' '#' [a-zA-Z0-9_, ]+ ']';
SHORT_IDENTIFIER: [a-zA-Z_][a-zA-Z_0-9]*;
LONG_IDENTIFIER: '"' ~('"'|'\r'|'\n')* '"';
EMOJI: '<:' [a-zA-Z0-9_]+ ':>';

PARAGRAPH: ~('\n'|'\r'|'('|')'|'{'|'}'|';'|'['|']'|':'|' '|'\t'|'|'|'<'|'>')+ ;

EOL: '\r'? '\n';
WS: [ \t]+ -> skip;

OTHER_CHAR: . ;
