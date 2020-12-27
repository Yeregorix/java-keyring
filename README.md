java-keyring
============

This repo has been cloned from https://bitbucket.org/bpsnervepoint/java-keyring/.
The original creator is east301.

Summary
-------

java-keyring is a small library which provides a Java API to store passwords and secrets.
Currently, Mac OS X, Windows and Linux (GNOME) are supported.

* __Mac OS X__
    * Passwords are stored using [OS X Keychain](http://developer.apple.com/documentation/Security/).
* __Linux__
    * Passwords are stored using [GNOME Keyring](https://wiki.gnome.org/Projects/GnomeKeyring).
* __Windows__
    * Passwords are encrypted by [Data Protection API](http://msdn.microsoft.com/en-us/library/ms995355.aspx).

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
