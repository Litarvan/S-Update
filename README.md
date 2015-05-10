[![S-Update Logo](http://theshark34.github.io/S-Update-Server/logo.png)](http://theshark34.github.io/S-Update/)

## How to use it

First you need to install the [Server](http://github.com/TheShark34/S-Update-Server) on a Web Server.
Then, in your code, just insert that to update your program !

```java
SUpdate su = new SUpdate("UrlOfTheServer", new File("OutputFolder"));
su.update();
```

## Features

* Files checking with last modified dates or MD5s
* Unknown files are deleted excepted the one listed in the server ignore file
* Modified files are re-downloaded
* Zips files for those that need not to be checked