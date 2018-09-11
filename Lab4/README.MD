# Lab 4 - ICP & Helm, CI/CD with Jenkins

- Helm CLI Setup
- Hello Helm
- Using Helm to deploy a cloud-native microservices application "bluecompute-ce"
- Set up a Jenkins pipeline for CI/CD of deployments to a ICP Cluster

## 4.0 Check Helm CLI Setup

- Check the installed Helm version (Client & Server)

      helm version --tls

    Expected output:

      Client: &version.Version{SemVer:"v2.7.2+icp", ...}
      Server: &version.Version{SemVer:"v2.7.2+icp", ...}


## 4.1 Hello Helm

- Create a simple "hello-helm" example and check the helm artifacts

      mkdir ~/charts
      cd ~/charts
      helm create hello-helm

      tree hello-helm/
      hello-helm/
      +-- charts
      +-- Chart.yaml
      +-- templates
      ¦   +-- deployment.yaml
      ¦   +-- _helpers.tpl
      ¦   +-- ingress.yaml
      ¦   +-- NOTES.txt
      ¦   +-- service.yaml
      +-- values.yaml
      2 directories, 7 files

- Install this helm chart as "my-hello-helm" release

      helm install --name my-hello-helm hello-helm --tls

- Check all Helm releases (Release "my-hello-helm", Revision "1", Chart "hello-helm-0.1.0")

      helm list --all --tls

- Check Helm release in ICP Console (Current Version "0.1.0")

      https://<icp-ip-address>:8443/catalog/instances

- Check all created Kubernetes artifacts

      kubectl get all

- Lookup the HTTP access information of the deployed application ("Expose details - Endpoint")

       https://<icp-ip-address>:8443/console/workloads/deployments/default/my-hello-helm

- Access the application via following Kubernetes proxy URL in the browser

       https://<icp-ip-address>:8443/kubernetes/api/v1/proxy/namespaces/default/services/my-hello-helm:80/

- Change in hello-helm/Chart.yaml the "version" property from "0.1.0" to "0.2.0"

- Upgrade the hello-helm release to the helm chart release 0.2.0

       helm upgrade my-hello-helm hello-helm --tls

- Check the Helm release revision in ICP Console (now "0.2.0")

       https://<icp-ip-address>:8443/catalog/instancedetails/default/my-hello-helm

- Check the "my-hello-helm" release history

       helm history my-hello-helm --tls

- Rollback the hello-helm release to the helm chart release 0.1.0 (Revision 1)

       helm rollback my-hello-helm 1 --tls

- Wait a minute and check the Helm release revision in ICP Console (now again "0.1.0")

       https://<icp-ip-address>:8443/catalog/instancedetails/default/my-hello-helm

- Check again the "my-hello-helm" release history (Revision "3" - Chart "hello-helm-0.1.0", Description "Rollback to 1")

       helm history my-hello-helm --tls

- Delete the release

       helm delete my-hello-helm --purge --tls

- Create Helm chart package ("hello-helm-0.2.0.tgz") and to be able to add it to a chart repository

       cd ~/charts
       helm package hello-helm

- Generate a Helm chart index.yaml and check the content

       helm repo index .
       cat index.yaml

- Place this generated Helm chart packages and index.yaml file on a HTTP Server, see following section


## 4.2 Using Helm to deploy a cloud-native microservices application "bluecompute-ce"

- Check the following index.yaml and Helm chart package from the following URLs in a browser:

       https://raw.githubusercontent.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/spring/docs/charts/bluecompute-ce/index.yaml

  and

       https://raw.githubusercontent.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/spring/docs/charts/bluecompute-ce/bluecompute-ce-0.0.6.tgz

- Add this Helm Chart repository to the Helm client configuration

       helm repo add ibmcase https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes/tree/spring/docs/charts/bluecompute-ce

       helm repo list

- Install the "bluecompute-ce" application

       helm install --name bluecompute-ce ibmcase/bluecompute-ce --tls

- Check the list of Helm releases for the new  "bluecompute-ce" release with revision "1"

       helm list --tls

- Check the status of the deployment and of the pods

       kubectl get all

