import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Scanner{

    public final boolean DEBUG = false;

    public enum CharType
    {LETTER, DIGIT, OTHER};

    public CharType characterClass[] = new CharType[256];
    
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
            case ACCEPT:return type +"(" + lexeme + ")";

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