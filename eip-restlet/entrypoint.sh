#!/bin/bash
consul agent -data-dir=/consul/data -config-dir=/consul/config -dev -client 0.0.0.0
cd /app
java -jar eip-restlet.jar