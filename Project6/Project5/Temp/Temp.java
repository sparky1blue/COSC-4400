package Temp;

public class Temp {
    private static int count = 32;
    private int num;

    public Temp(){
        num = count++;
    }
    public Temp(int n) {
        num = n;
    }
    public String toString(){
        return "t" + num;
    }
}
