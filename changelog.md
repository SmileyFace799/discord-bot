# Changelog:

## v1.8.1
**NB: Older versions do not support YouTube oauth2 authentication, and may therefore not be able to play most songs**
### Technical:
- Added: Oauth2 authentication option for YouTube
- Added: New .properties template that includes a YouTube oauth2 token property
- Modified: Updated `lavaplayer` to version `2.2.2`
- Modified: Updated `v2` (`youtube-source`) to version `1.8.3`

## v1.8.0
### Features:
- Added: Genius support for song lyrics (`/lyrics`)
- Added: Selection prompts
- Modified: Fixed song search being broken
- Modified: Split the changelog into a features & a technical section
- Modified: Added emojis to `shuffle` & `repeat` player buttons

### Technical:
- Added: `GeniusLyricsAPI` (`com.github.LowLevelSubmarine`) version `1.0.3` as a dependency
- Added: Node (This replaces MultiTypeMap & PropertyNode)
- Added: Identifier (This partially replaces InputRecord)
- Modified: Actions have access to the ActionManager
- Modified: Actions & context actions are now separate, with a common superclass instead
- Modified: Actions inputs are no longer inner classes
- Modified: Abstracted the creation of "modal creator" actions
- Modified: Updated `JDA` to version `5.0.2`
- Modified: Updated `v2` (`youtube-source`) to version `1.5.2`
- Modified: .gitignore now properly includes .properties templates
- Modified: Moved properties to `discord-bot-framework` dependency
- Modified: Fixed changelog typos
- Removed: MultiTypeMap (Replaced by Node)
- Removed: InputRecord (Actions can access ActionManager, no longer needed + replaced by Identifier)
- Removed: Modal creator classes
- Removed: ActionUtil

## v1.7.1
<br/>**NB: Older versions are no longer able to play music**
- Modified: Updated `v2` (`youtube-source`) to version `1.5.1`

## v1.7.0
- Added: Deezer support
- Added: New `.properties` template (v1.7.0 - v1.7.1)
  - Added: Bot tokens can be assigned to `bot.[BOT_NAME]`
  - Added: Can set active bot with using `[BOT_NAME]`
  - Added: Spotify API client ID
  - Added: Spotify API client secret
- Modified: Rewrite on song queuing logic & implementation of external APIs
- Modified: Generic file interfaces moved to `discord-bot-framework` dependency
- Modified: Token files moved into a single .properties file
- Modified: Renamed `commands` package to `actions`

## v1.6.3
- Modified: Changed response text of `--newBotAnnouncement` to give accurate feedback
- Modified: Updated `lavaplayer` to version `2.2.1`
- Modified: Updated `v2` (`youtube-source`) to version `1.4.0`

## v1.6.2
- Added: Context buttons
- Added: Undo context button, attached to the response after queuing a song
- Added: Songs can now be removed from the queue (`/remove`)
- Added: Support for persisting data in files
- Added: Notification system, users can get notified about bot updates & expected downtime (`/notify`)
- Added: Owner commands: The bot owner can execute these by doing `--[command]`
- Added. Bot owner can send bot announcements (`--newBotAnnouncement`)
- Added: Proper logging (finally)
- Modified: Queuing multiple songs now gives more responsive feedback
- Modified: README.md, includes instructions on how to use owner commands

## v1.6.1
<br/>This version was immediately followed by v1.6.2. It is therefore not available for download
- Added: Player message buttons (again)
- Modified: Major rewrite of the music queue logic, to follow MVC design pattern
- Modified: `LavaPlayerJdaWrapper` is now a private static class inside `MusicManager`

