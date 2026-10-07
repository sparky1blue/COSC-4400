package Absyn;

public class SubExp extends Exp
{
  public Exp left, right;

  public SubExp(Exp left, Exp right)
  {   this.left = left;   this.right = right;   }

  public String toString()
  {   return this.left + " - " + this.right;   }
}
