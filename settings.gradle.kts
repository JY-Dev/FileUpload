rootProject.name = "FileUpload"

val modulesDir = File(rootProject.projectDir, "app")
val modulesDomainDir = File(rootProject.projectDir, "domain")
val modulesCoreDir = File(rootProject.projectDir, "core")

modulesDir.listFiles()?.forEach { dir ->
    if(dir.isDirectory) {
        include("app:${dir.name}")
    }
}

modulesDomainDir.listFiles()?.forEach { dir ->
    if(dir.isDirectory) {
        include("domain:${dir.name}")
    }
}

modulesCoreDir.listFiles()?.forEach { dir ->
    if(dir.isDirectory) {
        include("core:${dir.name}")
    }
}