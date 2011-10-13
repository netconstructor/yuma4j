# yuma4j

__yuma4j__ will be a collection of Java utilities complementing the __yuma.js__ media annotation
system, including a basic annotation storage server.

## Developer Info

yuma4j is built with [Gradle] (http://www.gradle.org/). For those using Eclipse:

``gradle eclipse``

will generate Eclipse project files.

``gradle startDemoServer``

will launch the demo annotation storage server. After launch, it will be available at 

http://localhost:8081/yuma4j-server

## Getting Started with the Demo Annotation Server

__yuma4j__ includes a basic annotation storage server you can use to make annotations on your
site persistent. To point [yuma.min.js] (http://yuma-js.github.com) to the annotation server,
you need to setting the ``serverURL`` init parameter. The code sample below shows how this 
would be done in case of image annotation.

     <head>
       ...
       <script type="text/javascript">
       var annotationLayer;
       
       window.onYUMAready = function() {
         annotationLayer = new YUMA.ImageAnnotationLayer('annotateMe', { serverURL:"http://my.server.org/annotation" });
       }
       </script>
     </head>
 
