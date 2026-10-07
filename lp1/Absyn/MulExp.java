package Absyn;

public class MulExp extends Exp
{
  public Exp left, right;

  public MulExp(Exp left, Exp right)
  {   this.left = left;   this.right = right;   }

  public String toString()
  {   return this.left + " * " + this.right;   }
}
