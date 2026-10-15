class testExtends {
    public static void main(String[] args) {
        Data d;
        Worker w;
        Data nt;

        d = new Data();
        w = new Worker();
        nt = w.setData(d);
        Xinu.threadCreate(w);
    }
}

class Worker extends Thread {
    Data d;
    int x;

    public void run() {
        x = d.increment();
    }

    public Data setData(Data dd) {
        d = dd;
        return d;
    }
}

class Data {
    int field;

    public synchronized int increment() {
        field = field + 1;
        return field;
    }
}