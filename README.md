# HRView

HRView is a data field for the Hammerhead Karoo. It shows your heart rate in large type, plus the average and the maximum for the ride, and fills the background with the color of the zone you are in.

It has no settings screen. To make the colors and numbers match your training, set up the heart-rate monitor, the zones, and the data page on the Karoo itself.

## What you will see

- The large number is your current heart rate, in beats per minute.
- **MED** is the average for the activity you are recording.
- **MAX** is the maximum for that same activity.
- The ECG icon at the top left marks the field.
- The background is the color of your heart-rate zone. The text switches between light and dark so it stays readable.

| Zone | Background | Text |
| --- | --- | --- |
| No data | White | Dark |
| Z1 | Dark green | Light |
| Z2 | Light green | Dark |
| Z3 | Yellow | Dark |
| Z4 | Salmon | Dark |
| Z5 | Red | Light |

Those zones are the ones on your Karoo profile. HRView uses the zone the Karoo has already assigned to your heart rate.

## Before you start

- A current-generation Karoo, on software **1.538.2049** or later. Installing from a phone is available on that generation.
- The **Hammerhead Companion** app on your phone: Android **1.36.0** or later, or iPhone **1.12.0** or later.
- The phone online, and the Karoo turned on, signed in to the same account, and connected to Wi-Fi.
- The release link for HRView. The APK is attached to that release.
- An ANT+ or Bluetooth heart-rate monitor. With no sensor paired, the field stays blank.

## 1. Install HRView on the Karoo

The app is installed from the APK link on the release. The phone shows a transfer screen, then the Karoo opens an install screen. Tap **Install** on the Karoo to finish. The field is on the device once you accept it there.

1. On your phone's browser, open the release link.
2. On that release, long-press the APK link.
3. In the share menu, choose **Hammerhead Companion**.
4. Wait for the Karoo to show the install screen, then tap **Install**.

The Karoo needs to be on and connected to Wi-Fi while the APK transfers.

### Check that it installed

On the Karoo, open the extension library. HRView should appear among the third-party apps.

You can also open the HRView app. That screen confirms it is active and reminds you of the next step: add the **❤ HRView** field to a ride page. There is nothing to change on that screen.

When a new release is published, install it the same way: open the release link, long-press the APK link, and share it with Hammerhead Companion.

## 2. Pair the heart-rate monitor

The field reads the heart rate the Karoo receives from the sensor. If the sensor is not paired, or is not enabled on the profile you ride with, you will see `--` and a white background for the whole ride.

1. Put the monitor on and wait for it to wake. For a chest strap, fastening it is enough.
2. On the Karoo home screen, tap **Apps**.
3. Open **Sensors**.
4. Tap **+** to search for nearby sensors. You can filter by ANT+ or Bluetooth.
5. Select your heart-rate sensor and confirm with **✓**. The Karoo shows a green screen when pairing succeeds.

After that, the sensor reconnects on its own whenever it is active. You do not need to pair it before every ride.

If you use more than one profile, assign it to the profile you will use with HRView:

1. In **Sensors**, open the heart-rate monitor.
2. Scroll down to **Enabled on**.
3. Tap **Select** and check that profile.

Or, from the profile:

1. **Apps** → **Profiles**.
2. Open the profile.
3. Scroll down to **Sensors Enabled**, tap **Select**, and check the heart-rate monitor.

## 3. Set your heart-rate zones

The field color is the zone the Karoo calculates from these thresholds. If the zones are not yours, the heart-rate number can be right while the color is wrong.

1. On the home screen, tap **Apps**.
2. Open **Settings**.
3. Scroll down to **Training Metrics**.
4. Open **Heart Rate Zones**.
5. Enter your maximum heart rate and review the boundaries of the five zones. You can keep the Karoo's automatic calculation, or type the boundaries yourself, using the same values you train with.

You can also edit weight, heart-rate zones, and power zones from the Hammerhead Dashboard, under Training Metrics settings. If you connect TrainingPeaks, Strava, intervals.icu, or another app, those zones can sync to your Hammerhead account. On the Karoo, check that the boundaries that arrived are the ones you want to see on the road: each platform calculates zones differently, and the HRView color always follows the Karoo.

Changing the zones in Training Metrics changes the field color on the next reading.

## 4. Add the field to a page

1. **Apps** → **Profiles**.
2. Open the profile you ride with. Check that the heart-rate monitor is still listed under **Sensors Enabled**.
3. Open that profile's data pages.
4. Add a page with **+** (a data page or a map page), or edit one you already have.
5. Add a field and search for **❤ HRView**. The filters at the top of the list narrow the search.
6. Select it. In the editor, the field cycles through the five zones with sample numbers so you can see the colors before you ride.
7. Long-press the field and drag it to the place on the page where you want to read it. On a map page you can put the fields at the top or the bottom.
8. Leave the editor to save the page.

The large number grows with the cell. A wide cell is easy to read at a glance; a half-width cell holds the same data, smaller. Each page holds up to 10 fields, and each profile holds up to 12 pages.

To remove it, tap the field in the editor and choose delete. During a ride you can also replace it: long-press the field and pick another one. While riding you can change the field, and the page layout stays as you saved it.

## 5. Use it on a ride

1. Choose the profile where you added **❤ HRView**.
2. Check that the heart-rate monitor is on and connected.
3. Press record to start the activity.

From then on:

- The large number follows your heart rate.
- **MED** and **MAX** are the average and the maximum for this recording. They start over each time you begin an activity.
- The background changes zone when your heart rate crosses the boundaries you set.

If you pause, the field keeps the last heart rate, average, maximum, and color. When you stop the activity and return to idle, the field goes back to `--` on a white background. That is expected: outside a recording there is no ride average or maximum to show.

## If something looks wrong

| What you see | What to check |
| --- | --- |
| The field does not appear when you add data | Open the extension library and confirm HRView is installed. If the install stopped on the phone, accept **Install** on the Karoo. |
| `--` and a white background before you press record | This is the idle state. Start the activity. |
| `--` and a white background while recording | The heart-rate monitor is not connected, or it is not checked under **Sensors Enabled** for this profile. Open it in **Sensors** and confirm it is on the profile. |
| Heart rate changes and the color is not what you expect | The **Heart Rate Zones** boundaries in Training Metrics. HRView colors the zone the Karoo reports. |
| **MED** or **MAX** does not match another screen | Those two values belong to the current activity, from the moment you pressed record. A previous activity does not carry over into the next recording. |
| The release never starts installing | The Karoo needs to be on Wi-Fi. Long-press the APK link on the release, and share that link with Hammerhead Companion. |