## v1.6.0
<br/>This version was immediately followed by v1.6.1. It is therefore not available for download
- Added: `dev.lavalink.youtube:v2` as a dependency. 
- Modified: Commands refactored into actions
- Modified: Actions also buttons & modals, which have been moved here
- Modified: General-purpose bot code moved to separate dependency (`discord-bot-framework`)
- Modified: Changed group id & package name from `org.smileyface` to `no.smileyface`
- Modified: Changed base package name from `org.smileyface` to `no.smileyface.discordbot`
- Modified: Updated the bot's YoutubeAudioSourceManager, as it was moved from `lavaplayer` into a separate dependency (`v2`)
- Modified: Updated from `Java 17` to `Java 21`
- Modified: Updated `lavaplayer` to version `2.2.0`
- Modified: Updated `JDA` to version `5.0.0-beta.24`
- Removed: The Category interface, and any implementing classes
- Removed: The Singleton manager classes
- Removed: Player message buttons. The backend logic will receive an extensive to make these work again

## v1.5.4
- Modified: Updated `lavaplayer` to version `0eaeee195f0315b2617587aa3537fa202df07ddc-SNAPSHOT`.
  This will be changed to a more stable version once a stable version that works is released

## v1.5.3
- Modified: Updated `lavaplayer` to version `2.1.1`.

## v1.5.2
- Modified: Moved from `Walkyst/lavaplayer-fork` to `lavalink-devs/lavaplayer`, since the old fork is seemingly not maintained anymore
- Modified: `lavaplayer` version temporarily moved to a snapshot release, to fix a 400 response code when searching for a song
- Modified: `/credits` command changed to include the new fork instead of the old one

## v1.5.1
- Modified: Updated `JDA` to version `5.0.0-beta.20`. This should hopefully fix some voice playback issues

## v1.5.0
- Added: Shuffle feature (`/shuffle`, `/shf`)
- Added: Repeat fetature (`/repeat`, `/rpt`)
- Added: Queue embed shown together with music player embed
- Added: Music player embed buttons
- Added: Queue embed buttons
- Added: Component manager, to manage action component instances
- Added: Modal manager, to manage modal instances
- Modified: Updated `lavaplayer-fork` to version `1.4.3`. This fixes a crucial bug where the bot was not able to play YouTube or Spotify tracks.
- Modified: Manager classes now have a common super class
- Removed: `/queue`, obsolete
- Removed: `/q` (shortcut for `/queue`)

## v1.4.1
- Modified: Fixed a bug where the music player embed didn't update upon songs finishing
- Modified: Reduced cognitive complexity of play command's "run(...)"-method

## v1.4.0:
- Added: A music player embed, that is constantly updated to avoid chat spam
- Added: `/showplayer`
- Modified: The music player now informs about who did the last command that affected it, and what that command was
- Modified: The skip command can now skip multiple songs
- Modified: The command category class is now abstract
- Modified: All usages of various IDs are now done through "{...}.getIdLong()" for consistency
- Removed: Music player status messages. These were replaced by the music player embed

## v1.3.0
- Added: Feedback commands
- Added: `/knownissues`
- Added: `/reportissue`
- Added: `/credits`
- Added: `/features`

## v1.2.1
- Modified: Fixed a bug where only searched songs, Spotify songs & YouTube videos in playlists were getting queued

## v1.2.0
- Added: Spotify features, can now queue Spotify songs, albums & playlists.
  Note that it doesn't actually play from Spotify, it just finds the songs on YouTube & plays them from there.
  Due to this, the bot might, in very rare cases, queue the wrong song. <b>This is a known issue that will not be fixed</b>
- Modified: Play command can now queue multiple songs with a single command, if the input is a list of links.
  This does not work for search inputs
- Modified: Code structure, commands are stores in their own classes, and categories have their own singleton class
- Modified: README.md, includes instructions on setting up Spotify functionality

## v1.1.0
- Added: YouTube search feature

## v1.0.1
- Added: `README.md`
- Added: `Changelog.md`
- Modified: Upon playing a YouTube video from a playlist, the bot will only play that video, and not the entire playlist
- Modified: The token file name specified inside "activeBot.txt" is no longer case-sensitive

## v1.0.0
- Added: `/ping`
- Added: `/say`
- Added: `/join`
- Added: `/leave`
- Added: `/stop` (shortcut for `/leave`)
- Added: `/play`
- Added: `/p` (shortcut for `/play`)
- Added: `/skip`
- Added: `/queue`
- Added: `/q` (shortcut for `/queue`)