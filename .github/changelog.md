# 1.0.0-beta1.8

- Replace Gradle Shadow with Fabric-Loom's include feature
  - Required to submit the mod on Modrinth
- Replace `org.spongepowered:configurate-yaml` with `org.bspfsystems:yamlconfiguration`
  - Configurate does not work with Fabric-Loom
  - Note: Confabricate does not support 1.19
- Included dependencies required by `club.minnced:discord-webhooks`
  - I may make a custom discord webhooks client because the required
  - Note: This increases the mod's JAR file with an additional 5MB
    (Before: 2mb, After: 7mb)

# 1.0.0-beta1.7

- Reload webhook from config
  - Close the old webhook if one exists
- Reload client from config
  - Shutdown the old client if one exists

# 1.0.0-beta1.6

- Changed configuration file from `kingsworld.properties` to `kingsworld/config.yml`
- Replaced config builder with configurate
- Added more user customization
- Replaced `/discord <message>` with `/kingsworld send <message>`
- Added `/kingsworld reload`

# 1.0.0-beta1.5

- Prevent sending animal deaths
- Added a message slash command
- Start the bot once the server has finished starting

# 1.0.0-beta1.4

- Disable webhook mentions
- Allow changing the bots status
- Allow changing the bots activity
- Removed the guild member intent

# 1.0.0-beta1.3

- Make deleting slash commands on server stop optional
- Shorten the notifying discord message
- Conditionally display shutdown logs

# 1.0.0-beta1.2

- Fixed issue of server getting stuck on "Thread Query Listener stopped"
- Close all connections that the mod makes

# 1.0.0-beta1.1

- Send player death messages
- Send player advancement unlocks

# 1.0.0-beta1.0

- Send basic discord content to minecraft
- Send player events to discord
  - Player join
  - Player leave
  - Player message
- Send server events to discord
  - Server start
  - Server stop
- Added commands
  - Created a list command for viewing online players from discord
  - Created a discord command for sending messages from minecraft to discord (permission level 4 or kings-world.discord required)
