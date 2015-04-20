# Contents #

### Information ###

This project is part of my Bachelor Thesis: "Implementation of a Bluetooth touchpad based on Android OS" at the [Chair of Operating Systems, TU Munich.](http://www13.in.tum.de/startseite/) You could download it [here](http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/BTTouchpad/thesis/). I will direct you to it whenever more information is needed.

## 1. Bluetooth Touchpad Application ##

The Bluetooth Touchpad Application is implementing the Bluetooth HID Framework, which could be found in the /src directory and more specifically in the tum.betriebsysteme.kostadinov.btframework package of the project. In the /doc dir you will find the framework documentation. The /jni dir contains the source code of the Linux executable. After compilation the executable file is placed in the /libs/armeabi dir. However, it should be manually moved to the /assets dir, so the SDP component of the framework could deploy and run it. In the /thesis dir one would also find my bachelor thesis as .pdf. Look for the "Videos" section in this wiki to see the app in action.

SVN check out: http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/BTTouchpad/

## 2. Bluetooth Touchpad Test ##

To run the Bluetooth Touchpad Test you must first check out the Bluetooth Touchpad Application. By running the test scenarios you could verify if your phone is capable of running the app or not. Read more about it at chapter 7 in the [thesis](http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/BTTouchpad/thesis/).

SVN check out: http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/BTTouchpadTest/



## 3. Sample Code ##

The sample code project is accompanying the framework tutorial, which could be also found in the wiki. It is the minimum effort required to implement the Bluetooth HID Framework and run a small demo.

SVN check out: http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/SampleCode/