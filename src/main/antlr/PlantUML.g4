grammar PlantUML;

// 解析主规则
plantuml: STARTUML EOL diagram ENDUML EOF|;

diagram: activity_diagram;

activity_diagram: activity_top_transition EOL activity_statement+;

activity_statement: activity_transition EOL;

activity_top_transition: activity_state activity_arrow transition_label? activity_state;

activity_transition: activity_state? activity_arrow transition_label? activity_state;

transition_label: '[' PARAGRAPH ']';

activity_state: identifier | ACTIVITY_START_END;

activity_arrow: '-->' | activity_arrow_dir | activity_arrow_right;

activity_arrow_right: '->';

activity_arrow_dir: '-' ARROW '->';

identifier: short_identifier | long_identifier;

short_identifier: SHORT_IDENTIFIER;

long_identifier: '"' LONG_IDENTIFIER '"';

// 关键字和标识符
STARTUML: '@startuml';
ENDUML: '@enduml';
ARROW: 'up' | 'down' | 'left' | 'right';
ACTIVITY_START_END: '(*)';
PARAGRAPH: [a-zA-Z0-9_]+;
LONG_IDENTIFIER: [a-zA-Z_][a-zA-Z_0-9 ]*[a-zA-Z_0-9];
SHORT_IDENTIFIER: [a-zA-Z_][a-zA-Z_0-9]*;
EOL: '\r'? '\n';

// 忽略空白符
WS: [ \t]+ -> skip;
