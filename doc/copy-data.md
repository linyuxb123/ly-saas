帮我写个脚本，要求如下：
0、定义变量：version1 = "chun", version2 = "xia"，
    module1="ly-saas-" + version1，module2="ly-saas-" + version2
1、在back目录下创建当前日期目录，将module2模块下的所有文件复制到该目录，保持原目录结构
2、清空module2模块下的所有文件
3、将module1模块下的所有文件复制到module2下，保持原文件目录结构
4、完成复制后对module2模块下的文件进行如下调整，将下数替换中的chun 和 xia 分别改为变量 version1 和 version2：
    1、将目录中名称为chun的文件夹重命名为xia，将chun-开头的文件夹重命名为xia-
    2、将所有以.java结尾文件中的 PREFIX = "chun" 修改为 PREFIX = "xia"
    3、将所有以.java结尾文件中的 package com.ly.saas.chun 修改为 package com.ly.saas.xia
    4、将所有以.java结尾文件中的 import com.ly.saas.chun 改为 import com.ly.saas.xia
    5、将所有以.java结尾文件中的 @Qualifier("chun 改为 @Qualifier("xia
    6、将所有以.java结尾文件中的 @Component("chun 改为 @Component("xia
    7、将所有以.java结尾文件中的 @Resource(name = "chun 改为 @Resource(name = "xia
    8、将所有以.java结尾文件中的 @Service("chun 改为 @Service("xia
    9、将所有以.java结尾文件中的 @Repository("chun 改为 @Repository("xia
    10、将所有以.java结尾文件中的 @Alias("chun 改为 @Alias("xia
    11、将所有以.java结尾文件中的 @RestController("chun 改为 @RestController("xia
    12、将所有以.java结尾文件中的 @Controller("chun 改为 @Controller("xia
    13、将所有名为pom.xml文件中的 <artifactId>chun- 改为 <artifactId>xia-
    14、将所有名为pom.xml文件中的 <module>chun- 改为 <module>xia-
    15、将所有名为pom.xml文件中的 -chun</artifactId> 改为 -xia</artifactId>

注意名为pom.xml的文件不止有一个，都要尝试做替换，用shell编写，日志输出改为中文