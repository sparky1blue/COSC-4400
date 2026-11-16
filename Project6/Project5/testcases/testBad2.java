class testBad2 {
    public static void main(String[] a) {
        Xinu.printint(new Foo().Bar(1));
    }
}

class Foo {
    int f;

    public int Bar() {
        int x;
        boolean b;
        x = undefinedVar;
        b = 5;
        f = new Missing();
        return b;
    }

    public int Baz(int p) {
        return this.Nope(p, p);
    }
}