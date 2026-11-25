package Riscv;
import Temp.Temp;

/**
 * InReg represents formal variables or local variables that may be stored
 *  in a register.
 * See MCIJ2e Section 6.2.
 */
public class InReg extends Frame.Access {
    Temp temp;

    public InReg(Temp t) {
	temp = t;
    }

    public Tree.Exp exp(Tree.Exp fp) {
	return new Tree.TEMP(temp);
    }

    public Tree.Exp exp(Frame.Frame f) {
	return new Tree.TEMP(temp);
    }

    public String toString() {
	return temp.toString();
    }
}
