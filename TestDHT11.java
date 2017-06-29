

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;



/**
 * The test class TestDHT11.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class TestDHT11
{
    int thisUnit1 = 1;
    int thisUnit2 = 2;
    int thisUnit3 = 3;
    /**
     * Default constructor for test class TestDHT11
     */
    public TestDHT11()
    {
      
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }


      
    @Test
    public void testTempServer () {
        RemoteThermometer tm = new RemoteThermometer(thisUnit1);  // tm starts itself
        String answer = null;
        System.out.println("Beginning of test server at 192.1.68.1.166");
        String serverAddress = "192.168.1.166";
        try {
            System.out.println("TestTempServer: sleeping 10 seconds to let remote thermometer stabalize.");
            Thread.sleep(10000);
            int i = 0;
            while (i < 100){
            Thread.sleep(5000);    
            Socket s = new Socket(serverAddress, 9090);
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            answer = input.readLine();
            System.out.println("The reported information: " + answer);
            } //end while
        } catch (Exception e) {
            if (answer.equals(null)) System.out.println ("No response from Temp Server");
            e.printStackTrace();
        }
        
    }
    
    @Test
    public void StartController () {
        // The controller can not run with the RemoteThermostat.  1. Only one instance of Gpio is allowed.  2. Physically,
        // the controller board can not accomodate a HAT and the temperature sensor.
        Controller cont = new Controller();
        
        
    
     }
    
     @Test
    public void TestRelays () {
        //RemoteThermometer tm;
        //tm = new RemoteThermometer(thisUnit);
        Controller cont = new Controller();
        
        
    
     }
    
    @Test
    public void testSchedule()
    {
        Schedule s = new Schedule();
        System.out.println ( "Temperature: "+ s.getTargetTemperature() + " Unit: " + s.getUnit());
        
    }
    
    
    @Test
    public void RunRemoteThermometer()
    {
        RemoteThermometer tm;
        tm = new RemoteThermometer(thisUnit1);
        try {
            Thread.sleep(20000); //Wait 20 seconds for the sensor to stabalize
            float t;
            float h;
            float f =(float) 0.0;
            int count = 0;
            while (true){
                t = tm.getTemperature();
                h = tm.getHumidity();
                f = tm.getPercentFail();
                System.out.println (count +" Temperature: " + t + " Humidity : "+ h + " Failure rate: " +f); 
                // testTempServer();
                Thread.sleep(5000); //5 second delay
                count +=1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
          
    
    @Test
    public void testHat1()
    {
        RelayHAT hat = new RelayHAT();
        int count = 1;
        try {
          while(true) { 
               hat.turnOn(count);
               System.out.println("Relay " + count + " On");
               Thread.sleep(1000);
               hat.turnOff(count);
               System.out.println("Relay " + count + " Off");
               Thread.sleep(1000);
               count += 1;
               if (count == 5) count = 1;
          }
        }catch (Exception e) {
           e.printStackTrace();
        }
    }
    @Test
    public void createRemoteThermometerNoArguments()
    {
        RemoteThermometer tm = new RemoteThermometer();
        
    }
}




