java-keyring
=============

Summary
-------

java-keyring is a small library which provides java API to store password etc. securely.
Currently Mac OS X, Windows and Linux (GNOME) are supported.

* __Mac OS X__
    * Passwords are stored using [OS X Keychain](http://developer.apple.com/documentation/Security/).
* __Linux__
    * Passwords are stored using [GNOME Keyring](https://wiki.gnome.org/Projects/GnomeKeyring).
* __Windows__
    * Passwords are encrypted by [Data Protection API](http://msdn.microsoft.com/en-us/library/ms995355.aspx).

If you find bug, please let me know via [issue tracker](http://bitbucket.org/east301/java-keyring/issues)
or twitter [@east301](http://twitter.com/east301). In addition, any patch/modification is highly welcome.



Source code tree
----------------

java-keyring package contains the following directories

* __java-keyring__ directory
    * java-keyring library source code
* __java-keyring-example__ directory
    * usage example of java-keyring library

Building
--------

```
mvn clean install
```

License
-------

Source code of java-keyring and java-keyring-example are available under modified BSD license. 
See the file LICENSE for more details.


Special Thanks
--------------

java-keyring uses the following library, thanks a lot!
java-keyring package contains copy of compiled JNA library. 
Source code of the library is available at its project page.

* [Java native access (JNA)](https://github.com/twall/jna)
