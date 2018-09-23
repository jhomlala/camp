#!/bin/bash

echo "Running service : $1"

if [ "$1" = "config" ]
then
    echo "Stop config service"
    process="[c]onfig"
fi
if [ "$1" = "registry" ]
then
    echo "Stop registry service"
    process="[r]egistry"
fi
if [ "$1" = "application-service" ]
then
    echo "Stop application service"
    process="[a]pplication-service"
fi
if [ "$1" = "application-user-service" ]
then
   echo "Stop application user service"
    process="[a]pplication-user-service"
fi
if [ "$1" = "notification-service" ]
then
   echo "Stop notification service"
    process="[n]otification-service"
fi
if [ "$1" = "notification-sender-service" ]
then
   echo "Stop notification sender service"
    process="[n]otification-sender-service"
fi
if [ "$1" = "user-event-service" ]
then
    echo "Stop user event service"
    process="[u]ser-sender-service"
fi
if [ ${#process} -ge 1 ]; then
	echo "Stopping service: $process"
    kill $(ps aux | grep $process | awk '{print $2}')
    
	echo "Stopped service"
else
	echo "Invalid service. Can't stop"
fi

