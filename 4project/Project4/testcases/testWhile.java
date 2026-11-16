class testWhile {
    public static void main(String[] args) {
        int a;
        int b;
        boolean t;

        a = 0;
        b = 10;
        t = true;

        while (a < 10) {
            a = a + 1;
        }


        while (a < b) {
            a = a + 1;
            b = b - 1;
        }


        while (a < 20)
            a = a + 1;


        if (t) {
            while (a < 5) {
                a = a + 1;
            }
        }

        while (a < 30) {
            if (a < 25) {
                a = a + 2;
            } else {
                a = a + 1;
            }
        }

        Xinu.print(a);
        Xinu.print(b);
    }
}