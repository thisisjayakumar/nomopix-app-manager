#!/usr/bin/env sh

# 1. Use local SDK gradle if running on local environment
if [ -f "$HOME/.local_sdk/gradle-8.5/bin/gradle" ]; then
    exec "$HOME/.local_sdk/gradle-8.5/bin/gradle" "$@"
fi

# 2. Fallback to standard Gradle wrapper jar
DIR=""
case "$0" in
    /*) DIR="${0%/*}" ;;
    *) DIR="./${0%/*}" ;;
esac

CLASSPATH="$DIR/gradle/wrapper/gradle-wrapper.jar"

if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

exec "$JAVACMD" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
