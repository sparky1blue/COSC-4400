import java.io.BufferedReader;
import java.io.InputStreamReader;



public class Scanner{

    public final boolean DEBUG = false;

    public enum CharType
    {LETTER, HEXLETTER, EX, DIGIT, ZERO, WHITESPACE, PLUS, MINUS, STAR,
     FORWARDSLASH, LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON, COMMA,
     LESSTHAN, GREATERTHAN, ASSIGN, OTHER, BANG, QOUTE, AMPERSAND, PIPE};



    public CharType characterClass[] = new CharType[256];
    
    
    public enum State
    {START, IDBUILDING, ACCEPT, ERROR, NUMBUILDING, STRINGBUILDING, OPBUILDER,
     PUNC, EQUALS, AND, OR, ZERO, HEXSTART, HEXBUILDING, OCTALBUILDING};
    
    private static final java.util.Set<String> RESERVED = new java.util.HashSet<>(
    java.util.Arrays.asList("class", "public", "static", "void", "main",
                             "String", "extends", "return", "int", "Boolean",
                             "if", "else", "while", "true", "false", "for"));



    //claude code
    
    public State next_state[][] = buildNextStateTable();

    private State[][] buildNextStateTable() {
        int numStates = State.values().length;
        int numCharTypes = CharType.values().length;
        State[][] table = new State[numStates][numCharTypes];

        for (State[] row : table) {
            java.util.Arrays.fill(row, State.ERROR);
        }

        // ================= START =================
        // No characters consumed yet.
        set(table, State.START, CharType.LETTER,       State.IDBUILDING);   // begins an identifier/keyword
        set(table, State.START, CharType.HEXLETTER,    State.IDBUILDING);   // a-f/A-F can also START an identifier (e.g. "cab", "deadCode")
        set(table, State.START, CharType.EX,           State.IDBUILDING);   // x/X can also START an identifier (e.g. "xValue")
        set(table, State.START, CharType.DIGIT,        State.NUMBUILDING);  // begins a nonzero decimal integer literal
        set(table, State.START, CharType.ZERO,         State.ZERO);         // begins a literal starting with '0' (decimal 0, octal, or hex)
        set(table, State.START, CharType.WHITESPACE,   State.START);        // whitespace is skipped, stay in START
        set(table, State.START, CharType.QOUTE,        State.STRINGBUILDING); // opening quote begins a string literal

        set(table, State.START, CharType.PLUS,         State.OPBUILDER);    // '+' -> "+" or "+="
        set(table, State.START, CharType.MINUS,        State.OPBUILDER);    // '-' -> "-" or "-="
        set(table, State.START, CharType.STAR,         State.OPBUILDER);    // '*' -> "*" or "*="
        set(table, State.START, CharType.FORWARDSLASH, State.OPBUILDER);    // '/' -> "/" or "/="
        set(table, State.START, CharType.LESSTHAN,     State.OPBUILDER);    // '<' -> "<" or "<="
        set(table, State.START, CharType.GREATERTHAN,  State.OPBUILDER);    // '>' -> ">" or ">="
        set(table, State.START, CharType.BANG,         State.OPBUILDER);    // '!' -> "!" or "!="

        set(table, State.START, CharType.ASSIGN,       State.EQUALS);       // '=' -> "=" or "=="

        set(table, State.START, CharType.AMPERSAND,    State.AND);          // '&' -> "&&" only
        set(table, State.START, CharType.PIPE,         State.OR);           // '|' -> "||" only

        set(table, State.START, CharType.LPAREN,       State.PUNC);         // '('
        set(table, State.START, CharType.RPAREN,       State.PUNC);         // ')'
        set(table, State.START, CharType.LBRACE,       State.PUNC);         // '{'
        set(table, State.START, CharType.RBRACE,       State.PUNC);         // '}'
        set(table, State.START, CharType.SEMICOLON,    State.PUNC);         // ';'
        set(table, State.START, CharType.COMMA,        State.PUNC);         // ','

        // ================= IDBUILDING =================
        // Accepts: identifiers and keywords, e.g. "foo", "cab", "xValue", "if"
        set(table, State.IDBUILDING, CharType.LETTER,    State.IDBUILDING);
        set(table, State.IDBUILDING, CharType.HEXLETTER, State.IDBUILDING); // a-f/A-F valid mid-identifier
        set(table, State.IDBUILDING, CharType.EX,        State.IDBUILDING); // x/X valid mid-identifier
        set(table, State.IDBUILDING, CharType.DIGIT,     State.IDBUILDING);
        set(table, State.IDBUILDING, CharType.ZERO,      State.IDBUILDING);

        // ================= ZERO =================
        // A literal that started with '0'. On its own accepts decimal "0".
        // Can branch into octal (more digits) or hex ("0x"/"0X").
        set(table, State.ZERO, CharType.DIGIT, State.OCTALBUILDING); // "0" + digit -> octal
        set(table, State.ZERO, CharType.ZERO,  State.OCTALBUILDING); // "0" + "0"   -> octal
        set(table, State.ZERO, CharType.EX,    State.HEXBUILDING);   // "0x" -> hex literal begins
        // anything else: back up, accept "0" as a decimal zero literal

        // ================= NUMBUILDING =================
        // Accepts: decimal integer literals, e.g. "123", "42"
        set(table, State.NUMBUILDING, CharType.DIGIT, State.NUMBUILDING);
        set(table, State.NUMBUILDING, CharType.ZERO,  State.NUMBUILDING);

        // ================= OCTALBUILDING =================
        // Accepts: octal integer literals, e.g. "017", "0123"
        set(table, State.OCTALBUILDING, CharType.DIGIT, State.OCTALBUILDING);
        set(table, State.OCTALBUILDING, CharType.ZERO,  State.OCTALBUILDING);
        // NOTE: this accepts 8/9 too. For strict octal (digits 0-7 only),
        // split CharType.DIGIT into DIGIT (1-7) and DIGIT89 (8,9), and
        // don't wire DIGIT89 here -> hitting an 8/9 would then correctly
        // fall to ERROR/ACCEPT instead of silently accepting bad octal.

        // ================= HEXBUILDING =================
        // Accepts: hex integer literals, e.g. "0x1A", "0xFF", "0xdeadbeef"
        set(table, State.HEXBUILDING, CharType.DIGIT,     State.HEXBUILDING); // 1-9
        set(table, State.HEXBUILDING, CharType.ZERO,      State.HEXBUILDING); // 0
        set(table, State.HEXBUILDING, CharType.HEXLETTER, State.HEXBUILDING); // a-f, A-F
        // NOTE: does NOT accept EX again ("0xx1" is invalid) or LETTER
        // (g-z are not valid hex digits) -- both correctly fall to ERROR

        // ================= STRINGBUILDING =================
        // Accepts: string literals, e.g. "hello world"
        for (CharType ct : CharType.values()) {
            set(table, State.STRINGBUILDING, ct, State.STRINGBUILDING);
        }
        set(table, State.STRINGBUILDING, CharType.QOUTE, State.ACCEPT); // closing quote completes it

        // ================= OPBUILDER =================
        // A single-char operator that might extend into a compound one:
        // "+=", "-=", "*=", "/=", "<=", ">=", "!="
        set(table, State.OPBUILDER, CharType.ASSIGN, State.ACCEPT);

        // ================= EQUALS =================
        // A single '=' seen so far -> "=" alone, or "==" if extended
        set(table, State.EQUALS, CharType.ASSIGN, State.ACCEPT);

        // ================= AND =================
        // A single '&' seen so far -> only accepted as "&&"
        set(table, State.AND, CharType.AMPERSAND, State.ACCEPT);

        // ================= OR =================
        // A single '|' seen so far -> only accepted as "||"
        set(table, State.OR, CharType.PIPE, State.ACCEPT);

        // ================= PUNC =================
        // Accepts: single-char punctuation "(" ")" "{" "}" ";" ","
        // Nothing extends a PUNC token; driver treats PUNC as accepting.

        return table;
    }

