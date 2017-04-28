![S-Update Logo](http://image.noelshack.com/fichiers/2015/18/1430286335-bannieresu.png)

[![Say Thanks!](https://img.shields.io/badge/Say%20Thanks-!-1EAEDB.svg)](https://saythanks.io/to/Litarvan)

## How to use it

First you need to install the [Server](http://github.com/Motarva,/S-Update-Server) on a Web Server.
Put your files in the files/ folder
Then, in your code, just insert that to update your program !

```java
SUpdate su = new SUpdate("UrlOfTheServer", new File("OutputFolder"));
su.start();
```

## Features

* Automatic files checking with MD5s
* Unknown files are deleted excepted the one listed in the server whitelist (config/ignore.list)
* Modified files are re-downloaded
* Multi-thread
* Progress bar API
