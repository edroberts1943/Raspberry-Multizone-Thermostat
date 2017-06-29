 
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

/**
  * Classs DHT11   Implements and interface with the temperature/humidity sensor
  * 
  * The communications portion of this class is based upon an example by Eric Smith referenced below:
  * https://stackoverflow.com/questions/28486159/read-temperature-from-dht11-using-pi4j
  **/ 

public class DHT11 extends Thread {
    private static final int    MAXTIMINGS  = 85;
    private final int[]         dht11_dat   = { 0, 0, 0, 0, 0 };
    private int success = 0;
    private int failure = 0; 
    private int reads = 0;
    private float percentFail;
    private float lastTemperature = (float) -1065.0;
    private float lastHumidity = (float) -1065.0;
    static Diagnostic d;
    
    /**
     * getPercentFail()  returns the percent failure rate of the communications with this sensor.  Failure rates of 
     *                   up to 80% are typical.
     * 
     * @param     NA
     * @return    calculate failure rate
     */
    public float getPercentFail() {
        return percentFail;
    }
    
    public DHT11() {
        d = new Diagnostic();
        
    }
    
    /**
     * run()      This is the main loop of the sensor.  New temperature and humidity values are read from the sensor
     *            every 3 seconds.
     * 
     * @param     NA
     * @return    NA   Temperature and relative humidity are read every 3 seconds.
     */
    public void run() {
        try {
           while (true) {
             Thread.sleep(3000);   
             // d.print("DTH11 running."); // Sleep for 3 seconds between reading
             this.getTemperature(7);    // 7 is the pin number for GPIO 4 on Rasberry Pi 3
           }
        } catch ( Exception e ) {
            e.printStackTrace();
        } 
    }
    public void getTemperature(final int pin) {
        int laststate = Gpio.HIGH;
        int j = 0;
        dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;

        Gpio.pinMode(pin, Gpio.OUTPUT);
        Gpio.digitalWrite(pin, Gpio.LOW);
        Gpio.delay(18);

        Gpio.digitalWrite(pin, Gpio.HIGH);
        Gpio.pinMode(pin, Gpio.INPUT);

        for (int i = 0; i < MAXTIMINGS; i++) {
            int counter = 0;
            while (Gpio.digitalRead(pin) == laststate) {
                counter++;
                Gpio.delayMicroseconds(1);
                if (counter == 255) {
                    break;
                }
            }

            laststate = Gpio.digitalRead(pin);

            if (counter == 255) {
                break;
            }

            /* ignore first 3 transitions */
            if (i >= 4 && i % 2 == 0) {
                /* shove each bit into the storage bytes */
                dht11_dat[j / 8] <<= 1;
                if (counter > 16) {
                    dht11_dat[j / 8] |= 1;
                }
                j++;
            }
        }
        // check we read 40 bits (8bit x 5 ) + verify checksum in the last
        // byte
        if (j >= 40 && checkParity()) {
        // Decode the humidity
            float h = (float) ((dht11_dat[0] << 8) + dht11_dat[1]) / 10;
            if (h > 100) {
                h = dht11_dat[0]; // for DHT11
            }
        // Decode the temperature in celcius
            float c = (float) (((dht11_dat[2] & 0x7F) << 8) + dht11_dat[3]) / 10;
            if (c > 125) {
                c = dht11_dat[2]; // for DHT11
            }
            if ((dht11_dat[2] & 0x80) != 0) {
                c = -c;
            }
        // Convert temperature to Farenheight
            final float f = c * 1.8f + 32;
        // Print the values read
            reads += 1;
            success += 1;
            //d.print("Humidity = " + h + " % Temperature = " + f + "f");
            lastHumidity = h;
            lastTemperature = f;
            percentFail = (float)((float)failure / (float)reads) * 100;
        } else {
           
            reads += 1;
            failure += 1;
            percentFail = (float)((float)failure / (float)reads) * 100;
            //d.print("Data Error.  Percent Failures: " + percentFail + " %  in "+ reads + " attemmpts.");
            
        }

    }

    /**
     * getTemp()   Returns the value of the temperature last read successively from the sensor.
     * 
     * @param     NA
     * @return    current temperature
     */
    public float getTemp(){
        return lastTemperature;
    }
    
    
    /**
     * getHumidity()   Returns the value of the humidity last read successively from the sensor.
     * 
     * @param     NA
     * @return    current humidity
     */
    public float getHumidity() {
        return lastHumidity;
    }
    
    
    /**
     * checkParity()   Communicions protocol parity check
     * 
     * @param     NA
     * @return    current temperature
     */
    private boolean checkParity() {
        return dht11_dat[4] == (dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3] & 0xFF);
    }

    /**
     * main()     Sets up port configuration for communications with the sensor.
     * 
     * @param     NA
     * @return    NA
     */
    public static void main(final String ars[]) throws Exception {
        
        //d.print("Starting Main()");
        // setup wiringPi
        //d.print("About to set up wiringPi");
        if (Gpio.wiringPiSetup() == -1) {
            d.print(" ==>> GPIO SETUP FAILED");
            return;
        }
        //d.print("WiringPi was apparently set up OK");
        GpioUtil.export(3, GpioUtil.DIRECTION_OUT);
        d.print("DHT11 Constructor Finished.");
        //final DHT11 dht = new DHT11();
        
    }
}

