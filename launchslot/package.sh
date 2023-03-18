#MUST BE RUN FROM INSIDE THE /launchslot DIRECTORY

for n in 1 2 3
do
    sed -i "s/lotSN/lot${n}/" ./pom.xml
    sed -i "s/profileNum=0/profileNum=${n}/" ./src/main/java/launchslot/patches/ChangeProfile.java
    mvn package
    sed -i "s/lot${n}/lotSN/" ./pom.xml
    sed -i "s/profileNum=${n}/profileNum=0/" ./src/main/java/launchslot/patches/ChangeProfile.java
done