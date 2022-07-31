# common-core

[![Build Status](https://img.shields.io/travis/com/fangzhengjin/common-core/master.svg?style=flat-square)](https://travis-ci.com/fangzhengjin/common-core)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.fangzhengjin/common-core.svg?style=flat-square&color=brightgreen)](https://maven-badges.herokuapp.com/maven-central/com.github.fangzhengjin/common-core/)
[![Bintray](https://img.shields.io/bintray/v/fangzhengjin/maven/common-core.svg?style=flat-square&color=blue)](https://bintray.com/fangzhengjin/maven/common-core/_latestVersion)
[![License](https://img.shields.io/github/license/fangzhengjin/common-core.svg?style=flat-square&color=blue)](https://www.gnu.org/licenses/gpl-3.0.txt)
[![SpringBootVersion](https://img.shields.io/badge/SpringBoot-2.1.5-heightgreen.svg?style=flat-square)](https://spring.io/projects/spring-boot)

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
