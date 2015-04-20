# Requirements #

### Information ###

This project is part of my Bachelor Thesis: "Implementation of a Bluetooth touchpad based on Android OS" at the [Chair of Operating Systems, TU Munich.](http://www13.in.tum.de/startseite/) You could download it [here](http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/BTTouchpad/thesis/). I will direct you to it whenever more information is needed.

## 1. Android device: ##

  * Phone running Android version **2.1 and later**
  * **ROOT** privileges , See http://en.wikipedia.org/wiki/Rooting_(Android_OS)
  * **Unmodified Bluetooth stack** Some manufacturers modify the Bluetooth stack in Android, so the framework will not work there. Ex. HTC Desire has proven not to work!

The framework was successfully tested on **Google Nexus One**.

## 2. Computer Host: ##

  * **Computer running Linux OS.** The framework was tested on Ubuntu, but other Linux distributions should also work. It is important they use the BlueZ Bluetooth Stack.
  * If running Ubuntu it is recommended you install **Blueman** - Bluetooth utility for Ubuntu with nice GUI. (_sudo apt-get install blueman_)
  * Windows will not work! And this is very unlikely to change. Check the [thesis](http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/BTTouchpad/thesis/), section 5.7.

## 3. Skills and Knowldge: ##
  * Good Android development skills are required. This is not the place to start learning Android.
  * Good understanding of the Bluetooth stack, HID and SDP is highly recommended. You could read chapter 2, 3 and 4 in the [thesis](http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/BTTouchpad/thesis/).
  * Understanding the concept behind the framework is not required in order to utilize its functionality. However, it would be required if you are willing to modify or extend it. You could read chapters 3, 4 and 5 of the [thesis](http://android-bluetooth-touchpad.googlecode.com/svn/trunk/%20android-bluetooth-touchpad/BTTouchpad/thesis/).

