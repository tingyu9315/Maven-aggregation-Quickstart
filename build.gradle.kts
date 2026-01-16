plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.wd"
version = "2.1.0"

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2020.3") // 使用最早兼容的版本作为构建 SDK
    type.set("IU") // 表示 IntelliJ IDEA Ultimate。IDEA Community的IC，IDEA Ultimate的IU
    //设置各版本兼容
    updateSinceUntilBuild.set(false) // 不自动更新 plugin.xml 中的 since-build/until-build
    plugins.set(listOf()) // 如果没有依赖其他插件
}


tasks {
    // Set the JVM compatibility versions
//    withType<JavaCompile> {
//        sourceCompatibility = "17"
//        targetCompatibility = "17"
//    }
//    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        kotlinOptions.jvmTarget = "17"
//    }

    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }


    patchPluginXml {
        sinceBuild.set("203") // 2020.3 的构建号是 203
        untilBuild.set("253.*") // 表示支持到未来所有版本（例如支持到 2099 年）
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
