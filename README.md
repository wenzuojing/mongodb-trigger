# mongodb-trigger

类似关系数据库的触发器，mongodb-trigger同样可以监听操作事件，粒度可以达到字段级别。

### 使用场景

* 同步mongodb数据到异构存储，如:mongo -> elasticsearch
* 业务cache刷新,如:redis中镜像数据
* 监听数据变化，触发一些业务逻辑

...

### 现实原理

实时读取oplog操作日志(副本集的复制靠的也是oplog),转化成相应的事件流输出，通过配置过滤不必要的事件。

### 使用方式

在"config.json"定义触发条件 

```json
    {
      db: "test_db",
      collection: "user",
      handler: "org.wzj.mongodb.trigger.UserHandler"
    }
```

编写handler现实
```java
package org.wzj.mongodb.trigger;

public class UserHandler implements Handler {
    @Override
    public void process(Stream stream) {
        //do something
        System.out.println(stream);
    }
}
```

打包部署
```sh
cd $mongodb-trigger
mvn clean package
```
启动
```sh
cd target/mongodb-trigger-1.0.0-package/mongodb-trigger-1.0.0
sh start-server.sh
```

### todo list

* 引入Groovy，handler可以使用groovy编写
* handler插件化
* 性能优化


