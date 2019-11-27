./mvnw clean
./mvnw thrift:compile -f Thrift-Server/pom.xml
./mvnw thrift:compile -f Thrift-Client/pom.xml
./mvnw compile

./mvnw exec:java -Dexec.mainClass="com.leozinho.thrift.server.LogEventServer" -f Thrift-Server/pom.xml