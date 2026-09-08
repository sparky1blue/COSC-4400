import java.io.BufferedReader;
import java.io.InputStreamReader;



public class Scanner{

    public final boolean DEBUG = false;

    public enum CharType
    {LETTER, DIGIT, WHITESPACE, PLUS, MINUS, STAR, FORWARDSLASH, LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON, COMMA, LESSTHAN, GREATERTHAN, ASSIGN, OTHER, BANG, QOUTE, ZERO};

    public CharType characterClass[] = new CharType[256];
    
    
    public enum State
    {START, IDBUILDING, ACCEPT, ERROR, NUMBUILDING, STRINGBUILDING, OPBUILDER, PUNC, EQUALS, AND, OR, ZERO};

    // == && || 

    //LETTER, DIGIT, WHITESPACE, PLUS, MINUS, STAR, FORWARDSLASH, LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON, COMMA, LESSTHAN, GREATERTHAN, ASSIGN, OTHER, QOUTE

    /*
    START
    IDBULIDING
    ACCEPT
    ERROR
    NUMBUILDING
    STRINGBUILDER
    OPBUILDER 
    EQUALS
    AND
    OR*/


    //can start with an int lit
    public State next_state[][] =
    { {State.IDBUILDING, State.NUMBUILDING, State.START, State.OPBUILDER, State.OPBUILDER, State.OPBUILDER, State.OPBUILDER, State.OPBUILDER, State.OPBUILDER, State.OPBUILDER, State.OPBUILDER, State.PUNC, State.PUNC, State.OPBUILDER, State.OPBUILDER, State.EQUALS, State.ACCEPT, State.STRINGBUILDING },
      {State.IDBUILDING, State.IDBUILDING, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT,State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT, State.ACCEPT,},
      {State.ACCEPT, State.ACCEPT, State.ACCEPT},
      {State.ERROR, State.ERROR, State.ERROR},
      {State.IDBUILDING, State.NUMBUILDING, State.ACCEPT}
      
    };

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

    }
    
    //make var to store the next character in 
    /*private int prev; // holds previous value across calls

public int reader() throws IOException {
    int temp = prev;
    prev = in.read(); // 'in' is your actual reader object, e.g. BufferedReader
    return temp;
}
} */
    
    
    //make ishex()
   
    

    public String getCoin (java.io.Reader reader) throws java.io.IOException
    {
        State state = State.START;
        int c = reader.read ();

        String lexeme = "";
        String type = "";

        while (-1 != c)
        {
            CharType charClass = characterClass[c];
            if (DEBUG) System.out.print ("state = " + state + ", class = " + charClass);
            
            state = next_state[state.ordinal ()][charClass.ordinal ()];
            if (DEBUG) System.out.println (" ==> state = " + state);
            
            switch (state)
            {
            case IDBUILDING:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "ID";
                break;
            case NUMBUILDING:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "int";
                break;
            case ZERO:
                lexeme = lexeme + (char) c;      
                c = reader.read ();
                if (c == 'x' || c == 'X') {
                    lexeme = lexeme + (char) c;
                    c = reader.read ();
                    if (!isHex(c)) {
                        return "Invalid character in hex number.";
                    }
                    state = State.HEXBUILDING;   // carried into next loop iteration's table lookup
                } else if (c == '8' || c == '9') {
                    return "Invalid character in octal number.";
                }
                break;
            case OCTALBUILDING:
                    if (c == '8' || c == '9') {
                        return "Invalid character in octal number."; //fix
                    }
                    lexeme = lexeme + (char) c;
                    c = reader.read ();
                    break;
            case ACCEPT:
                    if (c != -1) {
                        reader.unread (c);
                    }
                    //make type function 
                    return type (lexeme) + " " + lexeme;

                case ERROR:
                    return "Illegal token.";

                default:
                    System.err.println ("ERROR: Reached wrong state " + state);
                    return "Illegal token.";
            }
        }
        return "EOF";
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
