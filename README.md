## JTA sample for Java EE7 [![Build Status](https://travis-ci.org/WASdev/sample.javaee7.jta.svg?branch=master)](https://travis-ci.org/WASdev/sample.javaee7.jta)

This application demonstrates the use of @Transactional annotations to declaratively control transaction boundaries, along with using the @TransactionScoped annotation to scope a bean lifecycle to a transaction.

## WAS Liberty

### Maven

The sample can be built using [Apache Maven](http://maven.apache.org/). In the directory where you cloned the repository issue the following command to build the source.

    $ mvn install

Then, in the jta-webapp directory issue the following command to run it on a Liberty server.

    $ mvn liberty:run-server

#### WebSphere Development Tools (WDT)

The WebSphere Development Tools (WDT) for Eclipse can be used to control the server (start/stop/dump/etc.), it also supports incremental publishing with minimal restarts, working with a debugger to step through your applications, etc.
WDT also provides:
- content-assist for server configuration (a nice to have: server configuration is minimal, but the tools can help you find what you need and identify finger-checks, etc.)
- automatic incremental publish of applications so that you can write and test your changes locally without having to go through a build/publish cycle or restart the server (which is not that big a deal given the server restarts lickety-split, but less is more!).
- improved Maven integration for web projects starting with WDT 17.0.0.2 including support for loose applications.

Installing WDT on Eclipse is as simple as a drag-and-drop, but the process is explained on [wasdev.net](https://developer.ibm.com/wasdev/downloads/liberty-profile-using-eclipse/).

#### Import project and running in Eclipse/WDT:

1.	Select menu *File -> Import -> Maven -> Existing Maven Projects*.
2.	Select *Browse...* to the top level directory titled sample.javaee7.jta and select *Finish*.
3.	Click *Yes* to the WebSphere Liberty dialog to automatically create server in the Servers view for this project.
4.  Right-click the project and select *Run As > Run on Server*.
5.  Select the server and click *Finish*.
6.  Confirm web browser opens with the sample url: [http://hostname:port/sample.javaee7.jta/](http://hostname:port/sample.javaee7.jta/)

### Gradle

The sample can be built using [Gradle](https://gradle.org/) and the [Liberty Gradle Plug-in][]. In the directory where you cloned the repository issue the following command to build and run the project.

    $ gradle build

To start the application use the command:

    $ gradle libertyStart

To stop the application use the command:

    $ gradle libertyStop

## WAS Classic

#### Configure required resources

1. Verify that a Derby JDBC Provider instance exists. In the administrative console, click Resources > JDBC > JDBC providers.

  - If that provider does not exist, create one with a Connection pool datasource implementation type, and point to the Derby.jar file; for example: ${WAS_INSTALL_ROOT}/derby/lib

2. Verify that a Default datasource instance is configured. Click Resources > JDBC > Data sources.

  - If that datasource does not exist, create one with the name "Default datasource" and the JNDI name "DefaultDatasource" that points to the Derby JDBC Provider and "${WAS_INSTALL_ROOT}/derby/DefaultDB" database.
  - To create the actual database, remotely connect to your machine hosting WebSphere Classic using SSH.

    - Navigate to ${WAS_INSTALL_ROOT}/derby/bin/embedded/

    - Run "./ij.sh". When you see the prompt "ij>", enter the following command:
        $ connect 'jdbc:derby:DefaultDB;create=true';

    - The default Derby database is created in the following directory: ${WAS_INSTALL_ROOT}/derby/DefaultDB

### Install using the Administrative Console
1.	In your preferred browser, go to the Integrated Solutions Console; for example: [http://hostname:9060/ibm/console/](http://hostname:9060/ibm/console/)
2.	Log in with your user name and password.
3.	Select Applications > New Application.
4.	Select the New Enterprise Application link.
5.	Using the Local file system option, click Browse, and select the war file that you built using Maven.
6.	Click Next to follow the wizard using the default options, until the Finish button is displayed.
7.	When the Confirm changes section is displayed, click Save.
8.	Click Applications > Application Types > WebSphere enterprise applications.
9.	Select the check box next to the sample application, and click Start.
# Notice

© Copyright IBM Corporation 2016, 2017.

# License

```text
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
````

[Liberty Maven Plug-in]: https://github.com/WASdev/ci.maven
[Liberty Gradle Plug-in]: https://github.com/WASdev/ci.gradle
