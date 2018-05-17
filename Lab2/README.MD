# Lab 2 - ICP & Kubernetes CLI Basics

- Setup Kubernetes CLI (kubectl)
- Push container image to ICP image repository
- Create a simple deployment
- ReplicaSets, Labels & Selectors
- Service Discovery and Service Endpoints

## 2.0 Setup Kubernetes CLI (kubectl)

- Open browser with URL, e.g. `https://<icp-ip-address>:8443/console/`
- Login in ICP Console username: admin and password: admin
- Click "Configure client" in the upper-right corner in the admin-profile
- Copy and paste kubectl commands

  !!! PLEASE USE COMMANDS WITH VALID ACCESS TOKEN FROM ICP CONSOLE!!!

        kubectl config set-cluster mycluster.icp --server=https://<icp-master-ip-address>:8001 --insecure-skip-tls-verify=true
        kubectl config set-context mycluster.icp-context --cluster=mycluster.icp
        kubectl config set-credentials admin --token=<access_token>
        kubectl config set-context mycluster.icp-context --user=admin --namespace=default
        kubectl config use-context mycluster.icp-context


## 2.1 Push Container Image to ICP Private repository

- Login to ICP Private Image Repository (check /etc/hosts for "xx.xx.xx.xx mycluster.icp" entry) with username: admin and password: admin

      docker login mycluster.icp:8500

- Tag the docker image with the ICP Cluster CA domain `<cluster_CA_domain>` (e.g. "mycluster.icp") and port and the namespace "default":

      docker tag liberty-project:1.0.0 mycluster.icp:8500/default/liberty-project:1.0.0

- Push the image to the ICP private remote registry:

      docker push mycluster.icp:8500/default/liberty-project:1.0.0

- Check in ICP Console: https://<icp-ip-address>:8443/console/images

## 2.2 Create a simple deployment

- Start a single container instance of the liberty-project image

      kubectl run liberty-project --image=mycluster.icp:8500/default/liberty-project:1.0.0 --port=9081

      kubectl get deployments

      kubectl get pods

  OR list all resources with different types

      kubectl get all

  Wait until "liberty-project" pod is in STATUS Running

- Access the application in the "liberty-project" pod via port forwarding (Replace `<pod-name>` with the pod name retrieved in the last step, e.g. liberty-project-5648d87894-zfpj8 ):

      kubectl port-forward <pod-name> 9081:9081

  and execute in a second bash terminal:

      curl http://localhost:9081/liberty-project/System/health
      curl http://localhost:9081/liberty-project/System/properties

- Access the "liberty-project" pod via bash or tail the log files

      kubectl exec -it <pod-name> bash

      kubectl logs -f <pod-name>

- Press CTRL-C to exit log tailing

## 2.3 ReplicaSets, Labels & Selectors

- Edit the deployment YAML file and change the number of replicas to 3

      kubectl edit deployment liberty-project

- Check in the ICP Console for the event "Scaled up replica set liberty-project-... to 3":

      https://<icp-ip-address>:8443/console/workloads/deployments/default/liberty-project/events  

- Check in the ICP Console the different pod logs

      https://<icp-ip-address>:8443/console/workloads/deployments/default/liberty-project/logs

    Select the three different pods in the combo box on the right side.

- Check the Labels of the pods

      kubectl get pods --show-labels

- Check the Labels of the pods with POD and Node IP

      kubectl get pods -o wide --show-labels

- Search with Selectors for specific pods with attached labels

      kubectl get pods -l "run=liberty-project" --show-labels

- Check the replicaset of the deployment for the DESIRED, CURRENT and READY number of pods:

      kubectl get replicasets

      kubectl describe replicaset <replicaset-name>

## 2.4 Service Discovery and Service Endpoints

- Expose the liberty-project deployment

      kubectl expose deployment liberty-project

- Check the service

      kubectl get services

      kubectl describe service liberty-project

- Check the "liberty-project" application access using the service-endpoint of type "ClusterIP" (Replace "<clusterIP>" with the value of the column "CLUSTER-IP") and execute following command mutliple times:

      curl http://<clusterIP>:9081/liberty-project/System/properties

    The different responses contain the hostname of one of the three available pods.

- Check the Service discovery & check "self-healing" ReplicaSets

      kubectl describe endpoints liberty-project

      kubectl get endpoints liberty-project --watch

- Open a new terminal and delete one of the three available pods:

      kubectl delete pod <pod-name>

      kubectl get pods -o wide

- Check the re-scheduled pod and endpoint list

- Delete Deployment (kubectl performs cascading deletion of dependents automatically by default otherwise use "--cascade=false")

      kubectl delete deployment liberty-project

- Delete Service

      kubectl delete svc liberty-project

- Check remaining artifacts

      kubectl get all
