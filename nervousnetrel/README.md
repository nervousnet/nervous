# Planetary Nervous System

## Sensors

Currently we are focusing on Android devices. For a more elaborate introduction of available sensors please consider the official [documentation](http://developer.android.com/reference/android/hardware/SensorEvent.html). All information captured by Nervous is kept in triples:

    time            sensor  value
    1398700136002   light   100.0
    ...             ...     ...

Where:
* time - number of milliseconds since Epoch
* sensor - string value identifying the measurement value
* value - floating point value representing the measurement result in the corresponding units

Certain sensors are multi-valued, e.g. accelerometer will report acceleration on 3 axis:

    time            sensor  value
    1398700136083   accX    1.6208214
    1398700136083   accY    6.347082
    1398700136083   accZ    7.286886

### Light

Ambient light level in SI lux units. Outputs one record per measurement interval, e.g.:

    time            sensor  value
    1398700136002   light   100.0

### Accelerometer

Reports acceleration of the device on 3 axis:
* accX - acceleration minus Gx on X-axis
* accY - acceleration minus Gy on Y-axis
* accZ - acceleration minus Gz on Z-axis

Outputs three records per measurement interval, e.g.:

    time            sensor  value
    1398700136083   accX    1.6208214
    1398700136083   accY    6.347082
    1398700136083   accZ    7.286886

### Magnetic Field

Reports ambient magnetic field on 3 axis:
* magX - a micro-Tesla value on axis X
* magY - a micro-Tesla value on axis Y
* maxZ - a micro-Tesla value on axis Z

Outputs three records per measurement interval, e.g.:

    time            sensor  value
    1398700135890   magX    4.02
    1398700135890   magY    -24.3
    1398700135890   magZ    -29.34

### Proximity

Reports the proximity value in centimeters (on older devices this is a boolean value), e.g.:

    time            sensor  value
    1398700135760   prox    5.0

### Gyroscope

Reports the rate of rotation around the device's local x-, y- and z- axis:
* gyrX - angular speed around x-axis in radians per second
* gyrY - angular speed around y-axis in radians per second
* gyrZ - angular speed around z-axis in radians per second

Outputs three records per measurement interval, e.g.:

    time            sensor  value
    1399387382303   gyrX    -0.0021380284
    1399387382303   gyrY    0.0070249503
    1399387382303   gyrZ    -0.0033597588

### Temperature

Reports ambient temperature in degree Celsius, e.g.:

    time            sensor  value
    1398700135890   temp    21.3

### Humidity

Reports relative ambient air humidity in percent, e.g.:

    time            sensor  value
    1398700166006   humid   83.1

### Device Battery

Reports device battery/charging status, e.g.:

    time            sensor          value
    1398700166092   batteryPercent  0.91
    1398700166092   isUsbCharge     1
    1398700166092   isAcCharge      0

# Installation

You will need a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.htm), Apache [Ant](http://ant.apache.org/bindownload.cgi), and Android [SDK](http://developer.android.com/sdk/index.html) for API v10 or above. Once all the dependencies are setup, attach your device and run the following to install a debug version of Nervous:

    $ export ANDROID_HOME=/path/to/android-sdk
    $ export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
    $ ant debug
    $ adb install bin/nervous-debug.apk

# Contribute

We are interested in collaboratively developing a new information platform for everyone based on the following principles:

* community-based, open and transparent
* participatory, social, and fair
* respecting privacy and informational self-determination
* open for responsible applications of various kinds
* core functionality shall be freely accessible to everyone
