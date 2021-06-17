## 介绍
该项目是个人学习项目，通过学习分布式系统理论知识，使用 Java 构建一套简易的分布式系统，来实现服务注册、服务发现、网关路由、状态检测、心跳机制、日志服务等功能。服务之间采用 REST 风格进行调用，该项目麻雀虽小但五脏俱全，拥有分布式系统核心功能，通过该项目加深了对分布式系统理解。
## 技术栈
1. JDK11
2. SpringMVC
3. SpringBoot
4. Tomcat

## 主要特性
* 服务注册
* 服务发现
* 状态检测

## 运行
1. 进入 distributed-registry 模块启动，运行注册中心
2. 随后启动 distributed-gateway（网关）、distributed-log（日志服务）、distributed-service（订单服务）

发送一个保存日志的 post 请求，请求通过网关转发到日志服务，最终日志服务将日志持久化本地
``` sh
curl --location --request POST 'http://localhost:6000/log' \
--header 'Content-Type: application/json' \
--data-raw '{
    "serviceName": "Gateway-Service",
    "content": "gateway test"
}'
```
用户下单，请求通过网关转发到订单服务，订单服务下单成功后调用日志服务持久化日志
```sh
curl --location --request POST 'http://localhost:6000/order' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "test"
}'
```



  
