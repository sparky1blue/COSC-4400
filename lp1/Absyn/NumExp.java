package Absyn;

public class NumExp extends Exp
{
  public int num;

  public NumExp(String s)
  {  num = Integer.parseInt(s);   } 
     

  public String toString()
  {   return Integer.toString(num);   }
}
