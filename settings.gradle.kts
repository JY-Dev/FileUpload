rootProject.name = "FileUpload"

val modulesDir = File(rootProject.projectDir, "app")
val modulesDomainDir = File(rootProject.projectDir, "domain")

modulesDir.listFiles()?.forEach { dir ->
    include("app:${dir.name}")
}

modulesDomainDir.listFiles()?.forEach { dir ->
    include("domain:${dir.name}")
}