    private void set(State[][] table, State from, CharType on, State to) {
        table[from.ordinal()][on.ordinal()] = to;
    }

    public Scanner ()
    {
        for (int i = 0; i < characterClass.length; i++)
            characterClass[i] = CharType.OTHER;
        for (int i = 'A'; i <= 'Z'; i++)
            characterClass[i] = CharType.LETTER;
        for (int i = 'a'; i <= 'z'; i++)
            characterClass[i] = CharType.LETTER;
        for (int i = '0'; i <= '9'; i++)
            characterClass[i] = CharType.DIGIT;

        // overrides -- must come after the loops above
        characterClass['0'] = CharType.ZERO;

        for (int i = 'a'; i <= 'f'; i++)
            characterClass[i] = CharType.HEXLETTER;
        for (int i = 'A'; i <= 'F'; i++)
            characterClass[i] = CharType.HEXLETTER;

        characterClass['x'] = CharType.EX;
        characterClass['X'] = CharType.EX;

        characterClass['+'] = CharType.PLUS;
        characterClass['-'] = CharType.MINUS;
        characterClass['*'] = CharType.STAR;
        characterClass['/'] = CharType.FORWARDSLASH;
        characterClass['('] = CharType.LPAREN;
        characterClass[')'] = CharType.RPAREN;
        characterClass['{'] = CharType.LBRACE;
        characterClass['}'] = CharType.RBRACE;
        characterClass[';'] = CharType.SEMICOLON;
        characterClass[','] = CharType.COMMA;
        characterClass['<'] = CharType.LESSTHAN;
        characterClass['>'] = CharType.GREATERTHAN;
        characterClass['!'] = CharType.BANG;
        characterClass['='] = CharType.ASSIGN;
        characterClass['"'] = CharType.QOUTE;
        characterClass['&'] = CharType.AMPERSAND;
        characterClass['|'] = CharType.PIPE;
        characterClass[' ']  = CharType.WHITESPACE;
        characterClass['\t'] = CharType.WHITESPACE;
        characterClass['\n'] = CharType.WHITESPACE;
        characterClass['\r'] = CharType.WHITESPACE;
    }
    private int prev;
    private int temp;
    
