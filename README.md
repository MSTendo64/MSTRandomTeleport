# MSTRandomTeleport

**Версия:** 1.13.0  
**Автор:** mstendo  
**API версия:** 1.16+  
**Java версия:** 1.8+

## Описание

MSTRandomTeleport — это продвинутый плагин для рандомной телепортации игроков на серверах Minecraft. Плагин поддерживает множественные каналы телепортации, различные типы генерации локаций, систему стоимости, кулдауны, интеграции с популярными плагинами и многое другое.

## Основной функционал

### ✨ Возможности

- **Множественные каналы телепортации** — создавайте неограниченное количество каналов с индивидуальными настройками
- **Три типа телепортации:**
  - `DEFAULT` — стандартная рандомная телепортация
  - `NEAR_PLAYER` — телепортация рядом с другими игроками
  - `NEAR_REGION` — телепортация рядом с регионами WorldGuard
- **Гибкая система стоимости:**
  - Деньги (Vault или PlayerPoints)
  - Голод
  - Опыт
- **Система кулдаунов:**
  - Общий кулдаун после телепортации
  - Кулдаун до телепортации (с обратным отсчетом)
  - Групповые кулдауны (для разных групп из Vault/LuckPerms)
- **Ограничения во время телепортации:**
  - Запрет движения
  - Запрет получения урона
  - Запрет нанесения урона
  - Запрет других телепортаций
- **Система действий:**
  - Сообщения (с поддержкой hover/click событий)
  - Звуки
  - Тайтлы
  - Эффекты
  - ActionBar
  - Команды консоли
- **Интеграции:**
  - Vault (экономика)
  - PlayerPoints
  - WorldGuard
  - Towny
  - PlaceholderAPI
- **Визуальные эффекты:**
  - Боссбар обратного отсчета
  - Партиклы до и после телепортации
  - Настраиваемые анимации
- **Фильтры локаций:**
  - Черный/белый список блоков
  - Черный/белый список биомов
  - Исключение регионов WorldGuard
  - Исключение городов Towny
- **Поддержка прокси-серверов** (BungeeCord/Velocity)

## Команды

### Для игроков

- `/rtp` — телепортация по дефолтному каналу
- `/rtp <канал>` — телепортация по указанному каналу
- `/rtp cancel` — отмена текущей телепортации

### Для администраторов

- `/rtp admin reload` — перезагрузка плагина
- `/rtp admin forceteleport <игрок> <канал> [force]` — принудительная телепортация игрока

## Разрешения

- `rtp.use` — базовая возможность использовать команду `/rtp`
- `rtp.channel.<канал>` — доступ к конкретному каналу телепортации
- `rtp.bypasscooldown` — обход кулдаунов
- `rtp.admin` — доступ к админ-командам

## Зависимости

### Обязательные
- **Spigot/Paper 1.16.5+** (или выше)

### Опциональные (softdepend)
- **Vault** — для экономики
- **WorldGuard** — для телепортации рядом с регионами
- **Towny** — для исключения городов
- **PlayerPoints** — альтернативная валюта
- **PlaceholderAPI** — для плейсхолдеров

## Установка

1. Скачайте последнюю версию плагина
2. Поместите файл `MSTRandomTeleport-1.0.jar` в папку `plugins/`
3. Перезапустите сервер
4. Настройте конфигурацию в `plugins/MSTRandomTeleport/config.yml`
5. Настройте каналы в `plugins/MSTRandomTeleport/channels/`

## Конфигурация

### Основной конфиг (`config.yml`)

