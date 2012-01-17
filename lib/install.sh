#!/bin/bash
# vim: set sw=4 sts=4 et foldmethod=indent :

mvn install:install-file -Dfile=lib/jsc.jar -DgroupId=jsc -DartifactId=jsc -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=lib/DistLib.jar -DgroupId=DistLib -DartifactId=DistLib -Dversion=0.9.1 -Dpackaging=jar -DgeneratePom=true
