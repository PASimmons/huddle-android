
** file: readme.txt
** author: P A Simmons
** date: 2 Oct 2012
** 
** What is this?

This file is a result of a Tuesday-time task to get Huddle's Android app running, and represents
my notes on how to set up the dev environment.  

For those not familiar with Java, here's the task list to get "handle",
this 3rd party huddle android application, on the workbench.

As a result you will be able to:

Run an android emulator on your PC.
Debug the code in intellij idea, breakpoint whilst running.
Make fixes and commit changes back to source control

The whole lot should take an hour to get running.


** 1. Prerequisite downloads

Download intellij idea, community edition is fine.  I used 11.1.3.
You could use Eclipse or java command line instead, but intellij makes it easy.

Download java JDK, if you don't have it (might be under Sun or Oracle in program files).
Note you need a JDK, not a JRE. eg: Mine is C:\Program Files\Java\jdk1.7.0_03

Download Android SDK. This provides an emulator and the Android libraries for the code to compile.
Install in a path without spaces in it,
eg: I use: C:\local\apps\AndroidSDK

Download Maven 3.  Maven is a build dependency tool and makes it easier to pull 3rd party libraries,
a bit like Nuget.  Maven uses pom.xml to declare project dependencies, and intellij can use this to build the project.
My maven is in C:\local\apps\maven\apache-maven-3.0.4

** 2. Set up the environment

Intellij stores project info in huddle.iml, but by convention developers don't normally share this
as it can have some side-effects depending on your IDE version etc..  So instead here are the steps you need to prepare:

Set Maven env for intellij

Either set M2_HOME or overide it in the Maven settings.
In intellij idea, project settings (File .. project settings, Maven), the override is set by ticking override and set maven home directory (mine is C:\local\apps\maven\apache-maven-3.0.4).

Maven local repository by default gets put under windows user home.  I recommend moving this to a shorter folder name, again override "local repository" set to d:\dev\.m2\repository  or something similar. The .m2\repository is the standard naming convention, suggest you stick to this.

If you chose to set M2_HOME, look up "Path Variables" from project settings, and add a row for M2_HOME C:\local\apps\maven\apache-maven-3.0.4.  Whilst there, if you don't have JAVA_HOME set as a windows env, it may be required here too, so add JAVA_HOME C:\Program Files\Java\jdk1.7.0_03

The pom.xml also specifies a property for Android SDK. This is the wrong way to specify such, but for now, just make sure it is set to yours.  eg:
<android.sdk.path>c:\local\apps\AndroidSDK</android.sdk.path>


3. Create the Intellij project

Use subversion to checkout this project.  You can use Tortoise or other SVN client, or intellij has
svn integration built-in.

From intellij, File ... New Project ... existing sources ... maven.
This will creata a project hierarchy from the file system, and (probably on the right) a Maven project from the pom.

Name the project huddle-android (as I think the run configuration may use this name to find the package, so should match <Name> in pom.xml).  Select the Android 4.1 SDK.

Under Files, Project settings, SDKs, add a JDK 1.7, point to the JAVA_HOME installation.
Also add an Android 4.1 SDK

You should see a module here, under folder huddle, called Android.  If not, create one (don't tick 'is library project')


4. Build the project

Use maven to build the project.  There are several ways, but the simplest is to open the maven 'lifecycle' folder in the Maven projects window, and click on install.  Maven will pull in all depedencies, compile and build the artifact ready for Android.

To build as debug, uncomment the debug tag within pom.xml.  Don't commit this change, as we don't want debug to bloat the artifact deployed to a real Android device.  This is line 59 in pom.xml :
<debug>true</debug>

5. Run the project

You need to create an android run configuration.
From the Run menu, select Edit configurations.
Click plus, to add an Android application configuration.  Name this "Huddle-android"
General tab - select the huddle module (part of the project), and select launch default activity and tick deploy application.  Select Emulatoir and create a 'prefer android virtual device' in the "..." dialog.


9. Trouble shooting

The run configuration may not find the application.  The configuration seems to look for it's name as a apk package, so the above name is important to match the <name> tag at the start of the pom.xml.

If breakpoints aren't set, it's likely you haven't compiled with debug.  Ensure the pom.xml has debug tag in the compile section, and explcitly clean then install the app using the maven lifecycle steps in the maven project window.

One of the dependencies in pom.xml has changed name subtely from maven-android-plugin to android-maven-plugin. So the following is correct in pom.xml:
<groupId>com.jayway.maven.plugins.android.generation2</groupId>
<artifactId>android-maven-plugin</artifactId>
and code won't get all the dependencies without this.
Note: Maven shows 3rd party dependencies in the maven project window.


