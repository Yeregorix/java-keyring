java-keyring
============

This repo has been cloned from https://bitbucket.org/bpsnervepoint/java-keyring/.
The original creator is east301.

Summary
-------

java-keyring is a small library which provides a Java API to store passwords and secrets.
Currently, Mac OS X, Windows and Linux (GNOME) are supported.

Storage
-------

Passwords are stored using the following services.

* __Mac OS X__: [OS X Keychain](http://developer.apple.com/documentation/Security/)
* __Linux__: [GNOME Keyring](https://wiki.gnome.org/Projects/GnomeKeyring)
* __Windows__: [Windows Credential Manager](https://support.microsoft.com/en-us/windows/accessing-credential-manager-1b5c916a-6a16-889f-8581-fc16e8165ac0)

Caution
-------

Data on disk is encrypted which is way better than storing clear data.
However, keep in mind that if your user session is compromised by an attacker
then he will be able to decrypt data, in the same way you do.
This is true for this library but also for any application pretending to store secrets without a main password.

Special Thanks
--------------

java-keyring uses the following library, thanks a lot!
Source code of the library is available at its project page.

* [Java Native Access](https://github.com/java-native-access/jna)
