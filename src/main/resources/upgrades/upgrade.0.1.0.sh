#!/bin/bash

rm *.jar
wget https://s3-us-west-1.amazonaws.com/charbot-dl/client/charbox-client-0.1.0-SNAPSHOT.jar

alias run_client=java -jar charbox-client-0.1.0-SNAPSHOT.jar client