#!/bin/bash


mvn clean
mvn verify
cd target
java -jar GenericNode.jar
cd ..

