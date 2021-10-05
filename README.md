## Route finder
Application using A* algorithm for finding shortest route within two countries.

### How to build and run
You have two options of build and run this application:
- using your locally installed maven and java
- using your locally installed docker

###### Option#1 - Using your locally installed maven and java

Switch to root project directory and run:

`mvn package && java -jar target/route-finder-*.jar`

##### Option#2 - Using your locally installed docker

Switch to root project directory and run:

`docker build -t route-finder . &&  docker container run -p 8080:8080 route-finder
`
