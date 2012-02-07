#!/bin/bash

REV=`curl -s -X GET 'http://localhost:5984/java/classpath' | sed -e 's/.*"_rev":"\([^"]*\)".*/\1/'`
echo ${REV}
curl -s -X DELETE "http://localhost:5984/java/classpath?rev=${REV}"

RES=`curl -s -H "Content-Type: application/octect-stream" -X PUT 'http://localhost:5984/java/classpath/views.jar' --data-binary @views/target/views-0.0.1-SNAPSHOT-jar-with-dependencies.jar`

REV_NEW_JAR="$(echo ${RES} | sed -e 's/.*"rev":"\([^"]*\)".*/\1/')"

echo "Second: $REV_NEW_JAR"

DD_REV=`curl -H "Content-Type: application/json" -s -X GET 'http://localhost:5984/java/_design/java' | sed -e 's/.*"_rev":"\([^"]*\)".*/\1/'`
echo "Deleting existing design doc."
curl -s -X DELETE "http://localhost:5984/java/_design/java?rev=${DD_REV}"

echo "Adding new design doc."
#curl -H "Content-Type: application/json" -X POST 'http://localhost:5984/java' -d "{\"_id\":\"_design/java\",\"language\":\"java\",\"views\":{\"resource-by-date\":{\"map\":\"{\\\"classpath\\\":[\\\"http://127.0.0.1:5984/java/classpath/views.jar\\\"],\\\"rev\\\":\\\"${REV_NEW_JAR}\\\",\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.views.ResourceDataByDateView\\\"}\"},\"simple\":{\"map\":\"{\\\"classpath\\\":[\\\"http://127.0.0.1:5984/java/classpath/views.jar\\\"],\\\"rev\\\":\\\"${REV_NEW_JAR}\\\",\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.views.SampleView\\\"}\"}}}"
curl -H "Content-Type: application/json" -X POST 'http://localhost:5984/java' -d "{\"_id\":\"_design/java\",\"language\":\"java\",\"views\":{\"xmlvalidate\":{\"map\":\"{\\\"classpath\\\":[\\\"http://127.0.0.1:5984/java/classpath/views.jar\\\"],\\\"rev\\\":\\\"${REV_NEW_JAR}\\\",\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.views.XMLSchemaValidationView\\\"}\"},\"simple\":{\"map\":\"{\\\"classpath\\\":[\\\"http://127.0.0.1:5984/java/classpath/views.jar\\\"],\\\"rev\\\":\\\"${REV_NEW_JAR}\\\",\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.views.SampleView\\\"}\"}}}"

#echo "Restart Couch."
#curl -H "Content-Type: application/json" -X POST 'http://localhost:5984/_restart'
echo "Done"


