#!/bin/bash

DOCKER_SERVER_PATH="docker_server"
DOCKER_CLIENT_PATH="docker_client"


# Show the information of docker containers
show_docker() {
    echo "--------------------Display all the docker images----------------------"
    sudo docker images
    echo "--------------------Display all the running containers----------------------"
    sudo docker ps -a
}

# Delete all the docker containers and docker images
clean() {
    echo "--------------------Stop all the docker containers----------------------"
    container_id=`sudo docker ps -a -q`
    echo ${container_id}
    if [ -n "${container_id}" ]
    then
        for id in ${container_id}
        do
            sudo docker stop ${id}
            echo "docker container ${id} has been stopped"
        done
    fi

    echo "--------------------Remove all the docker images----------------------"
    image_id=`sudo docker images -q`
    echo ${image_id}

    if [ -n "${image_id}" ]
    then
        for id in ${image_id}
        do
            sudo docker rmi -f ${id}
            echo "docker image ${id} has been removed"
        done
    fi
}

# Delete all the docker containers and show the information of docker containers
clean
show_docker

# Build TCP cilent docker container and run it
cd $DOCKER_CLIENT_PATH
sudo docker build -t tcss558client .
echo "build the TCP_client docker container"
sudo docker run -d --rm tcss558client
echo "run the TCP_client docker container"
cd ..

# Build all the TCP server docker containers
if [ "$#" == "0" ]
then
    echo "usage"
else
    for i in `seq 1 $1`;
    do
        cd ${DOCKER_SERVER_PATH}
        sudo docker build -t tcss558server_${i} .
        echo "build the TCP_server docker container ${i}"

        sudo docker run -d --rm tcss558server_${i}
        echo "run the TCP_server docker container ${i}"
        cd ..
    done
fi

# Show the information of docker containers which have been built
show_docker



