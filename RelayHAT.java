/**
 * Relay     implements the GPIO interface with a specified relay on the 4-relay Hat board
 * 
 * @author (Edward B Roberts) 
 * @version (6-28-17)
 * 
 */
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RelayHAT
{
    // instance variables - replace the example below with your own
    private int relayNumber;    //1-4
    
    GpioPinDigitalOutput relay1,relay2,relay3,relay4;

    /**
     * Constructor for objects of class Relay
     */
    public RelayHAT()
    {
        // Important Note:
        //
        //  The Documentation provided with the 4 Channel Relay AT is wrong for the Raspberry Pi 3.
        //  The corredt GPIO pin assignments are defined for relays 1-4 below. These were incorrectly
        //  published as 26, 19, 13, 06.gpio  I am using gpio version 2.44.  Assignments could change in
        //  different versions.
        
        final GpioController gpio = GpioFactory.getInstance();   // Must enable to test without  controller
        // initialise instance variables
        relay1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "Relay1", PinState.LOW);
        relay2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, "Relay2", PinState.LOW); 
        relay3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, "Relay3", PinState.LOW); 
        relay4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "Relay4", PinState.LOW); 
        relay1.setShutdownOptions(true, PinState.LOW);
        relay2.setShutdownOptions(true, PinState.LOW);
        relay3.setShutdownOptions(true, PinState.LOW);
        relay4.setShutdownOptions(true, PinState.LOW);
    }

    /**
     * turnOff()
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void turnOff(int relay) 
    {
        switch (relay) {
            case 1:
                relay1.low();
                break;
            case 2:
                relay2.low();
                break;
            case 3:
                relay3.low();
                break;
            case 4:
                relay4.low();
                break;
            default:
        }
             
                
        
    }
    
    public void turnOn(int relay) 
    {
           switch (relay) {
            case 1:
                relay1.high();
                break;
            case 2:
                relay2.high();
                break;
            case 3:
                relay3.high();
                break;
            case 4:
                relay4.high();
                break;
            default:
        }
              
        
    }
    
    
    public void toggle(int relay) 
    {
               switch (relay) {
            case 1:
                relay1.toggle();
                break;
            case 2:
                relay2.toggle();
                break;
            case 3:
                relay3.toggle();
                break;
            case 4:
                relay4.toggle();
                break;
            default:
        }
              
        
    }
    
    public void shutdown() 
    {
       // Gpio.shutdown();
    }
}
