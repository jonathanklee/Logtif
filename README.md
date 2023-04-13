# Logtif

Logtif is a small library that can help you during your exploratory tests.

Indeed, you are not always in front of your computer and connected to adb looking at your logcat.

That's where Logtif enters the party.

Logtif allows you to easily create a notification for some INFO, WARNING or ERROR messages.

Initialize Logtif:

```
Logtif.init(context, "Your application")
```

Log some messages:

```
Logtif.log(Logtif.ERROR, "App reached an invalid state")
```
