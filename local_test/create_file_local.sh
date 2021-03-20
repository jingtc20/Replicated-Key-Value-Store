#!/bin/bash

file="/tmp/nodes.cfg"
server="192.168.86.162"
port1=1234
port2=1235
port3=1236
port4=1237
port5=1238

if [ "$#" == "0" ]
then
    echo "Please input a number"
elif [ "$1" == "1" ]
then
    if [ -e "${file}" ]
    then
        rm ${file}
        echo "${server}:${port1}" > $file
        cat $file
    else
        echo "${server}:${port1}" > $file
        cat $file
    fi
elif [ "$1" == "3" ]
then
    if [ -e "${file}" ]
    then
        rm ${file}
        echo "${server}:${port1}" > $file
        echo "${server}:${port2}" >> $file
        echo "${server}:${port3}" >> $file
        cat $file
    else
        echo "${server}:${port1}" > $file
        echo "${server}:${port2}" >> $file
        echo "${server}:${port3}" >> $file
        cat $file
    fi
elif [ "$1" == "5" ]
then
    if [ -e "${file}" ]
    then
        rm ${file}
        echo "${server}:${port1}" > $file
        echo "${server}:${port2}" >> $file
        echo "${server}:${port3}" >> $file
        echo "${server}:${port4}" >> $file
        echo "${server}:${port5}" >> $file
        cat $file
    else
        echo "${server}:${port1}" > $file
        echo "${server}:${port2}" >> $file
        echo "${server}:${port3}" >> $file
        echo "${server}:${port4}" >> $file
        echo "${server}:${port5}" >> $file
        cat $file
    fi
fi