```yaml
# Основные настройки
main_settings:
  # Форматирование текста: LEGACY, LEGACY_ADVANCED, MINIMESSAGE
  serializer: LEGACY
  # Команда рандомной телепортации
  rtp_command: 'rtp'
  # Алиасы к команде
  rtp_aliases: ['randomteleport', 'msrandomteleport']
  # Название дефолтного канала
  default_channel: 'default'
  # Включить ли поддержку PlaceholderAPI?
  papi_support: true
  # Отправлять ли анонимную статистику
  enable_metrics: true
  # Проверять ли на наличие обновлений
  update_checker: true
  # Настройки работы с прокси
  proxy:
    enabled: false
    server_id: 'server1'

# Каналы рандомной телепортации
channels:
  default:
    file: 'default.yml'
  near:
    file: 'near.yml'
  base:
    file: 'base.yml'

# Сообщения плагина
messages:
  prefix: '&7&l(&5&lMSTRandomTeleport&7&l) &6»&r'
  no_perms: '%prefix% &cУ вас не достаточно прав на использование данного канала.'
  invalid_world: '%prefix% &fВы не можете использовать данный канал рандомной телепортации в этом мире.'
  not_enough_players: '%prefix% &cНа сервере не достаточно игроков чтобы телепортироваться по данному каналу. Нужно: &6%required% игроков.'
  not_enough_money: '%prefix% &cУ вас не достаточно денег чтобы телепортироваться по данному каналу. Стоимость: &6%required% монет.'
  not_enough_hunger: '%prefix% &cУ вас не достаточно очков голода чтобы телепортироваться по данному каналу. Необходимо: &6%required%.'
  not_enough_experience: '%prefix% &cУ вас не достаточно очков опыта чтобы телепортироваться по данному каналу. Необходимо: &6%required%.'
  cooldown: '%prefix% &fВы еще не можете телепортироваться по данному каналу. Подождите еще &6%time%'
  moved_on_teleport: '%prefix% &cВы двинулись! Телепортация отменена.'
  teleported_on_teleport: '%prefix% &cВы телепортировались в процессе РТП! Телепортация отменена.'
  damaged_on_teleport: '%prefix% &cВы получили урон! Телепортация отменена.'
  damaged_other_on_teleport: '%prefix% &cВы нанесли урон! Телепортация отменена.'
  fail_to_find_location: '%prefix% &6Не удалось подобрать необходимую локацию. Попробуйте снова позже!'
  incorrect_channel: '%prefix% &cУказанного канала рандомной телепортации не существует!'
  channel_not_specified: '%prefix% &cВам необходимо указать канал рандомной телепортации!'
  canceled: '%prefix% &aТелепортация отменена!'
```

### Пример канала (`channels/default.yml`)

```yaml
# Название канала
name: 'Стандартный'
# Тип канала: DEFAULT, NEAR_PLAYER, NEAR_REGION
type: DEFAULT
# Миры в которых действует канал
active_worlds:
  - 'world'
  - 'world_nether'
  - 'world_the_end'
# Телепортировать в первый мир из списка, если игрок в другом мире
teleport_to_first_world: true
# Минимальное количество игроков для использования канала (-1 чтобы отключить)
min_players_to_use: -1
# Тики неуязвимости после телепортации (-1 чтобы отключить)
invulnerable_after_teleport: 12

# Настройка цены телепортации
costs:
  # Тип валюты: VAULT или PLAYERPOINTS
  money_type: VAULT
  # Цена в монетах (-1 чтобы отключить)
  money_cost: -1
  # Цена в единицах голода (-1 чтобы отключить)
  hunger_cost: -1
  # Цена в единицах опыта (-1 чтобы отключить)
  experience_cost: -1

# Принцип выбора локации
location_generation_options:
  # Форма заготовки: SQUARE или ROUND
  shape: SQUARE
  # Формат генерации: RECTANGULAR или RADIAL
  gen_format: RECTANGULAR
  # Координаты
  min_x: -1000
  max_x: 1000
  min_z: -1000
  max_z: 1000
  # Центр для RADIAL формата
  center_x: 0
  center_z: 0
  # Расстояние для NEAR_* типов
  min_near_point_distance: 30
  max_near_point_distance: 60
  # Максимальное количество попыток найти локацию
  max_location_attempts: 50

# Настройки кулдаунов
cooldown:
  # Дефолтный кулдаун после телепортации в секундах (-1 чтобы отключить)
  default_cooldown: 60
  # Кулдауны для групп
  group_cooldowns:
    vip: 30
    premium: 10
  # Кулдаун до телепортации в секундах (-1 чтобы отключить)
  default_pre_teleport_cooldown: 5
  # Кулдауны до телепортации для групп
  pre_teleport_group_cooldowns:
    vip: 1

# Настройка боссбара обратного отсчета
bossbar:
  enabled: true
  title: '&fТелепортация через: &5%time%'
  color: WHITE
  style: SEGMENTED_12

# Настройка партиклов
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

# Запреты во время телепортации
restrictions:
  move: true
  teleport: true
  damage: true
  damage_others: false
  damage_check_only_players: true

# Исключения телепортации
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

# Действия при телепортации
actions:
  pre_teleport:
    - '[MESSAGE] &7&l(&5&lMSTRandomTeleport&7&l) &6» &fВы будете телепортированы через %time% Не двигайтесь и не получайте урона. &6(Отменить РТП - /rtp cancel)'
    - '[SOUND] BLOCK_NOTE_BLOCK_PLING;1;1'
  on_cooldown:
    3:
      - '[TITLE] &aТелепорт через &e3...;&r;5;50;10'
    2:
      - '[TITLE] &aТелепорт через &62...;&r;5;50;10'
    1:
      - '[TITLE] &aТелепорт через &c1...;&r;5;50;10'
  after_teleport:
    - '[MESSAGE] &7&l(&5&lMSTRandomTeleport&7&l) &6» &aУспешная телепортация! &fВы телепортировались на координаты: &2%x% %y% %z%.'
    - '[TITLE] &a&lУспех!;&fВы телепортировались на координаты: &2%x% %y% %z%.'
    - '[SOUND] ENTITY_PLAYER_LEVELUP;1;1'
```

