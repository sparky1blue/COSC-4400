class Testinherit {
    public static void main(String[] a) {
        Xinu.printint(new Child().Get());
    }
}

class Parent {
    int x;
    int y;

    public int Get() {
        return x;
    }

    public int Set(int v) {
        x = v;
        return x;
    }
}

class Child extends Parent {
    int z;

    public int Get() {
        return z;
    }

    public int Extra() {
        return y;
    }
}