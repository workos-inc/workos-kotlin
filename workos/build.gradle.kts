group = "com.workos"
version = "0.0.1"

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.5.0"

  id("org.jlleitschuh.gradle.ktlint") version "10.2.0"

  id("org.jetbrains.dokka") version "1.5.30"

  signing

  `java-library`

  `maven-publish`
}

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("com.google.guava:guava:30.1.1-jre")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")

  implementation("org.apache.httpcomponents:httpclient:4.2.3")

  testImplementation("org.jetbrains.kotlin:kotlin-test")

  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")

  testImplementation("com.github.tomakehurst:wiremock:2.27.2")

  dokkaGfmPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.30")

  api("org.apache.commons:commons-math3:3.6.1")
}

tasks.named("build") {
  finalizedBy("dokkaJavadoc")
}

tasks.dokkaJavadoc.configure {
  outputDirectory.set(buildDir.resolve("docs/javadoc"))
}

tasks.jar {
  manifest {
    attributes(
      mapOf(
        "Implementation-Title" to project.name,
        "Implementation-Version" to project.version
      )
    )
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      artifactId = "workos"
      version = "0.0.1-SNAPSHOT"

      from(components["java"])

      pom {
        name.set("WorkOS SDK")
        description.set("The WorkOS Kotlin library provides convenient access to the WorkOS API from applications written in JVM compatible languages.")
        url.set("https://github.com/workos-inc/workos-kotlin")
        licenses {
          license {
            name.set("MIT License")
            url.set("https://github.com/workos-inc/workos-kotlin/blob/main/LICENSE")
          }
        }
        developers {
          developer {
            id.set("rframpton")
            name.set("Robert Frampton")
            email.set("rob@workos.com")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/workos-inc/workos-kotlin.git")
          developerConnection.set("scm:git:git@github.com:workos-inc/workos-kotlin.git")
          url.set("https://github.com/workos-inc/workos-kotlin")
        }
      }
    }
  }

  repositories {
    maven {
      val ossrhUsername: String? by project
      val ossrhPassword: String? by project

      url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")

      credentials {
        username = ossrhUsername
        password = ossrhPassword
      }
    }
  }
}

signing {
  val signingKey: String? by project
  val signingPassword: String? by project

  if (signingKey != null) {
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
  }
}

java {
  withSourcesJar()
  withJavadocJar()
}
