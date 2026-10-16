fun readDotEnv(file: File): Map<String, String> {
    if (!file.isFile) return emptyMap()
    return file.readLines().mapNotNull { rawLine ->
        val line = rawLine.trim()
        if (line.isEmpty() || line.startsWith("#")) return@mapNotNull null
        val separator = line.indexOf('=')
        if (separator <= 0) return@mapNotNull null
        val key = line.substring(0, separator).trim()
        var value = line.substring(separator + 1).trim()
        if (value.length >= 2 &&
            ((value.startsWith("\"") && value.endsWith("\"")) ||
                (value.startsWith("'") && value.endsWith("'")))
        ) {
            value = value.substring(1, value.length - 1)
        }
        key to value
    }.toMap()
}

val dotEnv = readDotEnv(settings.rootDir.resolve(".env"))

fun credential(name: String): String =
    System.getenv(name)?.takeIf { it.isNotBlank() } ?: dotEnv[name].orEmpty()

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/hammerheadnav/karoo-ext")
            credentials {
                username = credential("GITHUB_ACTOR")
                password = credential("GITHUB_TOKEN")
            }
        }
    }
}

rootProject.name = "HRView"
include(":app")
