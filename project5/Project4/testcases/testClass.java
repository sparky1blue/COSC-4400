class testClass {
    public static void main(String[] a) {
        Xinu.printint(new Fac().ComputeFac(10));
    }
}

class Fac {
    int count;

    public int ComputeFac(int num) {
        int num_aux;
        if (num < 1)
            num_aux = 1;
        else
            num_aux = num * (this.ComputeFac(num - 1));
        return num_aux;
    }
}
