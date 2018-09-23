#!/bin/bash

echo "Running service : $1"

if [ "$1" = "config" ]
then
    echo "Start config service"
    file=$(find ../config/target/ -type f -name "*.jar")
fi
if [ "$1" = "registry" ]
then
    echo "Start registry service"
    file=$(find ../registry/target/ -type f -name "*.jar")
fi
if [ "$1" = "application-service" ]
then
    echo "Start application service"
    file=$(find ../application-service/target/ -type f -name "*.jar")
fi
if [ "$1" = "application-user-service" ]
then
    echo "Start application user service"
    file=$(find ../application-user-service/target/ -type f -name "*.jar")
fi
if [ "$1" = "notification-service" ]
then
    echo "Start notification service"
    file=$(find ../notification-service/target/ -type f -name "*.jar")
fi
if [ "$1" = "notification-sender-service" ]
then
    echo "Start notification sender service"
    file=$(find ../notification-sender-service/target/ -type f -name "*.jar")
fi
if [ "$1" = "user-event-service" ]
then
    echo "Start user event service"
    file=$(find ../notification-user-event-service/target/ -type f -name "*.jar")
fi
if [ ${#file} -ge 1 ]; then
	echo "Starting jar: $file"
	nohup java -jar $file >/dev/null 2>&1 &
	echo "Started in nohup mode"
else
	echo "No jar found! Please build jar for given service"
fi

