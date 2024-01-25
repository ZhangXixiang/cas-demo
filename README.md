
```
环境jdk8



域名映射:
修改/etc/hosts文件，添加服务端域名(server.cas.com) 以及两个客户端的域名(app1.cas.com , app2.cas.com)

生成keystore,用于生成证书，https访问需要TLS认证： 3650day
注意名字要用server.cas.com，其他随意
keytool -genkey -alias tomcat -keyalg RSA -validity 3650 -keystore /Users/mac/Desktop/tomcat.keystore

查看证书
keytool -list -keystore /Users/mac/Desktop/tomcat.keystore
生成秘钥库
keytool -list -keystore /Users/mac/Desktop/tomcat.keystore

#生成证书cert 输入第一步中keystore的密码changeit
keytool -export -alias tomcat -file /Users/mac/Desktop/tomcat.cer -keystore /Users/mac/Desktop/tomcat.keystore -validity 3650 

##信任授权文件到jdk 这里你需要换成你自己的jdk目录
sudo keytool -import -keystore /Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home/jre/lib/security/cacerts -file /Users/wangsaichao/Desktop/tomcat.cer -alias tomcat -storepass changeit

##tomcat增加下面的8443端口配置
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
           maxThreads="200" SSLEnabled="true" scheme="https"
           secure="true" clientAuth="false" sslProtocol="TLS"
           keystoreFile="/Users/mac/Desktop/tomcat.keystore"
           keystorePass="changeit"/>

在 [钥匙串] 导入我们生成的cert证书

#在propeties中增加ssl相关配置
server.ssl.enabled=true
server.ssl.key-store=file:/Users/wangsaichao/Desktop/tomcat.keystore
server.ssl.key-store-password=changeit
server.ssl.key-password=changeit
server.ssl.keyAlias=tomcat

IDEA中配置tomcat环境

#用户表
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT '' COMMENT '用户名',
  `password` varchar(256) DEFAULT NULL COMMENT '登录密码',
  `name` varchar(256) DEFAULT NULL COMMENT '用户真实姓名',
  `id_card_num` varchar(256) DEFAULT NULL COMMENT '用户身份证号',
  `state` char(1) DEFAULT '0' COMMENT '用户状态：0:正常状态,1：用户被锁定',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `username` (`username`) USING BTREE,
  UNIQUE KEY `id_card_num` (`id_card_num`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

#插入用户信息表
INSERT INTO user_info(uid,username,`password`,`name`,id_card_num) VALUES (null,'admin','123456','大牛','110200198809091212');

增加jar包依赖
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-jdbc</artifactId>
    <version>${cas.version}</version>
</dependency>
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-jdbc-drivers</artifactId>
    <version>${cas.version}</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <!-- 根据数据版本选择
    <version>5.1.36</version>
    -->
    <version>8.0.11</version>
</dependency>

properties增加数据配置
#添加jdbc认证
cas.authn.jdbc.query[0].sql=SELECT * FROM user_info WHERE username =?
#那一个字段作为密码字段
cas.authn.jdbc.query[0].fieldPassword=password
#配置数据库连接
cas.authn.jdbc.query[0].url=jdbc:mysql://127.0.0.1:3306/guns?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false
cas.authn.jdbc.query[0].dialect=org.hibernate.dialect.MySQLDialect
#数据库用户名
cas.authn.jdbc.query[0].user=root
#数据库密码
cas.authn.jdbc.query[0].password=123456
#mysql驱动
cas.authn.jdbc.query[0].driverClass=com.mysql.jdbc.Driver



OAuth2适配

增加jar包依赖
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-oauth-webflow</artifactId>
    <version>${cas.version}</version>
</dependency>

##application.properties添加oauth属性
cas.authn.oauth.refreshToken.timeToKillInSeconds=2592000
##测试的时候这个数字放大些，不然容易报错
cas.authn.oauth.code.timeToKillInSeconds=300
cas.authn.oauth.code.numberOfUses=1
cas.authn.oauth.accessToken.releaseProtocolAttributes=true
cas.authn.oauth.accessToken.timeToKillInSeconds=7200
cas.authn.oauth.accessToken.maxTimeToLiveInSeconds=28800
cas.authn.oauth.grants.resourceOwner.requireServiceHeader=true
cas.authn.oauth.userProfileViewType=NESTED

##增加如下配置
/src/services/OAuthService-1002.json
{
  "@class" : "org.apereo.cas.support.oauth.services.OAuthRegisteredService",
  "clientId": "20180901",
  "clientSecret": "123456",
  "serviceId" : "^(https|http|imaps)://.*",
  "name" : "OAuthService",
  "id" : 1002
}

启用OAuth支持后



#验证OAuth2

##获取code
https://server.cas.com:8443/cas/oauth2.0/authorize?response_type=code&client_id=1000&redirect_uri=http://www.baidu.com

https://www.baidu.com/?code=OC-2-h0mFstbufIteBRdZZzLmSWSJJJJiRrgh

##获取token
https://server.cas.com:8443/cas/oauth2.0/accessToken?grant_type=authorization_code&client_id=1000&client_secret=1000&code=OC-1-zgSS9AWbgbm2VQcM4INLKuFy0k-trJir&redirect_uri=http://www.baidu.com

access_token=AT-2-XJSSe2pQqQTmGJ44NH8iMFyexANg4-lL&expires_in=28800

##通过token获取服务信息
https://server.cas.com:8443/cas/oauth2.0/profile?access_token=AT-2-XJSSe2pQqQTmGJ44NH8iMFyexANg4-lL

{
"service" : "http://www.baidu.com",
"attributes" : {
"credentialType" : "UsernamePasswordCredential"
},
"id" : "admin",
"client_id" : "1000"
}

https://www.baidu.com/?code=OC-3-dK8GeeHIeG7J1Fw5rlVuBRIMahGpzobY

```