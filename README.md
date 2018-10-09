# :star2: Camp
Intelligent notifications for mobile devices

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
* turbine-service - provides application for turbine application (hystrix metrics)
* monitoring - provides application for hystrix metrics monitoring

Technologies:
* Spring Boot
* Spring Cloud Config
* Spring Cloud Eureka
* Spring Cloud OpenFeign
* Spring Cloud Hystrix
* Spring Cloud Turbine
* Spring Cloud Stream
* Sprng Data Cassandra
* Apache Spark
* Apache Kafka
* Cassandra

