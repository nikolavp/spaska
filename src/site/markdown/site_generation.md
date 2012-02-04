# Site generation tools
To generate this site we are using the [maven site plugin](http://maven.apache.org/plugins/maven-site-plugin/maven-3.html) with the help of some plugins for reporting. This currently include:

* [maven project info](http://maven.apache.org/plugins/maven-project-info-reports-plugin/)
* [maven javadoc plugin](http://maven.apache.org/plugins/maven-javadoc-plugin/)
* [maven checkstyle plugin](http://maven.apache.org/plugins/maven-checkstyle-plugin/)
* [maven pmd plugin](http://maven.apache.org/plugins/maven-pmd-plugin/)
* [maven findbugs plugin](http://mojo.codehaus.org/findbugs-maven-plugin/)

the last three are also used when developing to verify for code style compliance(see more [here](/spaska/static_analysis.html))

# Syntax for the pages 
For the pages we are not using raw HTML, but rather take advantage of the [markdown maven plugin](http://markdown.tautua.org/doxia-module.html). The syntax of markdown is pretty straightforward and shouldn't take you more than 10 minutes to get you going if you are not familiar with it. More information on markdown can be found [here](http://daringfireball.net/projects/markdown/)

# Site generation
To generate a new version of the website locally you should run mvn site from the base directory of the project. If you also want to upload the website to the current url(http://nikolavp.github.com), you have to uncomment the executions section of the defined github maven site plugin in the pom.xml file. You will also have to provide credentials(username and password) as they are required by github. The credentials can be provided by your settings.xml with a section like this

    <profiles>
      <profile>
        <id>github</id>
        <properties>
          <github.global.userName>user</github.global.userName>
          <github.global.password>password</github.global.password>
        </properties>
      </profile>
    </profiles>

    <activeProfiles>
      <activeProfile>github</activeProfile>
    </activeProfiles>

More information can be found [here](https://github.com/github/maven-plugins)
