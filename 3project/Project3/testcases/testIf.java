class testIf {
    public static void main(String[] a) {
        int x;
        int y;
        boolean t;
 
        x = 5;
        y = 2;
        t = true;
 
        
        x = -1;
        x = -y;
 

        x = 10 / 2;
        x = 10 / y;
 

        Xinu.print(x > y);
 

        Xinu.print(x == y);
        Xinu.print(x != y);
 

        Xinu.print(null);
 

        Xinu.print(x + 1 == y * 2);
        Xinu.print(x - 1 != y / 2);
        Xinu.print(-x > -y);
        Xinu.print(!(x == y) && (x > y));
    }
}