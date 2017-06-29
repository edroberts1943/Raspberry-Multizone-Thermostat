
/**
 * Entry is a class to store the time and unit schedule information
 * 
 * @author (Edward B Roberts) 
 * @version (6-28-20)
 */
public class Entry
{
    // instance variables - replace the example below with your own
    private int x;
    int time;   // in 24 hour format: eg 1220
    int target;
    int unit;
    String mode;

    /**
     * Constructor for objects of class Entry
     */
    public Entry(int t, int u, int tar, String sMode)
    {
        time =t;
        unit = u;
        target = tar;
        mode = sMode;
    }

    /**
     * getUnit()   returns the value of unit.
     * 
     * @param      NA 
     * @return     The value of unit.
     */
    public int getUnit()
    {
        return unit;
    }
   
        /**
     * getTemperature()   returns the value of temperature.
     * 
     * @param      NA 
     * @return     The value of temperature.
     */
    public int getTemperature() {
        return target;
    }
       
    public String getMode(){
        return mode;
    }
}
