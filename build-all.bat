@echo off
REM App build batch script based on ECAFETERIA APP made by EAPLI professors
REM make sure JAVA_HOME is set to JDK folder
REM make sure maven is on the system PATH
mvn dependency:copy-dependencies package