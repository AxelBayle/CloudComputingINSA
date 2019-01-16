# CloudComputingINSA
During this Lab we had to develop several modules to manage CTs on proxmox servers.
The aim was to have one program that randomly generate CT, on one of our server, and another one that manages the CTs. 
In the following parts we will explain how the differents parts of the code works.

1. GeneratorMain

This is the program in charge of generating new CT. 
The first step is to print some information about the two serves such as CPU usage or RAM usage. Then if on both servers the memory usage is less than  16% we start creating a new CT.

The second step is to define the server and the CT parameters. The server is choose following a random function. This function is set to choose the server 5 66% of the time and on the server 6 33%of the times.

To determine the CTid we use the information given, depending on the group we are we had a range of 100 id possible. For us that was between 4600 and 4699. To do that we count the id from 0 to 99 modulo 100, and we had the CTid_base which equals 4600.
The name of the CT is directly defined using the CTid. Indeed the name of the CT is :
ct-tpgei-virt-D6-ctXXXX   with XXXX the CTid

The last step is the creation and the start of the CT. But first we had to check if there is another CT with the same id. If we found one, we check the state of this one. If it is running we stop it and we delete it, if it is not running we just delete it.
	Once the delete completed, we create and start the CT. 
	The main problem in this part was to have some time between each manipulation of the Ct because that take a lot of time. For example : we had to add a sleep time of 30 sec between the creation and the start.

At the end of the program, using a exponential law center on 30 sec we define the time between the next CT creation.
  
2 - Controller

Controller is the is the lower class level of the management process of our CTs. This class expose two methods : “migrateFromTo” and “offLoad”.

* “migrateFromTo” : this method start by checking the wake up time of all of our CTs on the source server and recovers the ID of the CT which has the higher wake up time. Then it will start the migration process by stopping that particular CT, then migrate the CT from the source server to the destination server and finally start the CT.

* “offLoad” : this method start by checking the wake up time of all of our CTs on the server. Then it will just stop the CT which have the higher wake up time. 

To check if a CT on the server is our, we make the list of all the CTs on the server and we then check for each of the CT if it name contains our CT_BASE_NAME. If yes, it is one of our CT according to our CT creation method on the GeneratorMain.

3 - Analyzer

The Analyse class provides two methods: the first one “getInfosmyCts” print informations about our CTs . To use it we had to provides the map of the CTs by server.
And, for each CT of the servers, we print the ID, the Name, the Memory usage, the Disk usage and the CPU usage.
	
The other methods is “analyze”, which also takes the map of the CTs on both servers. This method is the one in charge of the CTs management.
It starts by getting the memory used by all our CT on each servers. Then it calculate the threshold. It should be 8% and 12%, but, in practice, we use 3% and 5% because 8% and 12% needs a lot of CT running.
	
If, on one of the servers, the memory used by our CTs is higher than 12%  (5% in practice) we delete the older of our CT on this server using the “controller.offLoad”.
If on both servers, our CTs used more than 8% ( 3% in practice) we migrate a CT from the server with the higher memory use to the other. The goal is to equilibrate  the memory use on the two servers.
If on one server only the memory use is higher than the threshold than we migrate from this server to the other.
Finally, if the memory use on the two servers is lower than the threshold, nothing is done.

4 - Monitor

The Monitor class provides two methods : “run” and “deletemyCT”.
* “run” : this method start by making a map who associate the name of a serveur (here SERVER1 and SERVER2 which correspond to serveur 5 and server 6 on proxmox) with a list of all our CTs on that server. It then call the “getInfosmyCts” method and the  “analyze” method of the Analyser class with that map in argument. All those operations are on a while(true) loops with a particular sleep time between loops.
* “deletemyCT” : this method make a list of all our CTs on the SERVER1 (server 5) and SERVER2 (server 6). It then stop and delete all of our CTs one by one.
	
To make the list of our CTs, we start by making a list of all the CTs on the studied server. Then for each of the CT we check if it name contains our CT_BASE_NAME. If yes, it is one of our CT according to our CT creation method on the GeneratorMain. We then add this CT to our CT list.

5 - ManagerMain

The manager main is the program that we launch to manage the CTs. It called the monitor methods to do that. 
