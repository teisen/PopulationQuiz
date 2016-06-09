AND Final Project

Keystore is in the Folder /app/keystore
alias udacity
password for keystore and alias is androidrocks

Steps to run this:

1) You need to provide your own google-services.json file. Place it in the folder \PopulationQuiz\app\
1b) On Windows, you might need to define your GRADLE_USER_HOME as an environment variable.

2) Place this into ~/.gradle/gradle.properties
RELEASE_STORE_FILE=/keystores/keystore.jks
RELEASE_STORE_PASSWORD=androidrocks
RELEASE_KEY_ALIAS=udacity
RELEASE_KEY_PASSWORD=androidrocks