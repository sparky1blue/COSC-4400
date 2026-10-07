package Absyn;

public class DivExp extends Exp
{
  public Exp left, right;

  public DivExp(Exp left, Exp right)
  {   this.left = left;   this.right = right;   }

  public String toString()
  {   return this.left + " / " + this.right;   }
}
