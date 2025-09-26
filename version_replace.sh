#!/bin/bash

# 0. 定义变量
version1="shu"
version2="wei"
module1="ly-saas-${version1}"
module2="ly-saas-${version2}"

echo "开始模块转换：从 ${module1} 到 ${module2}"

# 1. 在back目录下创建当前日期目录，将module2模块下的所有文件复制到该目录
current_date=$(date +%Y%m%d)
backup_dir="back/${current_date}"
mkdir -p "${backup_dir}"
if [ -d "${module2}" ]; then
    echo "正在备份 ${module2} 到 ${backup_dir}/${module2}"
    cp -r "${module2}" "${backup_dir}/"
else
    echo "警告：未找到 ${module2} 目录，创建空的备份目录"
    mkdir -p "${backup_dir}/${module2}"
fi

# 2. 清空module2模块下的所有文件
if [ -d "${module2}" ]; then
    echo "正在清空 ${module2} 目录"
    rm -rf "${module2}"/*
else
    echo "正在创建 ${module2} 目录"
    mkdir -p "${module2}"
fi

# 3. 将module1模块下的所有文件复制到module2下
if [ -d "${module1}" ]; then
    echo "正在复制 ${module1} 到 ${module2}"
    cp -r "${module1}"/* "${module2}"/
else
    echo "错误：源目录 ${module1} 未找到"
    exit 1
fi

# 4. 完成复制后对module2模块下的文件进行调整

# 4.1 将目录中名称为chun的文件夹重命名为xia，将chun-开头的文件夹重命名为xia-
echo "正在重命名目录..."
# 先处理chun-开头的目录
find "${module2}" -type d -name "${version1}-*" | while read dir; do
    dir_name=$(basename "$dir")
    new_name=$(echo "$dir_name" | sed "s/^${version1}-/${version2}-/")
    new_dir=$(dirname "$dir")/"$new_name"
    echo "重命名目录: $dir -> $new_dir"
    mv "$dir" "$new_dir"
done

# 再处理名称为chun的目录
find "${module2}" -type d -name "${version1}" | while read dir; do
    new_dir=$(echo "$dir" | sed "s|${version1}$|${version2}|")
    echo "重命名目录: $dir -> $new_dir"
    mv "$dir" "$new_dir"
done

# 重新确定module2路径（因为可能有目录重命名）
module2_updated="${module2}"

# 4.2-4.12 处理所有 .java 文件
find "${module2_updated}" -type f -name "*.java" | while read file; do
    echo "正在处理Java文件: $file"

    # 4.2 将 PREFIX = "chun" 修改为 PREFIX = "xia"
    sed -i '' "s/PREFIX = \"${version1}\"/PREFIX = \"${version2}\"/g" "$file" 2>/dev/null ||
    sed -i "s/PREFIX = \"${version1}\"/PREFIX = \"${version2}\"/g" "$file"

    # 4.3 将 package com.ly.saas.chun 修改为 package com.ly.saas.xia
    sed -i '' "s/package com.ly.saas.${version1}/package com.ly.saas.${version2}/g" "$file" 2>/dev/null ||
    sed -i "s/package com.ly.saas.${version1}/package com.ly.saas.${version2}/g" "$file"

    # 4.4 将 import com.ly.saas.chun 改为 import com.ly.saas.xia
    sed -i '' "s/import com.ly.saas.${version1}/import com.ly.saas.${version2}/g" "$file" 2>/dev/null ||
    sed -i "s/import com.ly.saas.${version1}/import com.ly.saas.${version2}/g" "$file"

    # 4.5 将 @Qualifier("chun 改为 @Qualifier("xia
    sed -i '' "s/@Qualifier(\"${version1}/@Qualifier(\"${version2}/g" "$file" 2>/dev/null ||
    sed -i "s/@Qualifier(\"${version1}/@Qualifier(\"${version2}/g" "$file"

    # 4.6 将 @Component("chun 改为 @Component("xia
    sed -i '' "s/@Component(\"${version1}/@Component(\"${version2}/g" "$file" 2>/dev/null ||
    sed -i "s/@Component(\"${version1}/@Component(\"${version2}/g" "$file"

    # 4.7 将 @Resource(name = "chun 改为 @Resource(name = "xia"
    sed -i '' "s/@Resource(name = \"${version1}/@Resource(name = \"${version2}/g" "$file" 2>/dev/null ||
    sed -i "s/@Resource(name = \"${version1}/@Resource(name = \"${version2}/g" "$file"

    # 4.8 将 @Service("chun 改为 @Service("xia
    sed -i '' "s/@Service(\"${version1}/@Service(\"${version2}/g" "$file" 2>/dev/null ||
    sed -i "s/@Service(\"${version1}/@Service(\"${version2}/g" "$file"

    # 4.9 将 @Repository("chun 改为 @Repository("xia
    sed -i '' "s/@Repository(\"${version1}/@Repository(\"${version2}/g" "$file" 2>/dev/null ||
    sed -i "s/@Repository(\"${version1}/@Repository(\"${version2}/g" "$file"

    # 4.10 将 @Alias("chun 改为 @Alias("xia
    sed -i '' "s/@Alias(\"${version1}/@Alias(\"${version2}/g" "$file" 2>/dev/null ||
    sed -i "s/@Alias(\"${version1}/@Alias(\"${version2}/g" "$file"

    # 4.11 将 @RestController("chun 改为 @RestController("xia
    sed -i '' "s/@RestController(\"${version1}/@RestController(\"${version2}/g" "$file" 2>/dev/null ||
    sed -i "s/@RestController(\"${version1}/@RestController(\"${version2}/g" "$file"

    # 4.12 将 @Controller("chun 改为 @Controller("xia
    sed -i '' "s/@Controller(\"${version1}/@Controller(\"${version2}/g" "$file" 2>/dev/null ||
    sed -i "s/@Controller(\"${version1}/@Controller(\"${version2}/g" "$file"
done

# 4.13-4.15 处理所有 pom.xml 文件
find "${module2_updated}" -type f -name "pom.xml" | while read pom_file; do
    echo "正在处理 pom.xml: $pom_file"

    # 4.13 将 <artifactId>chun- 改为 <artifactId>xia-
    sed -i '' "s|<artifactId>${version1}-|<artifactId>${version2}-|g" "$pom_file" 2>/dev/null ||
    sed -i "s|<artifactId>${version1}-|<artifactId>${version2}-|g" "$pom_file"

    # 4.14 将 <module>chun- 改为 <module>xia-
    sed -i '' "s|<module>${version1}-|<module>${version2}-|g" "$pom_file" 2>/dev/null ||
    sed -i "s|<module>${version1}-|<module>${version2}-|g" "$pom_file"

    # 4.15 将 -chun</artifactId> 改为 -xia</artifactId>
    sed -i '' "s|-${version1}</artifactId>|-${version2}</artifactId>|g" "$pom_file" 2>/dev/null ||
    sed -i "s|-${version1}</artifactId>|-${version2}</artifactId>|g" "$pom_file"
done

echo "模块转换完成！"
