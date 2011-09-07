# yuma4j

__yuma4j__ will be a collection of Java utilities complementing the __yuma.js__ media annotation system,
including a basic annotation storage server.

## TODOs

* Update this file!
* Replace MongoDB JSON utils with Jackson (http://jackson.codehaus.org/)
* Pagination in annotation list views
* Revise feed API
* Split FormatHandler into two separate classes (for parsing and serialization).
* JSON and RDF export of list view contents (search results, feed pages)
* Think about a 'dump database' feature (in the admin section)
* Last but not least: more & better unit tests. Also look into Wicket tooling for
  Web tests.

## Getting Started

Coming soon...

## Developer Info

yuma4j is built with [Gradle] (http://www.gradle.org/). For those using Eclipse:

``gradle eclipse``

will generate Eclipse project files. 

The included annotation storage server requires Java 1.6, a Java Servlet container
that implements the Servlet 2.4 specification such as Tomcat 5.5 (or higher), and a
relational database such as MySQL or PostgreSQL.

## After Deployment

After deployment, YUMA Server provides various Web pages and HTTP end-points.

* A REST interface for annotation CRUD operations in JSON format is 
  available at http://localhost:8080/yuma-server/api (or similar, depending
  on your setup). For format documentation, please consult source code.
  
* Various annotation RSS feeds are available at 
  http://localhost:8080/yuma-server/feeds
  Consult source code for details and available feeds. 

* The Web fronted is available at the root path
  http://localhost:8080/yuma-server (or similar)
  and provides access to annotation search.
  
* There are also Web pages for each available RSS feed. E.g. the public
  timeline is available at
  http://localhost:8080/pages/Timeline
  Consult source code for details and further available pages.
