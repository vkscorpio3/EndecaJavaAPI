# Endeca Java Web Services

This project is for making web service calls to endeca to get records, filters and results.

## Using Eclipse
This project was developed using Eclipse, although you don't have to use Eclipse, it is highly recommended. Follow these steps to setup your Eclipse environment. (If you don't want to use Eclipse, you'll have to figure out your own development process/environment, however, some of the steps below may still apply to you).

 - If you don't already have Eclipse installed, download and install the latest version of [Eclipse](https://eclipse.org/downloads/).
 
 - Import this git repo into Eclipse. Click on File -> Import -> Existing Maven Project

 - Install external jars:

	- Verify you have maven installed on your machine: run this command `$ which mvn`. If you got a location back for where your mvn binary is then you should be all set. If not, install maven on your machine, follow this link: [Install Maven](https://maven.apache.org/install.html) (You might be able to just use yum on a CentOS box).

	- After verifying or installing mvn, run the following 2 maven commands to install the endeca jar files. 

```
$ mvn install:install-file -Dfile=/path-to-project-root/extlibs/endeca_logging-1.0.jar -DgroupId=com.endeca -DartifactId=endeca_logging -Dversion=1.0 -Dpackaging=jar

$ mvn install:install-file -Dfile=/path-to-project-root/extlibs/endeca_navigation-1.0.jar -DgroupId=com.endeca -DartifactId=endeca_navigation -Dversion=1.0 -Dpackaging=jar
```

- Setup a Tomcat 8 server, you'll have to download and install Tomcat 8 if you haven't already and then set that up in Eclipse. 
	- If you need help setting up your server in Eclipse, you can follow this tutorial on [Youtube](https://www.youtube.com/watch?v=skltzZH7i4w), fast-forward to the 6:55 mark in the video to where he shows you how to setup a server in Eclipse. 

- OPTIONAL, If you're on a unix system, you can set up a pre-push git hook to run the test suite before pushing your cahnges to the repo. To do this, simple cp the `pre-push` file in the root directory of this project to `PATH/TO/PROJECT/finder/.git/hooks/`. PLEASE NOTE: You'll need update the test suite to include relevant data for your endeca instance.



## Packaging 

To package the project run `$ mvn package` - This will create a targe directory in the project root with the packaged finder.war file that may be deployed to Tomcat.

Additionally, you can run the following command to create a version to run in production. This removes all jars that
are used for testing. As a result of this, tests are not run before compilation. It also disables swagger:
`mvn package -P -development -DskipTests`