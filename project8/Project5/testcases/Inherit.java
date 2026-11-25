class Inherit {
    public static void main(String[] a) {
        Xinu.printint(new Child().second(4));
    }
}

class Parent {
    public int first(int a) { return a; }
    public int second(int b) { return b + 1; }
}

class Child extends Parent {
    public int second(int b) { return b + 2; }
    public int third(int c) { return c; }
}