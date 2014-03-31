#!/bin/sh

curl -POST -H "Content-Type: application/json" -d '{"deviceId":"0001", "name":"Alan", "model": "iPhone", "os": "ios 4.1", "manufacturer": "Apple","mappingDetails":[]}' 'http://localhost:8080/devices'

curl -POST -H "Content-Type: application/json" -d '{"deviceId":"0002", "name":"Brian", "model": "iPhone", "os": "ios 4.1", "manufacturer": "Apple","mappingDetails":[]}' 'http://localhost:8080/devices'

curl -POST -H "Content-Type: application/json" -d '{"deviceId":"0003", "name":"Charles", "model": "iPhone", "os": "ios 4.1", "manufacturer": "Apple","mappingDetails":[]}' 'http://localhost:8080/devices'

curl -POST -H "Content-Type: application/json" -d '{"loc": {"longitude": -0.187969, "latitude":51.509918}, "timeCreated":"2013-02-12T11:39:43.618"}' 'http://localhost:8080/devices/0001/locations'

curl -POST -H "Content-Type: application/json" -d '{"loc": {"longitude": -0.175180, "latitude":51.511307}, "timeCreated":"2013-02-13T11:00:00.000"}' 'http://localhost:8080/devices/0002/locations'

curl -POST -H "Content-Type: application/json" -d '{"loc": {"longitude": -0.152349, "latitude":51.503400}, "timeCreated":"2013-02-13T10:00:00.000"}' 'http://localhost:8080/devices/0003/locations'

