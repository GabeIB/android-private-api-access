# android-private-api-access
An exercise in using Java Reflection to access a private Android API. The included APK contains an Android service that lists all active services running on the machine, then retrieves the batterystats interface and calls isCharging, printing the output to logcat.

## Dependencies
This application requires an Android emulator and adb. All testing occured on a Nexus 5X API level 24.

## Usage
* Boot the emulator
* `adb install saucechallenge.apk`
* `adb shell am startservice net.gabrielbrown.saucechallenge/.MyService`

## Time
Completing this challenge took me 5 hours. I was unfamiliar with Android mobile development before this challenge, so I needed to take time to read documentation on Android services. Part 1 took about 2 hours, and parts 2-4 took the remaining 3 hours.

If I had more time, I would refactor the code to remove duplicate code and restructure the try-catches to make the code more readable.
