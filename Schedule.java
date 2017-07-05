
/**
 * Schedule
 * 
 * This class maintains a vector representation of the control schedule and provides methods
 * for other classes to access the unit and temperature specified in the schedule based upon the time of day.
 * 
 * @author (Edward B.Roberts) 
 * @version (22 June 2017)
 * 
 */
import java.io.*;
import java.util.Vector;
import java.util.Scanner;
import java.io.File;
import java.time.LocalDateTime;



public class Schedule
{
    // instance variables - replace the example below with your own
    private Vector <Entry> theEntries;
    private int currentTargetIndex;
    Diagnostic d;
    private Vector theUnits;

    /**
     * Constructor for objects of class Schedule
     */
    public Schedule()
    {
        d = new Diagnostic();
        theEntries = new Vector();
        theUnits = new Vector();
        String thisTime = "25:30";
        int thisUnit = 99;
        int thisTemperature = 99;
        String thisMode = "";
        int count = 0;
        try {
           //Build theEntries Vector from the schedule file
           Scanner s = null;
           s = new Scanner( new File("/home/pi/workspace/StartController/schedule.txt"));
           s.useDelimiter(",| |\n");
          
           while ((s.hasNext() ) && (count < 24)){
               thisTime = s.next();
               // remove any leading carriage return
               //if (thisTime.charAt(0) == '\n') thisTime = thisTime.substring(1);
               // remove leading zeros from time
               while((thisTime.length() > 1)  && (thisTime.charAt(0) == '0')) {
                   thisTime = thisTime.substring(1);
                }
               d.print("Time as string: " + thisTime);
               if (thisTime.equals("9999")) break;
               int tt = -999;
               try { 
                     tt = Integer.parseInt(thisTime);
                   } catch (Exception ee) {
                       ee.printStackTrace();
                    }
               thisUnit = s.nextInt();
               theUnits.add(thisUnit);
               thisTemperature = s.nextInt();
               thisMode = s.next();
               d.print (thisTime + "   "+ thisUnit + "   " + thisTemperature + "  " + thisMode);
               Entry thisEntry = new Entry(tt, thisUnit, thisTemperature,thisMode );
               theEntries.add(thisEntry);
               count +=1;
            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        d.print("Final count is: " + count);
        d.print("The size of theEntries vector is: "+ theEntries.size());
       
    }

    /**
     * getUnit()-- Returns the unit that is schduled to have control when called
     * 
     * @param  NONE
     * @return Integer value of the currently scheduled unit (remote thermometer)
     */
    public int getUnit() {
        setCurrentTarget();
        Entry thisEntry = (Entry) theEntries.elementAt(currentTargetIndex);
        return thisEntry.getUnit();
    }
    
     /**
     * getTargetTemperature()-- Returns the target temperature for the time when called
     * 
     * @param  NONE
     * @return Integer value of the target temperature
     */
    public int getTargetTemperature() {
        setCurrentTarget();
        d.print("Schedule.getTargetTemperature(): currentTargetndex: " + currentTargetIndex + "  " +  LocalDateTime.now());
        return theEntries.elementAt(currentTargetIndex).getTemperature();
    }
    
     /**
     * setCurrentTarget()-- finds the target shedule entry appropriate for the current time based on
     *                      the time of day, then sets currentTargetIndex internally which points to
     *                      the entry to be used for scheduling.
     * @param  NONE
     * @return NONE 
     */
    public  void setCurrentTarget() {
        // calculate the integer value of te current time, eg. 1424 is 24 after two pm.
        String ts = LocalDateTime.now().toString();
        String hours = ts.substring(11);
        hours = hours.substring(0,2);
        String minutes = ts.substring(14,16);
        String targets = hours + minutes;
        // Process times near midnight so resulting targets will be a valid integer representation.
        while ((targets.charAt(0) == '0') && (targets.length() > 1))targets = targets.substring(1);
        if (targets.equals("") ) targets = "0";
        int target = Integer.parseInt(targets);
        int count = 0;
        while (count <  theEntries.size()){
            Entry thisEntry = (Entry) theEntries.elementAt(count);
            if (thisEntry.time >= target) break;
            count += 1;
        }
        currentTargetIndex = count - 1;
        // Account for roll-over at midnight
        if (theEntries.elementAt(1).time > target ) currentTargetIndex = 0;
    }
        
    
    public Vector getTheUnits() {
        return theUnits;
    }
    
    
    /**
     * getTargetMode()-- Returns a String representation of the mode scheduled for the time when called.
     * 
     * @param  NONE
     * @return String representation of the scheduled mode
     */
    public String getTargetMode() {
        setCurrentTarget();
        d.print("Schedule.getTargetTemperature(): currentTargetndex: " + currentTargetIndex + "  " +  LocalDateTime.now());
        return theEntries.elementAt(currentTargetIndex).getMode();
    }
        
        
    
    
}
