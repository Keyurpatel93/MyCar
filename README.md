#MyCar

The MyCar application is a prototype application developed to emulate car setting personalization based on user authentication on a smartphone. The goal of this application is to allow car settings to be managed and personalized based on the driver. Note, this application does not interface with an actual vehicle, rather it updates another application called [CarState](https://github.com/Keyurpatel93/CarState) which emulates basic vehicle functionality on the device. Thus, upon successful authentication, the CarState application will show the seat position, radio station values and other settings being changed. Details about this application can be found at [here](https://github.com/Keyurpatel93/CarState). To emulate different users on a single device, the home screen of the application allows you to choose which user to authenticate as. Due to the limitations of android’s fingerprint APIs, the same fingerprint is used to authenticate all 4 users as we cannot enroll distinct fingerprints for the 4 users. 

## Use Case:
A driver enters the car and sits in the driver seat. Upon turning on the car, the user opens the MyCar application on their smartphone and authenticates themselves to the application using their fingerprint. The car magically adjusts the mirror positions, seat positions, radio settings and enforces constraints (max speed) based on the settings the authenticated user sets as well as the admin driver of the car. 

## Personalizable Features:
*	Seating position for the driver seat. Values are between 0 to 100 indicating how far up or back the seat is.
*	Max Speed: Alerts the driver when they are exceeding the max speed limit set.
*	Seatbelt Enforced: Locks the radio until the driver has fasten his/her seatbelt. 
*	Radio Station: Changes the preset radio stations. 

## Functionality:
*	New users can be enrolled into the system and remove themselves. To enroll into the system, a secure code for the car will need to be known and a second admin code will need to be known to enroll a new driver as admin. 
*	Admin drivers can manager teenage drivers to enforce a seatbelt or max speed limit constraint. 
*	Seating position can be updated based on what the current seat position is shown in the CarState application. 

## Dependencies:
- **Hardware**:
  - Android Device with a fingerprint sensor running Android 6.0 or later
- **Software**: 
  - [AndroidViewAnimations] (https://github.com/daimajia/AndroidViewAnimations)
  - [OpenXC] (http://openxcplatform.com/android/getting-started.html)



##Permissions Needed:
- android.permission.USE_FINGERPRINT
- android.permission.WRITE_EXTERNAL_STORAGE
- android.permission.READ_EXTERNAL_STORAGE


Need fingerprint permission to allow the application to authenticate using the fingerprint sensor.
Need read/write permission to read and write to a xml file that stores data for the application. The xml file called “cardata.xml” which is located at the root of the internal storage, stores settings data (seat position, constraints) for enrolled users.

## License
Licensed under the BSD license.

##Screenshot
![Alt text](https://github.com/Keyurpatel93/MyCar-/blob/master/MyCar.png?raw=true "Screenshot")
