<p align="left"><img src="https://github.com/jonathanklee/Logtif/blob/main/notification.png" width="200"/></p>

# Logtif

Logtif is a small library that can help you during your exploratory tests.

Indeed, you are not always in front of your computer and connected to adb looking at your logcat.

That's where Logtif enters the party.

Logtif allows you to easily create a notification to display some INFO, WARNING or ERROR messages.

# Build

[![](https://jitpack.io/v/jonathanklee/Logtif.svg)](https://jitpack.io/#jonathanklee/Logtif)

Add Jitpack in your repositories:

In Groovy:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

In Kotlin:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io")
    }
}
```

Add the Logtif dependency:

In Groovy:

```
dependencies {
    implementation 'com.github.jonathanklee:Logtif:VERSION'
}
```

In Kotlin:

```
dependencies {
    implementation("com.github.jonathanklee:Logtif:VERSION")
}
```

# Usage

Initialize Logtif:

```
Logtif.init(context, "MyAppChannel")
```

Log some messages:

```
Logtif.log(Logtif.ERROR, "App reached an invalid state %d", state)
```

:memo: For Android >= 13, make sure that the application using Logtig has requested the runtime permission [android.permission.POST_NOTIFICATIONS](https://developer.android.com/reference/android/Manifest.permission#POST_NOTIFICATIONS)

# Credits

<a href="https://www.flaticon.com/free-icons/notification" title="notification icons">Notification icons created by Pixel perfect - Flaticon</a>
