import java.io.BufferedReader;
import java.io.InputStreamReader;



public class Scanner{

    public final boolean DEBUG = false;

    public enum CharType
    {LETTER, DIGIT, WHITESPACE, PLUS, MINUS, TIMES, DIVIDE, LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON, COMMA, LESSTHAN, GREATERTHAN, ASSIGN, OTHER};

    public CharType characterClass[] = new CharType[256];
    
    //ADD ASIGN
    public enum State
    {START, IDBUILDING, ACCEPT, ERROR, NUMBUILDING};

    //can start with an int lit
    public State next_state[][] =
    { {State.IDBUILDING, State.NUMBUILDING, State.ERROR},
      {State.IDBUILDING, State.IDBUILDING, State.ACCEPT},
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
        characterClass['*'] = CharType.TIMES;
        characterClass['/'] = CharType.DIVIDE;
        characterClass['('] = CharType.LPAREN;
        characterClass[')'] = CharType.RPAREN;
        characterClass['{'] = CharType.LBRACE;
        characterClass['}'] = CharType.RBRACE;
        characterClass[';'] = CharType.SEMICOLON;
        characterClass[','] = CharType.COMMA;
        characterClass['<'] = CharType.LESSTHAN;
        characterClass['>'] = CharType.GREATERTHAN;
    }
    
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
            case ASSIGN:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "ASSIGN";
                break;
            case PLUS:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "PLUS";
                break;
            case MINUS:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "MINUS";
                break;
            case STAR:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "TIMES";
                break;
            case FORWARDSLASH:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "DIVIDE";
                break;
            case LPAREN:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "LPAREN";
                break;
            case RPAREN:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "RPAREN";
                break;
            case LBRACE:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "LBRACE";
                break;
            case RBRACE:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "RBRACE";
                break;
            case SEMICOLON:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "SEMICOLON";
                break;
            case COMMA:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "COMMA";
                break;
            case LESSTHAN:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "LESSTHAN";
                break;
            case GREATERTHAN:
                lexeme = lexeme + (char) c;
                c = reader.read ();
                type = "GREATERTHAN";
                break;

            case ACCEPT:
                return type +"(" + lexeme + ")";

            case ERROR:return "ERROR_COIN";
            default:System.err.println ("ERROR: Reached wrong state " + state);
                return "ERROR_Coin";
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
