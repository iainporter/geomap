

geo1
====


To run locally:

1. pull the source
2. Ensure mongod is running
3. sbt run


To Add Devices:

 curl -POST -H "Content-Type: application/json" -d '{"deviceId":"123456-AAA", "name":"my iPhone", "model": "iPhone", "os": "ios 4.1", "manufacturer": "Apple","mappingDetails":[]}' 'http://localhost:8080/devices'

 to Get Devices

 curl -v -H "Content-Type: application/json" 'http://localhost:8080/devices'

To Add Locations:

curl -POST -H "Content-Type: application/json" -d '{"longitude": -2.5812800, "latitude":52.8409093}' 'http://localhost:8080/devices/123456780/locations'


geomap
======

provides a browser view of all items in the database.

To run the webapp locally:

1. pull the source
2. cd geomap
3. gradle jettyRunWar

note: you must have internet connectivity of the browser will not display.
The app is currently using a personal API key which has restricted number of calls to google maps api.

4. point your browser at: http://localhost:9000/geomap/map.html

The map uses AJAX to pull coordinates from the db.  The coords are refreshed every 5 seconds.


