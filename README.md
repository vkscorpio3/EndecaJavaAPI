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

- OPTIONAL, If you're on a unix system, you can create a pre-push git hook so that everytime you try to push code to the repo, you will first run the test suite to make sure there are no errors. To do this, create a file under /PATH/TO/PROJECT/ROOT/.git/hooks/ called pre-push with the contents of this file being:

```sh
#!/bin/bash

# save the file as <git_directory>/.git/hooks/pre-push

echo "Running \"mvn clean test\" to check for errors"
# retrieving current working directory

CWD=`pwd`
ROOT_DIR="$(git rev-parse --show-toplevel)"

# go to main project dir
cd $ROOT_DIR

# Redirect output to stderr.
exec 1>&2

# running maven clean test
mvn clean test

# check "$?" to ge the return value of the mvn command
# 0 means the tests succeeded
if [ $? -ne 0 ]; then
  cat <<\EOF
Error: mvn clean test failed.

Please fix your errors and try committing again.
EOF
  # go back to current working dir
  cd $CWD
  exit 1
fi

# go back to current working dir
cd $CWD
```

 - Next, make sure the pre-push file is executable by running:
 `$ chmod 755 pre-push`



## Packaging 

To package the project run `$ mvn package` - This will create a targe directory in the project root with the packaged finder.war file that may be deployed to Tomcat.

Additionally, you can run the following command to create a version to run in production. This removes all jars that
are used for testing. As a result of this, tests are not run before compilation. It also disables swagger:
`mvn package -P -development -DskipTests`
