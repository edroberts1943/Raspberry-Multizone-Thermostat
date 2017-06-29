
/**
 * Diagnostic-     class to support conditional printing of diagnostic information
 * 
 * @author (Edward B Roberts) 
 * @version (6-25-17)
 */
public class Diagnostic
{
    private boolean on;

    /**
     * Constructor for objects of class Diagnostic
     */
    public Diagnostic()
    {
        on = true;
    }

    /**
     * print(String s)
     * 
     * @param  s   the string to be conditionally printed.
     * @return     NONE  prints the string if on is true.
     */
    public void print ( String s)
    {
        if (on) System.out.println(s);
    }
}
