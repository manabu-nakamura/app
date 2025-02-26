> [!CAUTION]
> The app targeted Android 14 and ran on Android 15 (Android Emulator).\
> https://developer.android.com/about/versions/15/behavior-changes-15

🟥https://github.com/manabu-nakamura/app/blob/master/editor/src/main/res/values/themes.xml:
- [`android:enforceNavigationBarContrast`](https://developer.android.com/reference/android/R.attr#enforceNavigationBarContrast)
```xml
<style name="Theme.App" parent="Theme.Material3.DynamicColors.DayNight.NoActionBar">
    <item name="bottomSheetDialogTheme">@style/ThemeOverlay.App.BottomSheetDialog</item>
    <item name="android:enforceNavigationBarContrast">false</item>
</style>

<style name="ThemeOverlay.App.BottomSheetDialog" parent="ThemeOverlay.Material3.BottomSheetDialog">
    <item name="android:navigationBarColor">@android:color/transparent</item>
</style>
```
<img src="s9.png" width="20%">-><img src="s2.png" width="20%">\
x->`<item name="android:enforceNavigationBarContrast">false</item>`

- `paddingBottomSystemWindowInsets`
```xml
<style name="Theme.App" parent="Theme.Material3.DynamicColors.DayNight.NoActionBar">
    <item name="bottomSheetDialogTheme">@style/ThemeOverlay.App.BottomSheetDialog</item>
    <item name="android:enforceNavigationBarContrast">false</item>
</style>

<style name="ThemeOverlay.App.BottomSheetDialog" parent="ThemeOverlay.Material3.BottomSheetDialog">
    <item name="bottomSheetStyle">@style/Widget.App.BottomSheet.Modal</item>
</style>

<style name="Widget.App.BottomSheet.Modal" parent="Widget.Material3.BottomSheet.Modal">
    <item name="paddingBottomSystemWindowInsets">false</item>
</style>
```
<img src="s6.png" width="20%">-><img src="s5.png" width="20%">\
x->`<item name="paddingBottomSystemWindowInsets">false</item>`\
https://github.com/material-components/material-components-android/issues/3173

- [`android:navigationBarColor`](https://developer.android.com/reference/android/R.attr#navigationBarColor)
```xml
<style name="Theme.App" parent="Theme.Material3.DynamicColors.DayNight.NoActionBar">
    <item name="bottomSheetDialogTheme">@style/ThemeOverlay.App.BottomSheetDialog</item>
    <item name="android:enforceNavigationBarContrast">false</item>
</style>

<style name="ThemeOverlay.App.BottomSheetDialog" parent="ThemeOverlay.Material3.BottomSheetDialog">
    <item name="android:navigationBarColor">@android:color/transparent</item>
</style>
```
<img src="s7.png" width="20%">-><img src="s2.png" width="20%">\
x->`<item name="android:navigationBarColor">@android:color/transparent</item>`\
https://github.com/material-components/material-components-android/blob/master/docs/components/BottomSheet.md#handling-insets-and-fullscreen

- [`android:windowLightNavigationBar`](https://developer.android.com/reference/android/R.attr#windowLightNavigationBar)
```xml
<style name="Theme.App" parent="Theme.Material3.DynamicColors.DayNight.NoActionBar">
    <item name="bottomSheetDialogTheme">@style/ThemeOverlay.App.BottomSheetDialog</item>
    <item name="android:enforceNavigationBarContrast">false</item>
</style>

<style name="ThemeOverlay.App.BottomSheetDialog" parent="ThemeOverlay.Material3.BottomSheetDialog">
    <item name="android:windowLightNavigationBar">?isLightTheme</item>
</style>
```
<img src="s2.png" width="20%">-><img src="s2.png" width="20%"> The app ran on Android 15 (Android Emulator).\
<img src="s18.png" width="20%">-><img src="s19.png" width="20%"> The app ran on Android 14 (Android Emulator).\
<img src="s8.png" width="20%">-><img src="s12.png" width="20%"> The app ran on Android 13 (Android Emulator).\
x->`<item name="android:windowLightNavigationBar">?isLightTheme</item>`\
https://issuetracker.google.com/issues/295296005,
https://github.com/material-components/material-components-android/issues/4293

🟥https://github.com/manabu-nakamura/app/blob/master/editor/src/main/java/com/github/manabu_nakamura/editor/MainActivity.java:
- edge-to-edge
```java
EdgeToEdge.enable(this);
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    getWindow().setNavigationBarContrastEnforced(false);
}
```
<img src="s11.png" width="20%">-><img src="s10.png" width="20%">-><img src="s1.png" width="20%">\
x->`EdgeToEdge.enable(this)`->`getWindow().setNavigationBarContrastEnforced(false)`\
https://developer.android.com/develop/ui/views/layout/edge-to-edge,
https://issuetracker.google.com/issues/326356902

> [!CAUTION]
> The app targeted Android 15 and ran on Android 15 (Android Emulator).\
> https://developer.android.com/about/versions/15/behavior-changes-15

🟥https://github.com/manabu-nakamura/app/blob/master/editor/src/main/res/values/themes.xml:
- `android:enforceNavigationBarContrast`
```xml
<style name="Theme.App" parent="Theme.Material3.DynamicColors.DayNight.NoActionBar">
    <item name="android:enforceNavigationBarContrast">false</item>
</style>
```
<img src="s16.png" width="20%">-><img src="s17.png" width="20%">\
x->`<item name="android:enforceNavigationBarContrast">false</item>`

🟥https://github.com/manabu-nakamura/app/blob/master/editor/src/main/java/com/github/manabu_nakamura/editor/MainActivity.java:
- edge-to-edge
```java
EdgeToEdge.enable(this);
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    getWindow().setNavigationBarContrastEnforced(false);
}
```
<img src="s13.png" width="20%">-><img src="s14.png" width="20%">-><img src="s15.png" width="20%">\
x->`EdgeToEdge.enable(this)`->`getWindow().setNavigationBarContrastEnforced(false)`\
https://developer.android.com/develop/ui/views/layout/edge-to-edge,
https://issuetracker.google.com/issues/326356902,
https://issuetracker.google.com/issues/362137845

[Manabu Nakamura](https://github.com/manabu-nakamura)