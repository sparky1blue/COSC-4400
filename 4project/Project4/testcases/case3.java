class case3 {
    public static void main(String[] args) {
        int a = 5;
        int b = 2;
        int arr = 9;
        boolean t = true;


        a = 10;
        b = a + 1;


        arr[0] = 5;
        arr[a] = b + 1;

    
        {
            a = 1;
            b = 2;
        }


        if (a < b) {
            a = 1;
        }


        if (t) {
            a = 1;
        } else {
            a = 2;
        }


        if (a < b) {
            if (t) {
                a = 1;
            } else {
                a = 2;
            }
        } else {
            a = 3;
        }


        while (a < 10) {
            a = a + 1;
        }

        while (a < b) {
            a = a + 1;
            b = b - 1;
        }

   
        if (t) {
            while (a < 5) {
                a = a + 1;
            }
        }

        Xinu.print(a);
        Xinu.print(b);
        Xinu.print(arr[a]);
    }
}