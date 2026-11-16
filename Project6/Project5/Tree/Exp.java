package Tree;

public abstract class Exp implements Hospitable{
    public abstract void accept(IntVisitor v, int d);
}