## PlaceholderAPI

Плагин поддерживает PlaceholderAPI с префиксом `%msrtp_%`.

### Доступные плейсхолдеры

- `%msrtp_<канал>_hascooldown%` — есть ли кулдаун у игрока для канала
- `%msrtp_<канал>_cooldown%` — оставшееся время кулдауна
- `%msrtp_<канал>_cooldown_hours%` — часы кулдауна
- `%msrtp_<канал>_cooldown_minutes%` — минуты кулдауна
- `%msrtp_<канал>_cooldown_seconds%` — секунды кулдауна
- `%msrtp_<канал>_settings_name%` — название канала
- `%msrtp_<канал>_settings_type%` — тип канала
- `%msrtp_<канал>_settings_playersrequired%` — минимальное количество игроков
- `%msrtp_<канал>_settings_cost_money%` — стоимость в деньгах
- `%msrtp_<канал>_settings_cost_hunger%` — стоимость в голоде
- `%msrtp_<канал>_settings_cost_exp%` — стоимость в опыте

## Действия (Actions)

### Формат действий

- `[MESSAGE] <сообщение>` — отправить сообщение игроку
  - Поддерживает hover/click события: `hoverText={текст}` и `clickEvent={action;значение}`
  - Поддерживает кнопки: `button={Текст;hoverText;clickEvent}`
- `[ACTIONBAR] <сообщение>` — отправить сообщение в action bar
- `[SOUND] <id>;<громкость>;<тон>` — воспроизвести звук
- `[TITLE] <тайтл>;<субтайтл>;<fadeIn>;<stay>;<fadeOut>` — показать тайтл
- `[EFFECT] <эффект>;<время>;<уровень>` — выдать эффект
- `[CONSOLE] <команда>` — выполнить команду от консоли

### Доступные плейсхолдеры в действиях

- `%player%` — ник игрока
- `%name%` — имя канала
- `%time%` — время до телепортации
- `%x%`, `%y%`, `%z%` — координаты локации

## Примеры использования

### Создание канала для VIP игроков

```yaml
channels:
  vip:
    file: 'vip.yml'
```

В `channels/vip.yml`:
```yaml
name: 'VIP Телепорт'
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

Разрешение: `rtp.channel.vip`

### Создание канала телепортации рядом с игроками

```yaml
channels:
  near_players:
    file: 'near_players.yml'
```

В `channels/near_players.yml`:
```yaml
name: 'Возле игроков'
type: NEAR_PLAYER
active_worlds:
  - 'world'
min_players_to_use: 5
location_generation_options:
  min_near_point_distance: 30
  max_near_point_distance: 90
```

### Создание канала телепортации рядом с регионами WorldGuard

```yaml
channels:
  near_base:
    file: 'near_base.yml'
```

В `channels/near_base.yml`:
```yaml
name: 'Возле баз'
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

**Важно:** Для работы типа `NEAR_REGION` требуется установленный WorldGuard!