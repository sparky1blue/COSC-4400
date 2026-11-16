package Tree;

public abstract class Stm implements Hospitable {
    public abstract void accept(IntVisitor v, int d);
}