    //fix
    private boolean isHex(int c) {
        if (c == -1){
            return false;
        } 
        CharType ct = characterClass[c];
        return ct == CharType.DIGIT || ct == CharType.ZERO || ct == CharType.HEXLETTER;
    }
    private boolean pushedBack = false;

    public int buffer(java.io.Reader reader) throws java.io.IOException{
        if (pushedBack) {
            pushedBack = false;
            return temp; // re-deliver the same char instead of reading a new one
        }
        prev = temp;
        temp = reader.read();
        return temp;
    }

    public void pushback() {
        pushedBack = true;
    }

    private String compoundOpName(String lexeme) {
        switch (lexeme) {
            case "+=": return "PLUSEQUALS";
            case "-=": return "MINUSEQUALS";
            case "*=": return "STAREQUALS";
            case "/=": return "DIVIDEEQUALS";
            case "<=": return "LESSEQUAL";
            case ">=": return "GREATEREQUAL";
            case "!=": return "NOTEQUAL";
            default:   return "OP(" + lexeme + ")";
        }
    }


    private String checkReserved(String lexeme) {
        if (RESERVED.contains(lexeme)) {
            return lexeme.toUpperCase(); // e.g. "int" -> "INT"
        }
        return "ID(" + lexeme + ")";
    }

    private String singleOpName(char c) {
        switch (c) {
            case '+': return "PLUS";
            case '-': return "MINUS";
            case '*': return "STAR";
            case '/': return "DIVIDE";
            case '<': return "LESSTHAN";
            case '>': return "GREATERTHAN";
            case '!': return "BANG";
            case '=': return "ASSIGN";
            default:  return "OP";
        }
    }

    private String puncName(char c) {
        switch (c) {
            case '(': return "LPAREN";
            case ')': return "RPAREN";
            case '{': return "LBRACE";
            case '}': return "RBRACE";
            case ';': return "SEMICOLON";
            case ',': return "COMMA";
            default:  return "PUNC";
        }
    }
   
    

