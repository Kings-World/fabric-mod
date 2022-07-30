# 1.0.0-beta1.8

- Replace Gradle Shadow with Fabric-Loom's include feature
  - Required to submit the mod on Modrinth
- Replace `org.spongepowered:configurate-yaml` with `org.bspfsystems:yamlconfiguration`
  - Configurate does not work with Fabric-Loom
  - Note: Confabricate does not support 1.19
- Included dependencies required by `club.minnced:discord-webhooks`
  - I may make a custom discord webhooks client in the future to reduce the JAR file size
- Removed the source link (until I open source the repository)
