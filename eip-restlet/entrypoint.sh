#!/bin/bash
consul agent -dev -client 0.0.0.0
cd /app
java -jar eip-restlet.jar