### JTA sample for Java EE7

This application demonstrates the use of @Transactional annotations to declaratively control transaction boundaries, along with using the @TransactionScoped annotation to scope a bean lifecycle to a transaction.

### WAS Liberty

#### Maven

The sample can be built using [Apache Maven](http://maven.apache.org/). In the directory where you cloned the repository issue the following command to build the source.

$ mvn install

Then, in the jta-webapp directory issue the following command to run it on a Liberty server.

$ mvn liberty:run-server

#### WebSphere Development Tools (WDT)

The WebSphere Development Tools (WDT) for Eclipse can be used to control the server (start/stop/dump/etc.), it also supports incremental publishing with minimal restarts, working with a debugger to step through your applications, etc.
WDT also provides:
- content-assist for server configuration (a nice to have: server configuration is minimal, but the tools can help you find what you need and identify finger-checks, etc.)
- automatic incremental publish of applications so that you can write and test your changes locally without having to go through a build/publish cycle or restart the server (which is not that big a deal given the server restarts lickety-split, but less is more!).

Installing WDT on Eclipse is as simple as a drag-and-drop, but the process is explained on [wasdev.net](https://developer.ibm.com/wasdev/downloads/liberty-profile-using-eclipse/).

To import the source code into Eclipse/WDT:
1.	In the Enterprise Explorer view, right click and select Import -> Existing Maven Projects
2.	Browse... to the top level directory titled sample.javaee7.jta
3.	Verfiy all three boxes are checked and select Finish

### Manual Deployment
1.	Add following features to your server.xml 

  ```
  <feature>servlet-3.1</feature>
  <feature>jsonp-1.0</feature>
  <feature>jdbc-4.1</feature>
  <feature>jndi-1.0</feature>
  <feature>cdi-1.2</feature>
    ```

2.	Add following derby resources to your server.xml:
    
  ```
  <library id="DerbyLib">
        <fileset dir="<DERBY_LIB_DIR>" id="DerbyFileset" includes="derbyclient.jar"/>
  </library>
    ```
    
    ```
  <dataSource id="DefaultDataSource" jndiName="jdbc/derbyClient">
        <jdbcDriver libraryRef="DerbyLib"/>
        <properties.derby.client user="sample" password="sample" createDatabase="create" databaseName="<DB_DIR_LOCATION>" portNumber="<DB_PORT>" serverName="localhost"/>
  </dataSource>
    ```

3.  Install this sample app to your server by copying sample.javaee7.jta.war that was made from running mvn install
4.	Start the server.
5.	Run the sample by hitting the following URL using your servers hostname and port
[http://hostname:port/sample.javaee7.jta/](http://hostname:port/sample.javaee7.jta/)

### WAS Classic

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
