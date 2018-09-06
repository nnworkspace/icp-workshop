# Lab 1 - Docker Basics


- Docker Introduction
- Build a Docker Image
- Docker Container Life-cycle
- Access an application in a running container
- Set environment variables in a container
- Tail log files and execute bash inside a running container
- Start multiple containers


## 1.0 Docker Introduction

- Start "top" utility to display Linux processes inside an ubtuntu container

      docker container run --name ubuntu-top -t ubuntu top

    Only process "top" is visible.


- Open second terminal and check the container "ubuntu-top" is running

      docker container ls

    OR

      docker container ls -f name=ubuntu*

- Discover container details, like Hostname, IP address, ...

      docker container inspect ubuntu-top

    OR

      docker inspect --format='{{.Config.Hostname}}' ubuntu-top

- Execute a bash process inside the container "ubuntu-top" and observe "top" output in first terminal showing now two processes (top and bash)

      docker container exec -it ubuntu-top bash

  Type exit in bash and Ctrl-C to exit the "ubuntu-top" container

- Stop and delete the "ubuntu-top" container

      docker container stop ubuntu-top
      docker container rm ubuntu-top

## 1.1 Build a Docker Image

- Check local repository for existing images

      docker image ls

    OR

      docker images

    OR

      docker images "ibmcom/*"

- Clone ICP Workshop git repository

      git clone https://github.com/kkomp/icp-workshop.git

      cd ~/icp-workshop/Lab1/app

- Build OpenLiberty MicroProfile REST sample application (rest.war) with Maven

      mvn install

  + More information about OpenLiberty https://openliberty.io/
  + More information about Mircoprofile https://microprofile.io/


- Build OpenLiberty Docker Image and check local image repository

      cd ~/icp-workshop/Lab1
      docker build -t liberty-project:1.0.0 .

      docker images "liberty-project*"

## 1.2 Docker Container Life-cycle

- Start container based on previously built image

      docker run --name liberty-project -d -p 9081:9081 liberty-project:1.0.0

- Check the status of the started container

      docker ps | grep "liberty-project"

- Stop the container and check the list of running containers

      docker container stop liberty-project
      docker ps | grep "liberty-project"

- Start the container and check the list of running containers

      docker container start liberty-project
      docker ps | grep "liberty-project"

## 1.3 Access an application in a running container

- Use curl to access the application inside the running container via the mapped port

      curl http://localhost:9081/liberty-project/System/health
      curl http://localhost:9081/liberty-project/System/properties

- Stop and delete the container

      docker container stop liberty-project
      docker container rm  liberty-project

    OR force the removal of a running container (via SIGKILL):

      docker container rm -f liberty-project

## 1.4 Set environment variables in a container

- Set environment variables in the command line and start a container

      docker run --name liberty-project -d -p 9081:9081 -e LIBERTY_PROJECT_ENV="foo" liberty-project:1.0.0

    OR passing a filename with environment variables as key-values pairs

      docker run --name liberty-project -d -p 9081:9081 --env-file liberty-project.env liberty-project:1.0.0

      cat liberty-project.env
      LIBERTY_PROJECT_ENV=foo
      LIBERTY_PROJECT_NAME=OpenLiberty


- Use curl to check the new environment variable

      curl http://localhost:9081/liberty-project/System/properties

    OR open the URL in the browser

## 1.5 Tail log files and execute bash inside a running container

- Tail log files (STDOUT) of a running container

      docker logs -f liberty-project

  Use CTRL-C to stop tailing log files

- Execute command/process (e.g. bash) in a running container

      docker exec -it liberty-project bash

  Examine all running processes in this container with `ps auxwww`. Only the OpenLiberty "defaultServer" Java process apart from `bash` and `ps auxwww` is visible.

- Type `exit` to terminate bash

- Stop and remove the running container

      docker container stop liberty-project
      docker container rm  liberty-project

## 1.6 Start multiple containers using different port mappings

- Start multiple containers using different port mappings

      docker run --name liberty-project1 -d -p 9081:9081 liberty-project:1.0.0
      docker run --name liberty-project2 -d -p 9082:9081 liberty-project:1.0.0

      curl http://localhost:9081/liberty-project/System/health
      curl http://localhost:9082/liberty-project/System/health

- Note: "health" endpoints returns every request with HTTP 500 (Internal server error) after ~60 seconds (Used for Kubernetes "liveness probe" in a later lab)

      curl -I http://localhost:9081/liberty-project/System/health
      curl -I http://localhost:9082/liberty-project/System/health

## 1.7 Remove containers

- Cleanup lab artifacts

      docker container rm -f liberty-project1
      docker container rm -f liberty-project2
      docker ps | grep "liberty-project*"

---

*Note: PLEASE DON'T DELETE DOCKER IMAGE "liberty-project:1.0.0" - THIS IS REQUIRED FOR THE SECOND LAB*