- Get the HTTP (!) Service Node port endpoint information of the "bluecompute" WEB (!) application in the ICP Admin Console

      https://<icp-ip-address>:8443/console/access/services/default/bluecompute-ce-web

  OR check the bluecompute-ce/values.yaml file for "nodeport: port: 31337" (".Values.service.nodeport.port")

- Open the application via the following HTTP (!) URL in the browser

       http://<icp-ip-address>:31337/


## 4.3 Set up a Jenkins pipeline for CI/CD of deployments to a ICP Cluster

- Create PersistentVolume "jenkins-home" (1Gi and Storage Type "Host Path" with parameter key "path" and value "/storage/jenkins-home")

      cd ~/icp-workshop/Lab4
      kubectl apply -f jenkins-home-pv.yaml

- Create PersistentVolumeClaim "jenkins-home" and Storage Request Type "1"

      kubectl apply -f jenkins-home-pvc.yaml

- Check the PersistentVolume andPersistentVolumeClaim status (STATUS must be "bound")

      kubectl get pv

      kubectl get pvc

- Setup ConfgigMap and Secret for Jenkins to access the ICP Private Registry

      kubectl apply -f jenkins-registry_config_icp.yaml

      kubectl apply -f jenkins-registry_secret.yaml

- Install the Jenkins server by using Helm

      helm install --name jenkins --set Persistence.ExistingClaim=jenkins-home --set Master.ImageTag=2.67 stable/jenkins --tls

- Get the auto generated login password

      printf $(kubectl get secret --namespace default jenkins -o jsonpath="{.data.jenkins-admin-password}" | base64 --decode); echo

- Check for the Node port information via "Network Access - Services" and "jenkins" in the ICP console or use kubectl to get "jenkins" HTTP Node port

      kubectl get svc

- Login in with the Jenkins User: admin and the password retrieved in the previous step and the URL

      http://<icp-ip-address>:<port>/login

- Open the URI "/pluginManager" or use the Jenkins menu "Manage Jenkins" and "Goto Plugin Manager"

- In the Update tab select "Git", "Kubernetes", "Credentials Binding" and "Pipeline: Job" and press "Download now and install after restart"

- Check "Restart Jenkins when installation is complete and no jobs are running" on the "Installing Plugins/Upgrades" page

- Please wait while Jenkins is restarting

- Login in again and click "Back to the Dashboard"

- Open the URI "/configure" or use the Jenkins menu "Manage Jenkins" and "Configure System"
- Scroll to the "Cloud" section and update the "Container Cap" value to "1000" and click "Save"

- Back in the Jenkins Console Dashboard click "create new jobs"

- For the item name, type "web_pipeline". Click Pipeline, and then click OK

- In the Definition field, type Pipeline script from SCM

- In the SCM field, type Git

- In the Repository URL field, type "https://github.com/ibm-cloud-architecture/refarch-cloudnative-bluecompute-web"

- In the Branch Specifier field, type "*/tutorial"

- In the Script Path field, type "Jenkinsfile"

- Click "Save"

- To build by using the pipeline, on the "Pipeline web_pipeline" page, click "Build Now"

- Click on running build, e.g. "#1" and "Console Output" to display the standard output (stdout) of the running pipeline job and wait for the "Finished: SUCCESS" message

- Open the "bluecompute-ce" application via ICP Console "Network Access" - "Services" - "bluecompute-ce-web" and Click on the NodePort "http 31337/TCP" link

- The "bluecompute-ce" home page shows now the text "Congratulations! You completed the CI/CD tutorial."




---

References:

- Deploy a cloud-native microservices application on IBM Cloud Private
https://www.ibm.com/cloud/garage/tutorials/cloudnative-microservices-cloud-private

- Set up a Jenkins pipeline to continuously integrate and deliver Kubernetes deployments to an IBM Cloud Private cluster
https://www.ibm.com/cloud/garage/tutorials/cloud-private-jenkins-pipeline

- Bluecompute-web part of the 'IBM Cloud Native Reference Architecture for Kubernetes' suite
https://github.com/ibm-cloud-architecture/refarch-cloudnative-bluecompute-web

- Reference Implementation for Microservices based on Kubernetes and the IBM Container Service
https://github.com/ibm-cloud-architecture/refarch-cloudnative-kubernetes
