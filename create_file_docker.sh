#!/bin/bash

file="/tmp/nodes.cfg"
server1="172.17.0.3"
server2="172.17.0.4"
server3="172.17.0.5"
port=1234

if [ -e "${file}" ]
then
    rm ${file}
    echo "${server1}:${port}" > $file
    echo "${server2}:${port}" >> $file
    echo "${server3}:${port}" >> $file
    cat $file
else
    echo "${server1}:${port}" > $file
    echo "${server2}:${port}" >> $file
    echo "${server3}:${port}" >> $file
    cat $file
fi
