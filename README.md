# 318 Final Group Project 

## Overview
12 total use cases, 9 HTTP requests, 4 Kafka events

## Setup and Running the Application

### 1. Start Kafka Services

First, start the Kafka services (ZooKeeper and Kafka broker):

#### Zookeeper:
- For MacOS/Linux:
  ```
  bin/zookeeper-server-start.sh config/zookeeper.properties
  ```
- For Windows:
  ```
  .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
  ```

#### Kafka Broker:
- For MacOS/Linux:
  ```
  bin/kafka-server-start.sh config/server.properties
  ```
- For Windows:
  ```
  .\bin\windows\kafka-server-start.bat .\config\server.properties
  ```

### 2. Create Kafka Topics

After both Kafka Broker and Zookeeper are running, create the required topics (only do this once):

kafka-topics.sh --create --topic user-created-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
kafka-topics.sh --create --topic event-created-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
kafka-topics.sh --create --topic event-updated-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
kafka-topics.sh --create --topic booking-confirmed-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

### 3. Run Microservices

Run the microservices using either IntelliJ or Maven:

## Use Cases 

### User Service 

#### Creating a User 
No spaces in username
Email must be formatted correctly example xyz@example.com
Admin Permission Must be set 'true', or 'false'

curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d "{\"username\":\"username\", \"email\":\"user@uowmail.edu.au\", \"isAdmin\":true}"


#### Getting a user 


Fetch user data based on Id

curl -X GET http://localhost:8080/users/{id}


#### Updating a User 

Request Header asks for User-id which checks if admin 
and then edits accordingly

curl -X PUT http://localhost:8080/users/{id} -H "Content-Type: application/json" -H "User-Id: 1" -d "{\"username\":\"updateduser\",\"email\":\"updated@example.com\",\"isAdmin\":true}"


### Event Service

#### Create a Event 


The user creating the event must be an admin in order to create the event. The user is tagged in the header
A date cannot be in the past a date in the past cannot be created 
The date follows the format YYYY-MM-DD

curl -X POST http://localhost:8081/events -H "Content-Type: application/json" -H "User-Id: 1" -d "{\"name\":\"Meeting\",\"date\":\"2025-01-01\",\"location\":\"City\",\"description\":\"Good things happens here\"}"


#### Get an event 

Fetch Data about an event by ID

curl -X GET http://localhost:8081/events/{id}


#### Update an Event

The user editing the event must be a admin, the user making the request is tagged in the header 
The date cannot be in the past 
The date follows the format YYYY-MM-DD

curl -X PUT http://localhost:8081/events/{id} -H "Content-Type: application/json" -H "User-Id: 1" -d "{\"name\":\"New Meeting\",\"date\":\"2026-06-26\",\"location\":\"New York City\",\"description\":\"better things happen here\"}"



### Booking Service

#### Create a booking 

The event must be a real Event interacting with the Event Service
The booking must be created by a valid userID interacting with the User service

curl -X POST http://localhost:8082/api/bookings -H "Content-Type: application/json" -d "{\"userId\": 1, \"event\": \"Meeting\", \"numberOfTickets\": 2, \"tickets\": [{\"seatNumber\": \"A1\", \"price\": 50.00}, {\"seatNumber\": \"A2\", \"price\": 50.00}]}"


#### Get a booking 

curl -X GET http://localhost:8082/api/bookings/{id}


#### Delete a booking 

The user deleting the booking must be the owner of the booking or an admin 

curl -X DELETE http://localhost:8082/api/bookings/1 -H "User-Id: 1"



### Notification Service 

This service uses apache kafka to generate notifications when actions occur on other services allowing for seemless real time communication.

Notifications occur on the following events

    EVENT_CREATED,
    EVENT_UPDATED,
    BOOKING_CONFIRMED,
    USER_CREATED




