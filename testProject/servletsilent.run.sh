cd /home/nnm/IdeaProjects/testProject/src/main/java

javac -classpath /home/nnm/IdeaProjects/testProject/target/classes:/home/nnm/.m2/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar:/home/nnm/.m2/repository/org/ancoron/postgresql/org.postgresql/9.1.901.jdbc4.1-rc9/org.postgresql-9.1.901.jdbc4.1-rc9.jar *.java

sudo mv *.class /opt/tomcat/webapps/project/WEB-INF/classes/
sudo systemctl restart tomcat

echo ''
#echo 'Updated by address: http://127.0.0.1:8080/project'

#sudo systemctl status tomcat
#firefox http://localhost:8080/ignis/test &
