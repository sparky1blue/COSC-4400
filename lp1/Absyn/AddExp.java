package Absyn;

public class AddExp extends Exp
{
  public Exp left, right;

  public AddExp(Exp left, Exp right)
  {   this.left = left;   this.right = right;   }

  public String toString()
  {   return this.left + " + " + this.right;   }
}
