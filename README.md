this is a logger that logs to a wpilog

!notice this is currantly in active development and not yet finished 

‎ 

what is working:

auto logging all the veriables in a registered class

outputing to a wpilog

 ‎ 
 
working but needs to be improved:

logging suppliers

getting the file to the pc

‎ 

what i want to add:

auto logging of function returns

‎ 

how to use the test project:
1. download or clone the project
2. run the app on your phone
3. press start logging wait a few seconds and press stop logging
4. download the android sdk platform tool to be able to use adb
5. open cmd in the platform tool folder
6. connect to the phone with usb or adb over wifi
7. use adb pull /sdcard/.../FtcLoggerTest.myapplication/files/robot.wpilog . (i dont currently remember what is the exact file path i will update this in the next few days)
8. now you should have a robot.wpilog file in your platform tool folder

how to use with ftc robot:
1. download or clone the project
2. copy the files(WpiLog, TelemetryManager, SupplierLog and NoLog) to your robot project
3. inside your opmode put this line 
        WpiLog.getInstance().setup(hardwareMap.appContext);
4. to every object you want to log do TelemetryManager.getInstance().register(); and give it the object
5. to start logging in your opmode add the line
       TelemetryManager.getInstance().start();
6. run the op mode
7. download the android sdk platform tool to be able to use adb
8. open cmd in the platform tool folder
9. connect to the robot with usb or adb over wifi
10. use adb pull /sdcard/.../FtcLoggerTest.myapplication/files/robot.wpilog . (i dont currently remember what is the exact file path i will update this in the next few days)
11. now you should have a robot.wpilog file in your platform tool folder
