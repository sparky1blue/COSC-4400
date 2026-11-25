
class Fields {
    public static void main(String[] a) {
        Xinu.printint(new Counter().run(3));
    }
}

class Counter {
    int count;
    int step;

    public int run(int n) {
        int i;
        i = 0;
        count = 0;
        step = 5;
        while (i < n) {
            count = count + i;
            i = i + 1;
        }
        return count;
    }
}