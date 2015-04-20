# Framework Tutorial #

### Information ###

This project is part of my Bachelor Thesis: "Implementation of a Bluetooth touchpad based on Android OS" at the [Chair of Operating Systems, TU Munich.](http://www13.in.tum.de/startseite/) You could download it [here](http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/BTTouchpad/thesis/). I will direct you to it whenever more information is needed.

## 1. Requirements ##
It is very important to check the [requirements section](http://code.google.com/p/android-bluetooth-touchpad/wiki/Requirements). For this tutorial I will use Ubuntu on the computer host side and a rooted Nexus One running Android, version 2.3.6.

## 2. Pairing ##

You have to pair the phone and the host. Then you have to mark the phone as trusted on the host side.

### A) On Nexus ###
Press the menu button -> Settings -> Wireless & networks -> Bluetooth Settings. Turn on Bluetooth and make it discoverable. (It lasts couple a minutes.)

### B) On Ubuntu ###
Install and open blueman. (sudo apt-get install blueman) Click on "Search". After the device is found right click on it and then "Pair". Confirm the pairing request on both phone and computer. Right click on the device again and then click on "Trust". Done.

## 3. Check out the Sample Code project ##
Check out the Sample Code project in Eclipse. SVN check out: http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/SampleCode/

## 4. The Manifest ##
In the Manifest the following permissions are declared: ```xml

<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
```

## 5. The Bluetooth HID Framework Code ##

The Framework code is in the package tum.betriebsysteme.kostadinov.btframework. In the /doc dir you could also find the framework documentation.

## 6. The Executable ##

The Linux executable, which is to be deployed and run from the console is placed in the /asset dir. This operation is however handled by the SDP component in the framework. Read chapter 5 in the [thesis](http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/BTTouchpad/thesis/) to find out more.

## 7. UI ##
In main.xml we define very simple UI with three buttons:
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:orientation="vertical"
android:layout_width="fill_parent"
android:layout_height="fill_parent"
>
<Button
android:layout_width="fill_parent"
android:layout_height="100dip"
android:text="Add service record."
android:id="@+id/add_service_button"
/>
<Button
android:layout_width="fill_parent"
android:layout_height="100dip"
android:text="Connect to device"
android:id="@+id/connect_to_device_button"
/>
<Button
android:layout_width="fill_parent"
android:layout_height="100dip"
android:text="Send message"
android:id="@+id/send_message_button"
/>


Unknown end tag for &lt;/LinearLayout&gt;


```

With the first button we intent to add the new service record in the SDP registry. With the second the connection to the computer host on both the control and interruption channels would be established. With the third we would send the message, which would be handled by the PC as standard keyboard input.

### 8. onCreate() ###
```java

public class SampleCodeActivity extends Activity implements OnClickListener, SDPStateListener, EventListener {

private boolean registered = false;
private Context context;

private SDPRegister sdpRegister;
private SocketThread l2capSocket1;
private SocketThread l2capSocket2;
private Handler handler;

private static final CharSequence hello_message = "helloandroidbluetooth";

/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.main);

sdpRegister = new SDPRegister(this,this);
l2capSocket1 = new SocketThread(this);
l2capSocket2 = new SocketThread(this);

findViewById(R.id.add_service_button).setOnClickListener(this);
findViewById(R.id.connect_to_device_button).setOnClickListener(this);
findViewById(R.id.send_message_button).setOnClickListener(this);

handler = new Handler();
context = this;
}
}
```

In the onCreate method the SDP component is initiated. It is reponsible for registering and deleting the service record from the SDP registry. The Threads for the control and interruption L2CAP channels are also initiated. We then bind a listener for the three UI-buttons.

### 9. Service registration ###

```java

