# common-core

[![Build Status](https://img.shields.io/travis/com/fangzhengjin/common-core/master.svg?style=flat-square)](https://travis-ci.com/fangzhengjin/common-core)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.fangzhengjin/common-core.svg?style=flat-square&color=brightgreen)](https://maven-badges.herokuapp.com/maven-central/com.github.fangzhengjin/common-core/)
![GitHub](https://img.shields.io/github/license/fangzhengjin/common-core?style=flat-square)

```
dependencies {
    implementation "me.zhengjin:common-core:version"
}
```

新增了几个JacksonJson控制序列化的注解

```

四个注解可加载spring controller的方法上 控制返回的json

序列化时忽略列出的字段
@JsonSerializeExclude
@JsonSerializeExcludes

只序列化列出的字段
@JsonSerializeInclude
@JsonSerializeIncludes

不适用注解的地方可以使用工具
JacksonSerializeUtils

```
