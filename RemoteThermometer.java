
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Vector;
import java.util.Scanner;
import java.io.File;
import java.time.LocalDateTime;

/**
 * RemoteThermometer   This class collects temperature and relative humidity and makes these data 
 *                     available to the Controller (or other classes) upon request.
 * 
 * @author (Edward B Roberts) 
 * @version (6-28-17)
 */
public class RemoteThermometer extends Thread
{
    // instance variables - replace the example below with your own
    private int thisUnit;
    private DHT11 sensor;
    Diagnostic d;

    /**
     * Constructors for objects of class RemoteThermometer
     */
    public RemoteThermometer() {
        d = new Diagnostic();
        // Determine the unit number from file remoteThermometer.txt
        // Only the first word is read from the file, an integer specifying the unit number.
        try {
           BufferedReader br = new BufferedReader(new FileReader("remoteThermometer.txt"));
           String thisLine = br.readLine();
           Scanner sc = new Scanner(thisLine);
           String unitNumber = sc.next();
           thisUnit = Integer.parseInt(unitNumber);
           d.print("The unit number from file is: " + thisUnit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //d = new Diagnostic();
        //sensor = new DHT11();
        try {
               sensor  = new DHT11();
               sensor.main(null);
               sensor.start();
               Thread.sleep(5000);
               //d.print("RemoteThermometer: Returned from creating sensor.");
               this.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    
    public RemoteThermometer(int unit)
    {
        // initialise instance variables
        d = new Diagnostic();
        thisUnit = unit;
        //sensor = new DHT11();
        try {
               sensor  = new DHT11();
               sensor.main(null);
               sensor.start();
               Thread.sleep(5000);
               //d.print("RemoteThermometer: Returned from creating sensor.");
               this.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    
     /**
     * run()   This is the main run loop of the remoteThermometer.
     * 
     * @param     NA
     * @return    NA
     */
    public void run() {
        d.print("RemoteThermometer.run() has been initiated. Unit # "+ thisUnit);
        try {
        ServerSocket listener = new ServerSocket(9090);
        try {
            while (true) {
                Socket socket = listener.accept();
                try {
                    String message = "Unit:"+thisUnit+":temperature:"+getTemperature()+":humidity:"+getHumidity();
                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                    out.println(message);
                    d.print("RemoteThermometer:run(),sent messsage: "+message);
                }
                finally {
                    socket.close();
                }
            }
        } 
        finally{
            listener.close();
        }
    } catch (IOException eio) {
        eio.printStackTrace();
    }
        
    }
        
    /**
     * getPercentFail()    Returns the calculated failure rate of communications with the thermostat.
     *                      The one-wire communications with the temperature/humidity sensor is very
     *                      error-prone. Error rates of 50 to 80% are normal.  This is overcome by
     *                      the remote thermometer collecting information more often that is needed and
     *                      saving the last good values received.
     * 
     * @param     NA
     * @return    communications failure rate
     */
    public float getPercentFail(){
        return sensor.getPercentFail();
    }

     /**
     * getTemperature   Returns the value of the temperature measured at this remote thermostat.
     * 
     * @param     NA
     * @return    temperature measured at this remote thermostat
     */
    public float getTemperature (){
        return sensor.getTemp();
    }
    
     /**
     * getHumidity   Returns the value of the humidity measured at this remote thermostat.
     * 
     * @param     NA
     * @return    relative humidity measured at this remote thermostat
     */
    public float getHumidity() {
        return sensor.getHumidity();
    }
    
    /**
     * getUnitNumber   Returns the value of the unit number of this remote thermostat instance.
     * 
     * @param     NA
     * @return    unit number of the remoteThermostat.
     */
    public int getUnitNumber()
    {
        // put your code here
        return thisUnit;
    }
}
