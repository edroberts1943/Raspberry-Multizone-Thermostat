
/**
 * Write a description of class Target here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Target
{
    // instance variables - replace the example below with your own
    private int x;
    int unit;
    float temperature;
    String mode;

    /**
     * Constructor for objects of class Target
     */
    public Target(int u, float t, String m)
    {
       unit = u;
       temperature = t;
       mode = m;
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public int getUnit()
    {
        // put your code here
        return unit;
    }
    
    public float getTemperature() {
        return temperature;
    }
    
    public String getMode() {
        return mode;
    }
}