    public String getCoin (java.io.Reader reader) throws java.io.IOException
    {
        State state = State.START;
        State prevState = State.START;
        int c = buffer(reader);

        String lexeme = "";
        String type = "";

        while (true)
        {
            if (c == -1) {
                if (lexeme.isEmpty()) {
                    return "EOF";
                }
                if (isAcceptable(state)) {
                    return finishToken(state, lexeme);
                }
                if (state == State.STRINGBUILDING) {
                    return "Illegal token."; // unterminated string literal at EOF
                }
                return "EOF";
            }

            CharType charClass = characterClass[c];
            if (DEBUG) System.out.print ("state = " + state + ", class = " + charClass);

            prevState = state;
            state = next_state[state.ordinal()][charClass.ordinal()];
            if (DEBUG) System.out.println (" ==> state = " + state);

            switch (state){
                case START:
                    // whitespace — consume and keep looping, nothing to build yet
                    c = buffer(reader);
                    break;

                case IDBUILDING:
                    lexeme = lexeme + (char) c;
                    c = buffer(reader);
                    break;

                case NUMBUILDING:
                    lexeme = lexeme + (char) c;
                    c = buffer(reader);
                    break;

                case ZERO:
                    lexeme = lexeme + (char) c;
                    c = buffer(reader);
                    if (c == 'x' || c == 'X') {
                        lexeme = lexeme + (char) c;
                        c = buffer(reader);
                        if (!isHex(c)) {
                            return "Invalid character in hex number.";
                        }
                        state = State.HEXBUILDING;
                    } else if (c == '8' || c == '9') {
                        return "Invalid character in octal number.";
                    }
                    break;

                case OCTALBUILDING:
                    if (c == '8' || c == '9') {
                        return "Invalid character in octal number.";
                    }
                    lexeme = lexeme + (char) c;
                    c = buffer(reader);
                    break;

                case HEXBUILDING:
                    lexeme = lexeme + (char) c;
                    c = buffer(reader);
                    break;

                case STRINGBUILDING:
                    // don't store the opening quote itself in the lexeme (per D)
                    if (c != '"') {
                        lexeme = lexeme + (char) c;
                    }
                    c = buffer(reader);
                    break;

                case OPBUILDER:
                    lexeme = lexeme + (char) c;
                    c = buffer(reader);
                    break;

                case EQUALS:
                    lexeme = lexeme + (char) c;
                    c = buffer(reader);
                    break;

                case AND:
                    lexeme = lexeme + (char) c;
                    c = buffer(reader);
                    break;

                case OR:
                    lexeme = lexeme + (char) c;
                    c = buffer(reader);
                    break;

                case PUNC:
                    // single-char, done immediately, no lookahead consumption needed
                    return puncName((char) c);

                case ACCEPT:
                    if (prevState == State.STRINGBUILDING) {
                        // closing quote terminates the string but isn't part of its value
                        return "STRING_LITERAL(" + lexeme + ")";
                    }

                    lexeme = lexeme + (char) c; // this char (=, =, &, or |) completes the token

                    switch (prevState) {
                        case OPBUILDER:
                            return compoundOpName(lexeme);
                        case EQUALS:
                            return "EQUAL";       // "=="
                        case AND:
                            return "AND";         // "&&"
                        case OR:
                            return "OR";          // "||"
                    default:
                        System.err.println("ERROR: unexpected prevState before ACCEPT: " + prevState);
                        return "Illegal token.";
                    }

                case ERROR:
                    if (isAcceptable(prevState) && !lexeme.isEmpty()) {
                        // token actually ended one char ago -- back up and accept
                        pushback();
                        return finishToken(prevState, lexeme);
                    }
                    return "Illegal token.";

                default:
                    System.err.println ("ERROR: Reached wrong state " + state);
                    return "Illegal token.";
            }
        }
    }

    // States where running into ERROR on the *next* char just means
    // "the token is finished," not "this is invalid."
    private boolean isAcceptable(State s) {
        switch (s) {
            case IDBUILDING:
            case NUMBUILDING:
            case ZERO:
            case OCTALBUILDING:
            case HEXBUILDING:
            case OPBUILDER:
            case EQUALS:
                return true;
            default:
                return false; // AND, OR, START, etc. -- lone '&'/'|' really is illegal
        }
    }

    private String finishToken(State s, String lexeme) {
        switch (s) {
            case IDBUILDING:
                return checkReserved(lexeme);
            case NUMBUILDING:
            case ZERO:
            case OCTALBUILDING:
            case HEXBUILDING:
                return "INTEGER_LITERAL(" + lexeme + ")";
            case STRINGBUILDING:
                return "STRING_LITERAL(" + lexeme + ")";
            case OPBUILDER:
            case EQUALS:
                return singleOpName(lexeme.charAt(0));
            default:
                return "Illegal token.";
        }
    }





    public static void main (String[]args)throws java.io.IOException{
        java.io.Reader reader = null;
        Scanner r = new Scanner ();
        
        reader = new java.io.BufferedReader (new java.io.InputStreamReader (System.in));

        String coin;

        do{
           coin = r.getCoin(reader);
           System.out.println(coin);

        }while(!coin.equals("EOF"));   

    }
}
