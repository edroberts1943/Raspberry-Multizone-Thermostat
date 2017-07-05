/**
 * Controller   This class reads the schedule and appropriate remote thermometer (unit) to control the HVAC system.
 *              It  interfaces with the 4-relay HAT which is the electrical interface to the HVAC system. 
 * 
 * @author (Edward B Roberts) 
 * @version (6-28-17)
 */
import java.net.*;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.time.LocalDateTime;
import java.util.Vector;
import java.io.PrintWriter;
import java.io.File;
import java.time.LocalDateTime;
import java.io.FileOutputStream;

public class Controller extends Thread
{
    // Variables that the user may want to change--but probably will find these satisfactory.
    String []theThermostatIPs = {"","","","","",""}; // There can be up to 6 thermostats. This array may be expanded to as many
                                                     // remotes as required. There are no other places expansion is required.
    int period = 1;                   // minutes- specifies the frequency the controller runs
    int minCompressOffMinutes = 3;    //Minimum time (minutes) that the compressor should be off before turning back on again.
    float heatingDeadband = (float) 0.5;
    float coolingDeadband = (float) 0.5;
    
    // Variables the user should have no need to change
    private int x;
    Diagnostic d;
    enum Mode {off,heating,cooling};
    enum State {off,on,};
    int numberOfThermostats = 0;
    float targetTemperature;
    Schedule schedule;
    int activeUnit;
    float currentTemperature;
    Mode mode;
    State fanState, compressorState, furnaceState;
    RelayHAT theHat;
    Vector theUnits;

    
    /**
     * Constructor for objects of class Controller
     */
    public Controller()
    {
      d = new Diagnostic();
      theUnits = null;
      theHat = new RelayHAT();
      fanState = compressorState = furnaceState = State.off;   
      turnFurnaceOff();
      turnCompressorOff();
      turnFanOff();
      int attempts = 0;
      while (attempts < 10 ) {
      schedule = new Schedule(); //The controller should be re-initialized when the schedule is changed
      // Find the remote thermometers and register them
      theUnits = schedule.getTheUnits();
      findRemoteThermometers();
      // Verify that all the remote thermometers required by the schedule are available
         attempts += 1;
         if ( !verifyThermometers()) {
             System.out.println("Required remote thermometers were not found. Attempt " + attempts + " of 10");
             if(attempts == 9) {
                 d.print("After 10 attempts, the required thermometers can not be found. Controller is shut down.");
                 System.exit(0);
                }
             try {
                 Thread.sleep(60000); // Delay 1 minute before trying again
               } catch (Exception es) {
                   es.printStackTrace();
               } 
        } else break;
    }
      d.print("Completed search for remote thermostats: " + numberOfThermostats + " found.");
      int j =0;
      while(j < 6) {
          d.print("theThermostatIPs["+j+"] is: "+theThermostatIPs[j]);
          j += 1;
        }
      //mode = Mode.cooling; // Need to implement persistance for mode.

      //testRelays();
      flash(10);
      this.start();
    }
    /**
     * run()       This is the main control loop of the controller.
     * 
     * @param      NA
     * @return     NA
     */
    public void run(){   
            d.print("The Controller has started running.");
            int looper = 0;
            while(true) {
                if (looper >= 100) looper = 0;
                if ( (looper % 10 ) == 0 ) logTemps();   // Every 10 cycles make log entry.
                looper += 1;
                String sMode = schedule.getTargetMode();
                if(sMode.equals("cooling") ) mode = Mode.cooling;
                if(sMode.equals("heating") ) mode = Mode.heating;
                if(sMode.equals("off") )mode = Mode.off;
                // get target temp and unit from the schedule
                targetTemperature = schedule.getTargetTemperature();
                activeUnit = schedule.getUnit();
                //get current temperature from the active remote thermometer
                currentTemperature = readRemoteThermometer(activeUnit);
                d.print("Beginning control loop. Mode is " + mode + "Currently: " +currentTemperature + "   " +targetTemperature);
                // switch on mode
                switch(mode){
                   case heating: 
                        flash(3);
                        // too hot
                        d.print("HEATING");
                        if ((currentTemperature > (targetTemperature + heatingDeadband)) && (furnaceState == State.on)) {
                            d.print("Too hot turning furnace off");
                            turnFurnaceOff();
                            try {
                               d.print("Waiting 3 minutes before turning fan off.");
                               Thread.sleep(180000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            d.print("Turning fan off");
                            turnFanOff();
                        }
                        // too cold
                        else if ((currentTemperature < (targetTemperature - heatingDeadband)) && (furnaceState == State.off)){
                            d.print("Too cold turning fan and furnace on");
                            turnFanOn();
                            turnFurnaceOn();
                        } else {
                            d.print("Furnace state is " + furnaceState + " no action required.");
                        }
                        break;
                   case cooling: 
                       flash(2);
                       // too hot
                       d.print("COOLING");
                       if ((currentTemperature > (targetTemperature + coolingDeadband)) && (compressorState == State.off)){
                           d.print("Too hot, turning fan and compressor on.");
                           turnFanOn();
                           turnCompressorOn();
                        }
                        // too cold
                       else if ((currentTemperature < (targetTemperature - coolingDeadband)) && (compressorState == State.on) ){
                           d.print("Too cold, turning compressor off");
                           turnCompressorOff();
                           try {
                              d.print("Waiting 2 minutes to turn the fan off");
                              Thread.sleep(120000);
                              d.print("Turning fan off");
                              turnFanOff();
                              d.print("Waiting additional time to prevent compressor from turning on too early.");
                              Thread.sleep(minCompressOffMinutes * 60000); // Delay 3 more minutes so compressor will not be damaged if turned on too soon.
                            } catch (Exception e1){
                                e1.printStackTrace();
                            }
                        } else {
                            d.print("Compressor state is " + compressorState + " no action required.");  
                        }
                        break;
                   case off: 
                       flash(1);
                       turnCompressorOff();
                       turnFurnaceOff();
                       turnFanOff();
                       break;
                    
                   default:{
                       //No action required. This covers the off case as well.
                       d.print("Defaulted.  Should never get here.");
                    }

                } // end of switch
                try {
                    d.print("Delay for 1 minute.");
                    int count = 0;
                    while (count < 60) {
                       Thread.sleep(1000); 
                       d.printNoFeed(".");
                       count +=1;
                    }
                       // run again after period minutes.
                } catch (Exception e2){
                    e2.printStackTrace();
                }
    }    // end of while true
}

/**
     * turnFurnaceOn     Implements the action to turn the furnace on and modify the state of the controller accordingly.
     * 
     * @param      NA
     * @return     NA 
     */
private void turnFurnaceOn(){
    if(furnaceState == State.off) {
        //Gpio.pinMode(13, Gpio.OUTPUT);
        //Gpio.digitalWrite(13, Gpio.HIGH); 
        theHat.turnOn(3);
        d.print("turnFurnaceOn " + LocalDateTime.now());
        furnaceState = State.on;
   } 
    
}


/**
     * turnFurnaceOff    Implements the action to turn the furnace off and modify the state of the controller accordingly.
     * 
     * @param      NA
     * @return     NA 
     */
private void turnFurnaceOff(){
    if(furnaceState == State.on) { 
        theHat.turnOff(3);
        d.print("turnFurnaceOff " + LocalDateTime.now());
   }
   // Always initiate relay off for safety
   furnaceState = State.off;
   
}


/**
     * turnFanOn     Implements the action to turn the fan on and modify the state of the controller accordingly.
     * 
     * @param      NA
     * @return     NA 
     */
private void turnFanOn() {
    if(fanState == State.off) { 
        theHat.turnOn(1);
        d.print("turnFanOn " + LocalDateTime.now());
        fanState = State.on;
   }
}



/**
     * turnFanOff     Implements the action to turn the fan off and modify the state of the controller accordingly
     * @param      NA
     * @return     NA 
     */
private void turnFanOff() {
    if (fanState == State.on) {  
        theHat.turnOff(1);
        d.print("turnFanOff " + LocalDateTime.now());
        fanState = State.off;
    }
}


/**
     * turnCompressorOn     Implements the action to turn the compressor on and modify the state of the controller accordingly.
     * 
     * @param      NA
     * @return     NA 
     */
private void turnCompressorOn() {
    if (compressorState == State.off) {
        theHat.turnOn(2);
        d.print("turnCompressorOn " + LocalDateTime.now());
        compressorState = State.on;
    }
}


/**
     * turnCompressorOff     Implements the action to turn the compressor off and modify the state of the controller accordingly.
     * 
     * @param      NA
     * @return     NA 
     */
private void turnCompressorOff(){ 
    if (compressorState == State.on) {
        theHat.turnOff(2);
        d.print("turnCompressorOff " + LocalDateTime.now()); 
   }
   // Always initiate relay off for safety
   compressorState = State.off;
   
}

private void flash(int n) {
    try {
        int i = 0;
        while (i < n) {
            theHat.toggle(4);
            Thread.sleep(500);
            theHat.toggle(4);
            Thread.sleep(500);
            i += 1;
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * readRemoteThermometer(int unit)
     * 
     * This method reads temperature from the instance of remoteThermometer class specified by int unit.
     * 
     * @param     int unit   Specifies the remoteThermometer instance to read from.
     * @return    theTemperatue returned from the remoteThermometer.
     */
    private float readRemoteThermometer(int unit) {
        float theTemperature = 999;
        String serverAddress = theThermostatIPs[unit];
        d.print("The server address is: " + serverAddress);
        try{
           Socket  s = new Socket(serverAddress, 9090);
           BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
           String answer = input.readLine();
           StringTokenizer st = new StringTokenizer(answer,":");   //answer is NULL
           String a,b,c,d;
           a = st.nextToken();
           b = st.nextToken();
           c = st.nextToken();
           d = st.nextToken();
           theTemperature = Float.parseFloat(d);
        } catch (Exception e4) {
            e4.printStackTrace();
        } 
        return theTemperature;
    }
 
    
       private String pollRemoteThermometer(int unit) {
        float theTemperature = 999;
        String serverAddress = theThermostatIPs[unit];
        String answer = "";
        d.print("The server address is: " + serverAddress);
        try{
           Socket  s = new Socket(serverAddress, 9090);
           BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
           answer = input.readLine();
        } catch (Exception e4) {
            e4.printStackTrace();
        } 
        return answer;
    }
    
/**
     * findRemoteThermometers()     This method searched the address range 192.168.1.0 through 192.168.1.999 looking for remote thermometers.
     *                              There must be at least 1 thermometer and at most 6 (controlled by the dimensions of theThermostatIPs[].
     * 
     * @param      NA
     * @return     NA 
     */
    public void findRemoteThermometers() {
        Socket socket = null;
        int i = 0;
        int lastAddress = 255;
    
        while (i < lastAddress) {
        String serverBaseAddress = "192.168.1.";
        String serverAddress = serverBaseAddress + i;
        try {
            socket = new Socket();
            //BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //String answer = input.readLine();
            //d.print(serverAddress +" Response: " + answer);
            socket.connect(new InetSocketAddress(serverAddress,9090),200);
            socket.close();
            registerThermometer( serverAddress);
            } catch (Exception e) {
                 try { 
                     socket.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                     
            }
            i += 1;
        } //end of while
       
    }
    
    
/**
     * registerThermometer(String ip)   This method stores the IP address of a discoverd thermometer in the Vector theThermostatIPs[].
     *                                  The unit number is reported by the remote thermometer and is used as the index into 
     *                                  theThermostatIPs[] to stor the thermostat's IP address.
     * 
     * @param      String ip            The IP address of the thermometer.
     * @return     NA                   theThermostatIPs[] vector is updated.
     */
    public void registerThermometer( String ip) {
        int unit = -1;
        try {
            //Thread.wait(10000);
            Socket  s = new Socket(ip, 9090);
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String answer = input.readLine();
            d.print("Controller.registerThermometer 128 :"+ ip + "   " +answer);
            StringTokenizer st = new StringTokenizer(answer,":");   //answer is NULL
             String a,b,c;
            a = st.nextToken();
            b = st.nextToken();
            c = st.nextToken();
            d.print("Controller.registerThermometer:  " +a+"  "+b+"  "+c);
            unit = Integer.parseInt(b);
            theThermostatIPs[unit] = ip;
            numberOfThermostats += 1;
            d.print("Controller.registerThermostat: The IP address of unit "+ unit + " is: " + theThermostatIPs[unit]);
        } catch  (Exception e3) {
            e3.printStackTrace();
        }
        
    }
    
        /**
         * Test Relays     Test the relays in an endless loop
         *
         * @param  NA
         * @return NA
         */
        public void testRelays() 
        {
            while(true) {
                try {
                   turnFanOn();
                   Thread.sleep(2000);
                   turnFanOff();
                   turnCompressorOn();
                   Thread.sleep(1000);
                   turnCompressorOff();
                   turnFurnaceOn();
                   Thread.sleep(1000);
                   turnFurnaceOff();
                } catch (Exception e){
                    e.printStackTrace();
                }   
            }

        }
        
        private boolean verifyThermometers() {
            // theUnits cotains the unit numbers of every themometer required
            boolean x = true;
            int i = 0;
            while (i <  theUnits.size()) {
                try {
                    if (theThermostatIPs[(int) theUnits.elementAt(i)] == null) return false;
                } catch (Exception e) {
                    return false;
                }
                i += 1;
            }
            return true;
        }
    
        private void logTemps() {
            //LocalDateTime tick = new LocalDateTime();
            PrintWriter out = null;
            //String timeStamp = LocalDateTime.now();
            try {
                  out = new PrintWriter(new FileOutputStream(new File("/home/pi/controllerLog.txt"),true)); 
                  //out.println(timeStamp);
                  int index = 0;
                  while (index < 6) {
                      if (theThermostatIPs[index].length() > 4) {
                          String reported = pollRemoteThermometer(index);
                          reported = reported + " " + LocalDateTime.now();
                          out.println(reported);
                          d.print("*****> " +reported);
                        }
                        index += 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) out.close();
                }
        }
   }
