package Absyn;

public class Goal 
{
  public Exp e;
  public Goal(Exp e) {   this.e = e;   }
  
  public String toString()
  {
    if (null != e) return e.toString();
    else           return null;
  }

}
