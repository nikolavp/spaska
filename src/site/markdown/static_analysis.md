# Static analysis

We currently have 4 static analysis tools that can be run as part of the  build process. Everyone can check if he is compliant with the coding standard with

    mvn verify

This will run:

1. checkstyle:check
1. pmd:check and cpd:check
1. findbugs:check

cobertura:check is skipped for now(commented in the pom) as we don't have enough tests to be complaint.

*Note*: The tools are run from the fastest to the slowest so we can have faster feedback if something is wrong. Have a look at the project reports on the website [here](http://nikolavp.github.com/spaska/project-reports.html) for more information