@Override
public void onClick(View view) {

if(view.getId() == R.id.add_service_button){

if(registered){
toastLog("Service is already registered.");
return;
}

// For keyboard and regular mouse
sdpRegister.registerHID(SDPRegister.SDP_CONFIG_MOUSE_RELATIVE);
// or for keyboard and pointer
// sdpRegister.registerHID(SDPRegister.SDP_CONFIG_MOUSE_ABSOLUTE);
view.setEnabled(false);
}
```

When the register button is pressed, the SDP component is attempting to add the new service record in the registry of the SDP module. You could choose between one of two available service records - mouse + keyboard or pointer + keyboard. The call is asynchronous. The result is delivered through a listener and the onRegisterComplete method is called.

```java


@Override
public void onRegisterComplete(int code) {

if(SDPRegister.isSuccess(code)){

toastLog( "The new service record has been registered successfuly!");

registered = true;

}else{

toastLog("The Registration failed on your phone. Try with Nexus One.");

}

}
```

If the result code is not positive you could check its semantic by comparing it with the error codes in the SDP component. It is possible that your development phone could not add the new service record in the registry, because its Bluetooth stack has been modified by the manufacturer. This is for example the case with HTC Desire. Possible solution is to find a way to flash a clean Android ROM on the device or simply try with one of the Nexus devices.

### 10. Refresh the service on the host side (IMPORTANT) ###

Now that the service is in the phone's SDP registry, the host side must be notified about this change. Open blueman and right click on the Android device in the list of devices. Press the "Refresh Services" button! (You might press it several times, sometimes it didn't work with one press when I was testing.) The host is aware there is an input service available and has opened the L2CAP ServerSocket. You are all se to connect.

### 11. Connecting ###
After the computer host side is aware of the new service, one could press the connect button and initiate the connection on both the control and interruption channels. However, since this is only a demo you should hard-code the Bluetooth address of the computer host you are connecting to. To check this address on your ubuntu computer, just type hciconfig in the console.
```java

else if(view.getId() == R.id.connect_to_device_button){
if(!registered){
toastLog("Service hasn't been registered. Connection will fail.");
return;
}
/*	ATTENTION !!!
*  MAKE SHURE TO PAIR YOUR PHONE WITH THE LINUX COMPUTER
*  AND MARK THE PHONE AS TRUSTED !!!
*
*  ENTER YOUR COMPUTER'S BLUETOOTH ADDRESS HERE !!!
*  TO FIND IT OUT, TYPE "hciconfig" IN THE LINUX CONSOLE.
*/
String address = "00:09:DD:50:86:5B";

BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);

l2capSocket1.connect(device, 0x11);

l2capSocket2.connect(device, 0x13);

view.setEnabled(false);
}
```

The connect method is also asynchronous and the result is delivered through a listener. One of two methods is called. If the connection attempt fails:

```java

@Override
public void onSocketConnectionFailed(int port) {
toastLog("The connection could not be established. Port: "+port);

}
```

Or if it succeeds:

```java

@Override
public void onSocketConnected(int port) {


toastLog("Connection on "+port+" established.");

}
```

If the connection is established on both channels you are set to send mouse or keyboard reports. Or if you added the other service record to the registry, you have to send pointer or keyboard reports. However, you could be also interested in listening for incoming messages from the computer host:

```java

@Override
public void onBytesRead(int port, int bytesRead, byte[] buffer) {

Log.v(this.getClass().getSimpleName(), "Some bytes are read !!!");

}
```

### 12. Sending input ###

Sending the input is made very easy.

```java

else if(view.getId() == R.id.send_message_button){

if(l2capSocket1.isConnected() && l2capSocket2.isConnected()){

HIDReportKeyboard keyboardEvent = new HIDReportKeyboard();

for(int i=0;i<hello_message.length();i++){
int sign = hello_message.charAt(i);
keyboardEvent.setSingleKeycode(sign-93);
l2capSocket1.write(keyboardEvent.getReportPayload());
keyboardEvent.setSingleKeycode(HIDReportKeyboard.EMPTY_KEYCODE);
l2capSocket1.write(keyboardEvent.getReportPayload());
}
```

To send a single letter you create a HIDReportKeyboard object and set the keycode. Then you could send the bytes returned by the getPayload method. Similarly, you could send mouse reports or if the other record is in the registry, pointer reports. Check the documentation of the report package for more information. If everything is successful your PC should have received "helloandroidbluetooth" as standard keyboard input.

### 12. Termination ###
To terminate the service you should safely disconnect from the computer host and remove the service record from the SDP registry - the service would not be available:

```java

@Override
public void onPause(){
super.onPause();
l2capSocket1.cancel();
l2capSocket2.cancel();
sdpRegister.unregisterHID();
this.finish();
}
```

Once you have finished this tutorial, you might take a look at the Bluetooth Touchpad Application. It realizes touchpad, keyboard, joystick and other use cases.
