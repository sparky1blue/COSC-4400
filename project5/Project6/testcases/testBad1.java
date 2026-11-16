class testBad1 {
    public static void main(String[] a) {
        Xinu.printint(new Foo().Bar());
    }
}

class Foo {
    public int Bar() {
        int x;
        x = true;
        return x;
    }
}