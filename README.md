# cas-demo
```
生成证书keystore密码changeit
keytool -genkey -alias tomcat -keyalg RSA -validity 3650 -keystore /Users/mac/Desktop/tomcat.keystore

查看证书keystore
keytool -list -keystore /Users/mac/Desktop/tomcat.keystore

根据keystore生成crt文件
#输入第一步中keystore的密码changeit
keytool -export -alias tomcat -file /Users/mac/Desktop/tomcat.cer -keystore /Users/mac/Desktop/tomcat.keystore -validity 3650

信任授权文件到jdk
sudo keytool -import -keystore /Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home/jre/lib/security/cacerts -file /Users/mac/Desktop/tomcat.cer -alias tomcat -storepass changeit

删除证书
sudo keytool -delete -alias tomcat -keystore /Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home/jre/lib/security/cacerts
查看证书
keytool -list -v -keystore /Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home/jre/lib/security/cacerts

tomcat的server.xml增加配置
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
           maxThreads="200" SSLEnabled="true" scheme="https"
           secure="true" clientAuth="false" sslProtocol="TLS"
           keystoreFile="/Users/mac/Desktop/tomcat.keystore"
           keystorePass="changeit"/>
           
mac钥匙串中导入秘钥crt

配置tomcat，配置/etc/hosts
127.0.0.1 server.cas.com





集成oauth2
配置依赖

<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-oauth-webflow</artifactId>
    <version>${cas.version}</version>
</dependency>

增加service配置
{
  "@class" : "org.apereo.cas.support.oauth.services.OAuthRegisteredService",
  "clientId": "20180901",
  "clientSecret": "123456",
  "serviceId" : "^(https|http|imaps)://.*",
  "name" : "OAuthService",
  "id" : 1002
}

https://server.cas.com:8443/cas/oauth2.0/authorize?response_type=code&client_id=20180901&redirect_uri=https://www.baidu.com
```