class Hello {
    public static void main(String[] args) {
        int a = 5;
        int b = 2;
        boolean t = true;
        boolean f = false;
        int arr = 9;


        Xinu.print(4);
        Xinu.print("hello");
        Xinu.print(true);
        Xinu.print(false);

        Xinu.print(a);
        Xinu.print(t);


        Xinu.print(this);


        Xinu.print(!t);
        Xinu.print(!f);

 
        Xinu.print(a * b);
        Xinu.print(a * b * 3);

  
        Xinu.print(a + b);
        Xinu.print(a - b);
        Xinu.print(a + b * 3);
        Xinu.print(a - b * 3);


        Xinu.print(a < b);
        Xinu.print(a + 1 < b * 3);

 
        Xinu.print(t && f);
        Xinu.print(a < b && t);


        Xinu.print(arr[a]);
        Xinu.print(arr[a + 1]);
        Xinu.print(arr[a][b]);

 
        Xinu.print(a.compute());
        Xinu.print(a.compute(1));
        Xinu.print(a.compute(1, 2));
        Xinu.print(a.compute(b + 1, t && f));
        Xinu.print(a.compute(1).next(2));


        Xinu.print(t && arr[a].compute(b, 3) * 2);

        Xinu.print(new int[10]);
        Xinu.print(new Foo());

        Xinu.print((2 + 3) * 4);

        Xinu.print(arr.length);

    }
}