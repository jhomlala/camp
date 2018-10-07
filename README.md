# camp
:star2: Camp - notifications for mobile devices

Build in microservices architecture, using Spring Boot, Cassandra and Spark.

Modules:
* application-service - handles mobile application operations
* application-user-service - handles mobile application users operations
* user-event-service - handles users events from mobile application
* notification-service - handles creating and managing push notification operations
* notification-sender-service - handles sending message to FCM server
* spark-service - handles big data processing/machine learning operations
* config - stores config for each services
* registry - provides discovery server for services

Technologies:
* Spring Boot
* Spring Cloud Config
* Spring Eureka
* Spring OpenFeign
* Sprng Data Cassandra
* Apache Spark
* Cassandra

