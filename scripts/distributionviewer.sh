#!/bin/sh

# This script is a convenience script to automatically set the correct
# classpath for the eSalsa Tools given the location of an installation
# specified in the $ESALSA_HOME environment variable.

# Check setting of ESALSA_HOME
if [ -z "$ESALSA_HOME" ];  then
    echo "please set ESALSA_HOME to the location of your eSalsa Tools installation" 1>&2
    exit 1
fi

exec java \
    -classpath "$ESALSA_HOME/lib/"'*' \
    -Dlog4j.configuration=file:"$IPL_HOME"/log4j.properties \
    nl.esciencecenter.esalsa.tools.DistributionViewer \
    "$@"

