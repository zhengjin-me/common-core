import com.jfrog.bintray.gradle.BintrayExtension

apply(plugin = "com.jfrog.bintray")

val bintrayUser: String by extra
val artifactId: String by extra
val githubRepoName: String by extra {
    extra.get("groupName") as String
}
val repoUrl: String by extra
val gitUrl: String by extra
val issueUrl: String by extra
val pomLicenceName: String by extra
val pomDescription: String by extra

configure<BintrayExtension> {
    user = bintrayUser
    key = ""
    setPublications("mavenJava")
    dryRun = false
    publish = true
    pkg(closureOf<BintrayExtension.PackageConfig> {
        //你在bintray建立的仓库类型
        repo = "maven"
        //包名
        name = artifactId
        //描述
        desc = pomDescription
        //站点地址
        websiteUrl = repoUrl
        //缺陷提交地址
        issueTrackerUrl = issueUrl
        //版本库地址
        vcsUrl = gitUrl
        //许可证
        setLicenses(pomLicenceName)
//        setLabels("")
        publicDownloadNumbers = true
        //Github仓库相对地址
        githubRepo = githubRepoName
        //版本历程文件
        githubReleaseNotesFile = "README.md" //Optional Github readme file
    })
}