package Temp;

import Symbol.Symbol;

public class Label {
    private static int count = 0;
    private String name;

    public Label(String n){
        name = n;
    }
    public Label(){
        this("L" + count++);
    }

    public Label(Symbol s){
        this(s.toString());
    }
    public String toString(){
        return name;
    }
}
