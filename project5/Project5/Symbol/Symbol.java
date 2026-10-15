package Symbol;

import java.util.HashMap;
import java.util.Map;

public class Symbol {
    private final String name;
    private Symbol(String name) {this.name = name;}

    private static final Map<String, Symbol> dict = new HashMap<String, Symbol>();

    public String toString() {return name;}

    public static Symbol symbol(String n){
        Symbol s = dict.get(n);
        if (s == null){
            s = new Symbol(n);
            dict.put(n,s);
        }
        return s;
    }
}
