class testRegularExtends {
    public static void main(String[] args) {
        Child c;
        int x;

        c = new Child();
        x = c.getValue();
    }
}

class Parent {
    int value;

    public int getValue() {
        return value;
    }
}

class Child extends Parent {
    public int getValue() {
        return 42;
    }
}