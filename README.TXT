PROJECT TITLE: Raspberry Multiple Thermostat Controller

This repository comprises a complete BlueJ Project.

===================================================================================================
WARNING: THIS PROJECT IS FOR EDUCATIONAL PURPOSES ONLY. IT IS NOT WARRANTED OR PROPOSED AS SUITABLE
FOR ANY USE OR PURPOSE. ANY USE OF THIS INFORMATION IS AT THE USERS OWN RISK. DAMAGE TO EQUIPMENT,
PERSONS, OR PROPERTY COULD RESULT FROM INAPPROPRIATE APPLICATION OF IDEAS AND INFORMATION IN THIS
PROJECT.                 U S E  A T   Y O U R   O W N   R I S K !
===================================================================================================

PURPOSE OF PROJECT: Utilize multiple temperature sensors to control 
HVAC in a house with a single HVAC sytem.

My two-story house has a single thermostat located at a central location
downstairs. Most of the daylight hours, we stay in the family room downstairs. 
In the evening, we sleep upstairs.

We want to have a thermostat in the family room and another in the bedroom
upstairs to control the HVAC consistent with where we are likely to be.

The system is configured to be expansible to up to 6 remote thermostats but
by changing only  one dimension, it may be expanded indefinitely.


VERSION 2017-06-22

AUTHORS: Edward B. Roberts PE
         ed.roberts@verizon.net
         
HOW TO START THIS PROJECT:
         Install hardware and wire the contoller raspberry with a 4-relay HAT to the HVAC controls.
         
         On the controller raspberry, edit the file "schedule.txt" to specify the schedule desired
         
         Remote thermostats are  units 1 through 6.
         There must be at least 1 remote thermostat.
         
         Thermostats should be started before the controller is started so that these may be discovered
         by the controller when it initializes.
         
         
         
WORKING INSTALLATION:
        The working installation consists of at least 2 Raspberry Pi 3 computers:
        
        The Controller will be fitted with a 4-Relay HAT with relays 1,2, and 3 connected to the Fan,
        Compressor, and Furnace controls of the HVAC system respectively.
        
        The remaining Raspberry computers are RemoteThermometers.  Each will be fitted with a single 
        AM2302 Temperature/Humidity sensor.  There may be up to 6 remote thermometer Raspberrys.
        
        On each Raspberry, untar RaspberryMultizoneThermostat.tgz  in 
        /home/{yourDirectory}/raspberryMultizoneThermostat/.  All of the software for both controller 
        and remote thermostat Raspberries is included.  The BlueJ Project Files are also included.
        
        On the controller Raspberry, edit the file 
        /home/{yourDirectory}/raspberryMultizoneThermostat/schedule.txt to specify the schedule you 
        wish to execute.  For each time entered you will specify UnitNumber (of the remote
        thermostat to be controlling), the mode, and the target temperature.  The file is terminated 
        by "9999".
        
        On each remote thermometer Raspberry, edit the file thermometer.txt to specify the unit number 
        of this Raspberry remote thermometer.  THERE MUST BE A REMOTE THERMOMETER FOR EVERY UNIT 
        SPECIFIED IN THE SCHEDULE ON THE CONTROLLER.
        
        TBD:
        Set up start up files on each raspberry.
        
        To start the system, turn  power off on all the Raspberry computers.
        
        Turn power on on all the remote thermometer Raspberrys,
        
        Turn power on on the controller raspberry.
        
        Power can be appllied to the controller before the thermometer raspberries but operation will 
        be delayed as the controller repeats a polling operation to discover all of the thermometers 
        needed to support its schedule.  If all the required remote thermometers are not discovered 
        in 10 attempts, the controller will shut down. 
        
         
