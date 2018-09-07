# Lab 3 - ICP & Kubernetes Deployments, ConfigMaps

- ConfigMaps and Secrets via YAML file
- Deployment via YAML file with configMap Key References
- Deployment via YAML file with "livenessProbe"
- Automatic rolling updates with "readinessProbe"

## 3.0 ConfigMaps and Secrets via YAML file

- Create a configMap

      cd ~/icp-workshop/Lab3
      kubectl apply -f liberty-project-trace-config.yaml

## 3.1 Deployment via YAML file with configMap Key References

- Create a deployment via YAML file with configMap Key References

      kubectl apply -f liberty-project-deployment.yaml

      kubectl get deployments

  OR

      kubectl get all

- Check the passed configuration values

      kubectl port-forward <pod-name> 9081:9081

      curl http://localhost:9081/liberty-project/System/properties


- Change in ConfigMap  (e.g. set " tracespec: '*=debug' ") and save/quit the editor

      kubectl edit configMap liberty-project-trace-config

- Restart PODs (scale down, scale up) - DOWNTIME!

      kubectl scale deployment liberty-project --replicas=0
      kubectl scale deployment liberty-project --replicas=3

    OR redeploy the application

    and check the changed configuration values again

## 3.2 Deployment via YAML file with "livenessProbe"

- Create a deployment via YAML file with "livenessProbe"

      kubectl apply -f liberty-project-deployment-with-livenessProbe.yaml

- Check the pod events and logs for the events:
 "Unhealthy - Liveness probe failed: HTTP probe failed with statuscode: 500"
 "Killing - Killing container with id docker://liberty:Container failed liveness probe.. Container will be killed and recreated."

      https://<icp-ip-address>:8443/console/workloads/deployments/default/liberty-project/pods/

- Check the number of restarts of the PODs

      kubectl get pods

- Remove "livenessProbe" from the deployments to prepare the next lab section

      kubectl apply -f liberty-project-deployment-without-livenessProbe.yaml


## 3.3 Automatic rolling updates with "readinessProbe"

- Make a change in the OpenLiberty application of Lab1, e.g. change the "version" - property to the value "2" in `PropertiesResource.java` file in the directory `icp-workshop\Lab1\app\src\main\java\io\openliberty\guides\rest\`

- Compile the application with `mvn install` according to the instructions in Lab1

      cd ~/icp-workshop/Lab1/app
      mvn install

- Build, tag and push a new version (tag 2.0.0) of the Docker Image to the ICP Private Repository according to the instructions in Lab 1 and 2

      cd ~/icp-workshop/Lab1
      docker build -t liberty-project:2.0.0 .

      docker login mycluster.icp:8500

      docker tag liberty-project:2.0.0 mycluster.icp:8500/default/liberty-project:2.0.0

      docker push mycluster.icp:8500/default/liberty-project:2.0.0

- Check all pods in the default namespace and the used image version, image tag is "1.0.0"

      kubectl get pods --namespace default -o=jsonpath='{range.items[*]}{.metadata.name}{"\t"}{.spec.containers[0].image}{"\t"}{.status.phase}{"\n"}{end}'

- Perform a rolling update of the deployment liberty-project with three pods to image with tag "2.0.0" and "minReadySeconds: 10"

      kubectl apply -f liberty-project-deployment-rolling-update.yaml

- Check the status and the history of the update (status: "deployment "liberty-project" successfully rolled out")

      kubectl rollout status deployments liberty-project
      kubectl rollout history deployment liberty-project

- Check the used images of the running pods, now tag is "2.0.0"

      kubectl get pods --namespace default -o=jsonpath='{range.items[*]}{.metadata.name}{"\t"}{.spec.containers[0].image}{"\t"}{.status.phase}{"\n"}{end}'

- Roll back to previous deployment (Revisison "3" - one revision before CHANGE-CAUSE = "Update liberty-project to 2.0.0") and check the used image tag (should be "1.0.0" again)

      kubectl rollout undo deployments liberty-project --to-revision=3
      kubectl rollout status deployments liberty-project

      kubectl get pods --namespace default -o=jsonpath='{range.items[*]}{.metadata.name}{"\t"}{.spec.containers[0].image}{"\t"}{.status.phase}{"\n"}{end}'

- Perform again a rolling update of the deployment liberty-project with three pods to image with tag "2.0.0" and "minReadySeconds: 120", "progressDeadlineSeconds: 600" and a "readinessProbe"

      kubectl apply -f liberty-project-deployment-rolling-update-failing.yaml
      kubectl rollout status deployments liberty-project

      kubectl get pods --namespace default -o=jsonpath='{range.items[*]}{.metadata.name}{"\t"}{.spec.containers[0].image}{"\t"}{.status.phase}{"\n"}{end}'

     The Deployment is marked as failed, and all attempts to move the Deployment forward are halted, because the first new updated POD doesn't change its status to "READY". The "readinessProbe" URL failed after approx. 60 seconds (returned HTTP Code 500) and the deployment manifest specified for every pod status be ready for at least 2 minutes ("minReadySeconds: 120") before proceeding with the next pod. The total time limit for the deployment is specified with 10 mins ("progressDeadlineSeconds: 600"):

     `error: deployment "liberty-project" exceeded its progress deadline`

- Check the replicasets

      kubectl get replicasets -o wide

  The replicaset with the "liberty-project:1.0.0" image still reported DESIRED=CURRENT=READY=3 number of pods, but the new replicaset with the "liberty-project:2.0.0" image is reporting DESIRED=CURRENT=1 and READY=0 (!) number of pods.

- Check the detailed info of the deployment ("Progressing  False ProgressDeadlineExceeded"):

      kubectl describe deployment liberty-project

- Remove the "liberty-project" deployments

      kubectl delete deployment liberty-project
