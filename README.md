# Pumpkin - WIP
A Sponge utility plugin.

## What?

Modular plugin for Sponge that does basic tasks for a server: admin commands, chat formatting et cedera.

## Why?

I wanted to get into the SpongeAPI.

## How?

1. Clone it
1. Build it (`mvn clean install`)
1. Use it (move `target/Pumpkin-*-SNAPSHOT.jar` to your plugins folder)
1. Configure it

### Default configuration

```hocon
# Contains database configuration.
database {
    database=minecraft
    host=localhost
    password="please enter a password here"
    port=3306
    "table prefix"="pumpkin_"
    type=MYSQL
    username=root
}

# Enables / Disables Debug mode.
debug=false

# This array contains all enabled modules and commands.
"enabled features"=[
    chat,
    punishments,
    "disable_join_messages",
    "cmd_gamemode",
    "cmd_teleport",
    "cmd_pumpkin",
    "cmd_fly",
    "cmd_heal",
    "cmd_feed"
]
```

## Contributing

1. Setup like above
1. Code your stuff
1. Make a PR