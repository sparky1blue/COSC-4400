package Absyn;

public class ModExp extends Exp{
	public Exp left, right;

	public ModExp(Exp left, Exp right){
		this.left = left;
		this.right = right;
	}
	public String toString()
	{	return this.left + " % " + this.right; }
}
