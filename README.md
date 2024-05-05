# Java_Docker_Example 

This is only a bare minimal sample repository to demonstrate integration between java, RabbitMQ, Mongo and MSSQL through docker and docker-compose for the purposes of teaching beginners.

### Features

* Git for version control

* Messaging Tier - through RabbitMQ. 

* Data Tier - MSSQL

* Web Tier - Sample Java HTTP Server to to interface with Messaging and Data tiers

* Vagrant to simplify development setup so docker runs on a ubuntu VM

### Launching

First you need to bring up the VM through vagrant
```
vagrant up
```

Then you need to ssh onto the VM
```
vagrant ssh
```

Next you have to change to the Java_Docker_Example folder
```
cd /home/vagrant/Java_Docker_Example
```

Next you have to run docker-compose to bring all the containers up
```
docker-compose up
```

It can take a minute or so for everything to start, make sure to watch for any errors in the logs.

Once you have verified things are working then to bring down you can run the following:
```
docker-compose down
```

Subsequent runs are best launched in the detached mode:
```
docker-compose up -d
```

Once up you must create the development database on MSSQL. This can be done by either logging in through a database tool such as management studio or by logging into the container directly and creating via the command line tool sqlcmd:

```
$ docker exec -it mssql /opt/mssql-tools/bin/sqlcmd -U sa -P Passw0rd
1> CREATE DATABASE development;
2> GO
1> exit
```

### Testing 

After 5mns or so when the RabbitMQ listeners are up, and the node servers have scaled up to 3 then the app is ready to send some user actions. This can be done using the example powershell script from the local machine 

Healthcheck against the Java web API should return
```
$ curl -s -X GET http://192.168.20.102:3000/health
running
```

Publishing a message to RabbitMQ
```
$ curl -s -X GET http://192.168.20.102:3000/message/publish
Published '2024/05/05 23:29:04'
```

Creating a person in the MSSQL@development/dbo.Person table 
```
$ curl -s -X POST -H "Content-Type: application/json"  -d '{"name":"Bruce","age":39}' http://192.168.20.102:3000/person
Created person Bruce aged 39
```

### TODO 
Mongo is running under docker-compose but I didn't have time to write the Java to interface with this.