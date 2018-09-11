# ICP Workshop

## Preparation

1. Log into the Skytap environment, start your VM
1. Adjust the screen size of your VM: ![Adjust VM screen size](/doc-images/adjust-vm-screen-size.png "Adjust VM screen size")
1. If the layout of your laptop keyboard is DE, change the VM keyboard layout to DE:
  1. ![Adjust keyboard layout part 1](/doc-images/keyboard-layout-de-1.png "Adjust keyboard layout part 1")
  1. ![Adjust keyboard layout part 2](/doc-images/keyboard-layout-de-2.png "Adjust keyboard layout part 2")

1. Fire up a command line console, make sure these runtimes and tools are installed on your VM:
  * git
  * java
  * maven
  * docker
  * kubectl
  * helm
  
  If Maven is not installed, install it with this command:

  ```
  sudo apt-get install maven
  ```
  The password for superuser should be: `passw0rd`

1. Connect to the IBM Cloud Proviate Server in your virtual network:
  1. In your running VM, open the firefox and click the bookmark "IBM Cloud Private". On the "Your connection is not secure page, click "advanced":![Init ICP Server 1](/doc-images/init-icp-server-1.png "Init ICP Server 1")

    Then click "Add Exception", and click "Confirm Security Exception".
    
    ----
    **NOTE**

    If you see a "502 Bad Gateway" message after confirming the security exception, just wait a couple of minutes and reload the page. Kubernetes master cluster needs time to intialize, multiple containers need to boot. 

    [More information about the 502 error can be found here](https://www.ibm.com/support/knowledgecenter/en/SSBS6K_2.1.0.2/getting_started/known_issues.html#ui_502).

    ----

  1. On the ICP log in page, log in with username `admin` and password `admin`. 

  1. Click the admin icon on the upper right corner, then click "Configure client": ![Configure ICP client 0](/doc-images/configure-icp-client-0.png "Configure ICP client 0")

  1. You will need to paste all the commands in the next window to a terminal console. Click the "Copy" button in the next window and these commands are copied: ![Configure ICP client 1](/doc-images/configure-icp-client-1.png "Configure ICP client 1")

  1. In a console in your VM, paste the commands copied from last step. Execute them. 


1. Clean up the VM. Since the VM for this workshop was provisioned with very limited resource, it is good to delete kubernetes pods and deployments to free up resource and make your workshop running more smoothly. You can view the pods and deployments with these two commands:

  ```
  kubectl get pods
  kubectl get deployment
  ```
  once you see the names of the deployments and pods, you can delete them by running these commands on all the exisiting deployments and pods:
  ```
  kubectl delete deployment <name-of-a-deployment>
  kubectl delete pod <name-of-a-pod>
  ```


## Labs

[Lab 1 - Docker Basics ](Lab1/)

[Lab 2 - ICP & Kubernetes CLI Basics](Lab2/)

[Lab 3 - ICP & Kubernetes Deployments, ConfigMaps](Lab3/)

[Lab 4 - ICP & Helm, CI/CD with Jenkins](Lab4/)
