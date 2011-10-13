# yuma4j

__yuma4j__ will be a collection of Java utilities complementing the __yuma.js__ media annotation
system, including a basic annotation storage server.

## Getting Started with the Demo Annotation Server

__yuma4j__ includes a bare-bones annotation storage server you can use to make annotations on your
site permanent. The demo server also provides a basic search GUI and a 'public timeline'
which shows the 20 most recent annotations in the system.

To point [yuma.min.js] (http://github.com/rsimon/yuma.min.js) to an annotation server, you need to
set the ``serverURL`` init parameter. The code sample below shows how this is done in case of image
annotation.

     <head>
       ...
       <script type="text/javascript">
       var annotationLayer;
       
       window.onYUMAready = function() {
         annotationLayer = new YUMA.ImageAnnotationLayer('annotateMe', { serverURL:"http://my.server.org/myserver" });
       }
       </script>
     </head>
 
Note that we have also set up a publicly accessible demo server instance at

http://dme.ait.ac.at/yuma4j-server

Feel free to point your page to this server __for testing purposes__. Keep in mind though that 
we __do not make any guarantees__ on this server in terms of 

* server uptime
* availability of your annotations - we may occasionally clear the database as we are developing
  yuma4j further
  
Furthermore, be aware that the server is __open to everyone__. This means anybody can search through
your annotations and see them in the public timeline. Private annotations and non-anonymous posting
are not (yet) supported by the demo server!

## Developer Info

yuma4j requires [Java 1.6] (http://www.java.com/de/download/index.jsp) and is 
built with [Gradle] (http://www.gradle.org/).

For those using Eclipse:

``gradle eclipse``

will generate Eclipse project files.

``gradle startDemoServer``

will launch the demo annotation storage server. The demo server runs with an embedded in-memory
database. There is no need to set up any additional components on your system. After launch,
it will be available at 

http://localhost:8081/yuma4j-server

yuma4j uses the [Hibernate] (http://www.hibernate.org/) database persistence framework.
You can switch out the demo server's in-memory database with a
[database of your choice] (http://community.jboss.org/wiki/SupportedDatabases2) by
editing the ``persistence.xml`` file located in the [src/main/resources/META-INF/]
(https://github.com/rsimon/yuma4j/tree/master/src/main/resources/META-INF) folder.

To deploy the demo server in you own environment, you will need a Java Servlet container
that implements the Servlet 2.4 specification, for example Tomcat 5.5 (or higher) or Jetty 6
(or higher). Use 

``gradle war`` 

To build the deployable Web archive (.war) file.
