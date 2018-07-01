mvn package
sudo unzip -o /home/nnm/IdeaProjects/testProject/target/project-1.0.war -d /opt/tomcat/webapps/project/
sudo rm -r -f /opt/tomcat/webapps/project/META-INF
sudo chown -R tomcat:tomcat /opt/tomcat/webapps/project/WEB-INF
sudo systemctl restart tomcat
echo ''
