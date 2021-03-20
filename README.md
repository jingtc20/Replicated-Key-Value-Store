# Replicated-Key-Value-Store
Build a TCP client/server to support replication using two-phase commit algorithm.

## Devlopment Environment
- programming language and version: Java 8.0
- IDE used to develop the project: Eclipse 2020-12
- Operating System: macOS 
- Membership Tracking Method: Dynamic config file

## Build maven project:
- Execute `./maven.sh` 

## Testing on Docker containers:
1. Execute `./config.sh <number of TCP Servers>`
2. Execute `sudo docker exec -it <CONTAINER ID> bash`. Use `ifconfig` command to get the local IP address of TCP Server
3. Execute `./create_file_docker.sh` to create the file “/tmp/nodes.cfg” 
4. Use `cd docker_client` command, and then execute `./bigtest_tc.sh <TCP server IP address>` 

## Testing on localhost
1. Execute `cd local_test` command
2. Execute `./create_file_local.sh <number of TCP Servers>` to create the file “/tmp/nodes.cfg” 
3. Open n terminals (n is the number of TCP Servers) and execute `java -jar GenericNode.jar ts <port>` in each terminal
4. Execute `time bigtest_tc.sh > /dev/null` 
