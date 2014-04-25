# Planetary Nervous System

    Sensors:
      GPS
      accelerometer / gyroscope
      microphone
      camera
      screen pressure
      tcp/ip
      bluetooth
      wifi
      light sensor
      proximity sensor
      temperature
      barometer
      battery
      magnetic field

# Installation

You will need a JDK, Apache Ant, and Android [SDK](http://developer.android.com/sdk/index.html) for API v14 or above.

    export ANDROID_HOME=/path/to/android-sdk
    export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
    ant debug
    adb install bin/nervous-debug.apk
