package Absyn;

public class IdExp extends Exp
{
  public String id;

  public IdExp(String id)
  {   this.id = id;   }

  public String toString()
  {   return id;   }
}
