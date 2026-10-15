class testXinuExpr {
    public static void main(String[] args) {
        int x;

        x = Xinu.readint();
        Xinu.printint(x);
        Xinu.printint(Xinu.readint());
    }
}