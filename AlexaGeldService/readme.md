# Links
[http://localhost:8080/confirmationCode?customerId=Erik&confirmationCode=1234](http://localhost:8080/confirmationCode?customerId=Erik&confirmationCode=1234)  
[http://localhost:8080/cashAccountBalance?customerId=Erik](http://localhost:8080/cashAccountBalance?customerId=Erik)  
[http://localhost:8080/cashAccountBalance?customerId=Tania](http://localhost:8080/cashAccountBalance?customerId=Tania)    

[http://edittrich.de:38080/confirmationCode?customerId=Erik&confirmationCode=1234](http://edittrich.de:38080/confirmationCode?customerId=Erik&confirmationCode=1234)  
[http://edittrich.de:38080/cashAccountBalance?customerId=Erik](http://edittrich.de:38080/cashAccountBalance?customerId=Erik)  
[http://edittrich.de:38080/cashAccountBalance?customerId=Tania](http://edittrich.de:38080/cashAccountBalance?customerId=Tania)  

[https://github.com/hbci4j/hbci4java/](https://github.com/hbci4j/hbci4java/)

# Maven
## Goals
 
spring-boot:run  
package

## Configuration

settings.xml:
   
    <profile>
        <id>allow-snapshots</id>
        <activation><activeByDefault>true</activeByDefault></activation>
        <repositories>
            <repository>
                <id>snapshots-repo</id>
                <url>https://oss.sonatype.org/content/repositories/snapshots</url>
                <releases><enabled>false</enabled></releases>
                <snapshots><enabled>true</enabled></snapshots>
             </repository>
        </repositories>
    </profile> 


# Execution
    sh -c 'trap "" HUP; java -jar erik-service-0.9.jar' &