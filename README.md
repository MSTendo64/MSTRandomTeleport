# MSTRandomTeleport
[![Java CI with Maven](https://github.com/MSTendo64/MSTRandomTeleport/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/MSTendo64/MSTRandomTeleport/actions/workflows/maven.yml)

**Version:** 1.15.0  
**Author:** MSTendo64  
**API version:** 1.16+  
**Java version:** 1.8+

## Description

MSTRandomTeleport is an advanced plugin for random teleportation of players on Minecraft servers. The plugin supports multiple teleportation channels, various location generation types, cost system, cooldowns, integrations with popular plugins, and much more.

## Core Features

### ✨ Features

- **Multiple Teleportation Channels** — create unlimited channels with individual settings
- **Three Teleportation Types:**
  - `DEFAULT` — standard random teleportation
  - `NEAR_PLAYER` — teleportation near other players
  - `NEAR_REGION` — teleportation near WorldGuard regions
- **Flexible Cost System:**
  - Money (Vault or PlayerPoints)
  - Hunger
  - Experience
- **Cooldown System:**
  - General cooldown after teleportation
  - Pre-teleportation cooldown (with countdown)
  - Group cooldowns (for different groups from Vault/LuckPerms)
- **Restrictions During Teleportation:**
  - Movement restriction
  - Damage restriction
  - Damage dealing restriction
  - Other teleportation restriction
- **Action System:**
  - Messages (with hover/click event support)
  - Sounds
  - Titles
  - Effects
  - ActionBar
  - Console commands
- **Integrations:**
  - Vault (economy)
  - PlayerPoints
  - WorldGuard
  - Towny
  - PlaceholderAPI
- **Visual Effects:**
  - Countdown bossbar
  - Particles before and after teleportation
  - Customizable animations
- **Location Filters:**
  - Black/whitelist of blocks
  - Black/whitelist of biomes
  - WorldGuard region exclusion
  - Towny town exclusion
- **Proxy Server Support** (BungeeCord/Velocity)

## Commands

### For Players

- `/rtp` — teleportation via default channel
- `/rtp <channel>` — teleportation via specified channel
- `/rtp cancel` — cancel current teleportation

### For Administrators

- `/rtp admin reload` — reload the plugin
- `/rtp admin forceteleport <player> <channel> [force]` — force teleport a player

## Permissions

- `rtp.use` — basic ability to use the `/rtp` command
- `rtp.channel.<channel>` — access to a specific teleportation channel
- `rtp.bypasscooldown` — bypass cooldowns
- `rtp.admin` — access to admin commands

## Dependencies

### Required
- **Spigot/Paper 1.16.5+** (or higher)

### Optional (softdepend)
- **Vault** — for economy
- **WorldGuard** — for teleportation near regions
- **Towny** — for town exclusion
- **PlayerPoints** — alternative currency
- **PlaceholderAPI** — for placeholders

## Installation

1. Download the latest version of the plugin
2. Place the `MSTRandomTeleport.jar` file in the `plugins/` folder
3. Restart the server
4. Configure settings in `plugins/MSTRandomTeleport/config.yml`
5. Configure channels in `plugins/MSTRandomTeleport/channels/`

## Configuration

### Main Config (`config.yml`)

```yaml
# Main settings
main_settings:
  # Text formatting: LEGACY, LEGACY_ADVANCED, MINIMESSAGE
  serializer: LEGACY
  # Random teleport command
  rtp_command: 'rtp'
  # Command aliases
  rtp_aliases: ['randomteleport', 'msrandomteleport']
  # Default channel name
  default_channel: 'default'
  # Enable PlaceholderAPI support?
  papi_support: true
  # Send anonymous statistics
  enable_metrics: true
  # Check for updates
  update_checker: true
  # Proxy settings
  proxy:
    enabled: false
    server_id: 'server1'

# Random teleport channels
channels:
  default:
    file: 'default.yml'
  near:
    file: 'near.yml'
  base:
    file: 'base.yml'

# Plugin messages
messages:
  prefix: '&7&l(&5&lMSTRandomTeleport&7&l) &6»&r'
  no_perms: '%prefix% &cYou do not have permission to use this channel.'
  invalid_world: '%prefix% &fYou cannot use this random teleport channel in this world.'
  not_enough_players: '%prefix% &cThere are not enough players on the server to teleport via this channel. Required: &6%required% players.'
  not_enough_money: '%prefix% &cYou do not have enough money to teleport via this channel. Cost: &6%required% coins.'
  not_enough_hunger: '%prefix% &cYou do not have enough hunger points to teleport via this channel. Required: &6%required%.'
  not_enough_experience: '%prefix% &cYou do not have enough experience points to teleport via this channel. Required: &6%required%.'
  cooldown: '%prefix% &fYou cannot teleport via this channel yet. Please wait &6%time%'
  moved_on_teleport: '%prefix% &cYou moved! Teleportation cancelled.'
  teleported_on_teleport: '%prefix% &cYou teleported during RTP! Teleportation cancelled.'
  damaged_on_teleport: '%prefix% &cYou took damage! Teleportation cancelled.'
  damaged_other_on_teleport: '%prefix% &cYou dealt damage! Teleportation cancelled.'
  fail_to_find_location: '%prefix% &6Failed to find a suitable location. Please try again later!'
  incorrect_channel: '%prefix% &cThe specified random teleport channel does not exist!'
  channel_not_specified: '%prefix% &cYou must specify a random teleport channel!'
  canceled: '%prefix% &aTeleportation cancelled!'
```

### Channel Example (`channels/default.yml`)

```yaml
# Channel name
name: 'Default'
# Channel type: DEFAULT, NEAR_PLAYER, NEAR_REGION
type: DEFAULT
# Worlds in which the channel is active
active_worlds:
  - 'world'
  - 'world_nether'
  - 'world_the_end'
# Teleport to first world from list if player is in another world
teleport_to_first_world: true
# Minimum number of players to use the channel (-1 to disable)
min_players_to_use: -1
# Invulnerability ticks after teleportation (-1 to disable)
invulnerable_after_teleport: 12

# Teleportation cost settings
costs:
  # Currency type: VAULT or PLAYERPOINTS
  money_type: VAULT
  # Cost in coins (-1 to disable)
  money_cost: -1
  # Cost in hunger units (-1 to disable)
  hunger_cost: -1
  # Cost in experience points (-1 to disable)
  experience_cost: -1

# Location selection principle
location_generation_options:
  # Shape template: SQUARE or ROUND
  shape: SQUARE
  # Generation format: RECTANGULAR or RADIAL
  gen_format: RECTANGULAR
  # Coordinates
  min_x: -1000
  max_x: 1000
  min_z: -1000
  max_z: 1000
  # Center for RADIAL format
  center_x: 0
  center_z: 0
  # Distance for NEAR_* types
  min_near_point_distance: 30
  max_near_point_distance: 60
  # Maximum number of attempts to find a location
  max_location_attempts: 50

# Cooldown settings
cooldown:
  # Default cooldown after teleportation in seconds (-1 to disable)
  default_cooldown: 60
  # Group cooldowns
  group_cooldowns:
    vip: 30
    premium: 10
  # Pre-teleportation cooldown in seconds (-1 to disable)
  default_pre_teleport_cooldown: 5
  # Pre-teleportation cooldowns for groups
  pre_teleport_group_cooldowns:
    vip: 1

# Countdown bossbar settings
bossbar:
  enabled: true
  title: '&fTeleportation in: &5%time%'
  color: WHITE
  style: SEGMENTED_12

# Particle settings
particles:
  pre_teleport:
    enabled: false
    send_only_to_player: true
    id:
      - FLAME
    dots: 2
    radius: 1.25
    particle_speed: 0.0
    speed: 4.0
    invert: false
    jumping: true
    move_near: true
  after_teleport:
    enabled: true
    send_only_to_player: true
    id: CLOUD
    count: 45
    radius: 1.25
    particle_speed: 0.0

# Restrictions during teleportation
restrictions:
  move: true
  teleport: true
  damage: true
  damage_others: false
  damage_check_only_players: true

# Teleportation exceptions
avoid:
  blocks:
    blacklist: true
    list:
      - 'LAVA'
      - 'WATER'
  biomes:
    blacklist: true
    list:
      - 'OCEAN'
  regions: true
  towns: false

# Actions during teleportation
actions:
  pre_teleport:
    - '[MESSAGE] &7&l(&5&lMSTRandomTeleport&7&l) &6» &fYou will be teleported in %time% Do not move or take damage. &6(Cancel RTP - /rtp cancel)'
    - '[SOUND] BLOCK_NOTE_BLOCK_PLING;1;1'
  on_cooldown:
    3:
      - '[TITLE] &aTeleport in &e3...;&r;5;50;10'
    2:
      - '[TITLE] &aTeleport in &62...;&r;5;50;10'
    1:
      - '[TITLE] &aTeleport in &c1...;&r;5;50;10'
  after_teleport:
    - '[MESSAGE] &7&l(&5&lMSTRandomTeleport&7&l) &6» &aSuccessful teleportation! &fYou teleported to coordinates: &2%x% %y% %z%.'
    - '[TITLE] &a&lSuccess!;&fYou teleported to coordinates: &2%x% %y% %z%.'
    - '[SOUND] ENTITY_PLAYER_LEVELUP;1;1'
```

## PlaceholderAPI

The plugin supports PlaceholderAPI with the prefix `%msrtp_%`.

### Available Placeholders

- `%msrtp_<channel>_hascooldown%` — whether the player has a cooldown for the channel
- `%msrtp_<channel>_cooldown%` — remaining cooldown time
- `%msrtp_<channel>_cooldown_hours%` — cooldown hours
- `%msrtp_<channel>_cooldown_minutes%` — cooldown minutes
- `%msrtp_<channel>_cooldown_seconds%` — cooldown seconds
- `%msrtp_<channel>_settings_name%` — channel name
- `%msrtp_<channel>_settings_type%` — channel type
- `%msrtp_<channel>_settings_playersrequired%` — minimum number of players
- `%msrtp_<channel>_settings_cost_money%` — cost in money
- `%msrtp_<channel>_settings_cost_hunger%` — cost in hunger
- `%msrtp_<channel>_settings_cost_exp%` — cost in experience

## Actions

### Action Format

- `[MESSAGE] <message>` — send a message to the player
  - Supports hover/click events: `hoverText={text}` and `clickEvent={action;value}`
  - Supports buttons: `button={Text;hoverText;clickEvent}`
- `[ACTIONBAR] <message>` — send a message to the action bar
- `[SOUND] <id>;<volume>;<pitch>` — play a sound
- `[TITLE] <title>;<subtitle>;<fadeIn>;<stay>;<fadeOut>` — show a title
- `[EFFECT] <effect>;<duration>;<amplifier>` — give an effect
- `[CONSOLE] <command>` — execute a console command

### Available Placeholders in Actions

- `%player%` — player nickname
- `%name%` — channel name
- `%time%` — time until teleportation
- `%x%`, `%y%`, `%z%` — location coordinates

## Usage Examples

### Creating a Channel for VIP Players

```yaml
channels:
  vip:
    file: 'vip.yml'
```

In `channels/vip.yml`:
```yaml
name: 'VIP Teleport'
type: DEFAULT
active_worlds:
  - 'world'
costs:
  money_type: VAULT
  money_cost: 500
cooldown:
  default_cooldown: 30
  default_pre_teleport_cooldown: 3
```

Permission: `rtp.channel.vip`

### Creating a Teleportation Channel Near Players

```yaml
channels:
  near_players:
    file: 'near_players.yml'
```

In `channels/near_players.yml`:
```yaml
name: 'Near Players'
type: NEAR_PLAYER
active_worlds:
  - 'world'
min_players_to_use: 5
location_generation_options:
  min_near_point_distance: 30
  max_near_point_distance: 90
```

### Creating a Teleportation Channel Near WorldGuard Regions

```yaml
channels:
  near_base:
    file: 'near_base.yml'
```

In `channels/near_base.yml`:
```yaml
name: 'Near Bases'
type: NEAR_REGION
active_worlds:
  - 'world'
costs:
  money_type: PLAYERPOINTS
  money_cost: 100
location_generation_options:
  min_near_point_distance: 30
  max_near_point_distance: 60
```

**Important:** WorldGuard must be installed to use the `NEAR_REGION` type!