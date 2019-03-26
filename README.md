# ginnungagap
Cumulus Bevarings Service, aka. Ginnungagap!

Ginnungagap appears as the primordial void in the Norse creation... 


Installation and user guide at the IT-wiki of kb.dk.


Prerequisite
--------------------------
This requires [Yggdrasil](https://github.com/Det-Kongelige-Bibliotek/Yggdrasil).
* git clone https://github.com/Det-Kongelige-Bibliotek/Yggdrasil.git
* cd Yggdrasil
* mvn clean -Dmaven.test.skip=true install


Testing against Cumulus
--------------------------
The current tests against Cumulus required a password-file in the project folder named cumulus-password.yml

It must be a YAML file in the format: 
<pre>
login: $username
password: $password
</pre>
(where you have replace $username and $password with your actual login credentials).

If such a file does not exist, then the cumulus tests are ignored. 


Installing Cumulus libraries to Linux
--------------------------

* Download the Cumulus zip-file for Linux

* Unzip it, and run the installer (remember sudo)

* Fix the rights of the installation directory (`/usr/local/Cumulus_Java_SDK`) and if it isn't done automatically the JAR file (`/usr/local/Cumulus_Java_SDK/CumulusJC.jar`)
  * `chmod +xr` to read and execute access for your user

* Make your machine load and use the libraries
  * Go to `/etc/ld.so.conf.d`, create a new file for refering to the cumulus  (e.g. `cumulus-[version].conf`, or just `cumulus.conf`), and write the path to your cumulus library files in the new conf file. And then reload the libraries.
  * e.g. `echo "/usr/local/Cumulus_Java_SDK/lib" > cumulus.conf; sudo mv cumulus.conf /etc/ld.so.conf.d/.; sudo ldconfig -v`

