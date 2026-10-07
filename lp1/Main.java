public class Main
{
  public static void main(String[] args) 
  {
    Absyn.Goal g = null;
    try
      {
	java.io.FileReader reader = new java.io.FileReader(args[0]);
        g = new LP1Parser(reader).Goal();
      }
    catch (java.io.IOException e)
      {
	System.err.println(e.toString());
      }
    catch (ParseException p)
      {
	System.out.println(p.toString());
	System.exit(-1);
      }
    System.out.println(g);
  }
} // Main
