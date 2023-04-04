Changelog:

v1.2.1
- Modified: Fixed a bug where only searched songs, Spotify songs & YouTube videos in playlists were getting queued

v1.2.0
 - Added: Spotify features, can now queue Spotify songs, albums & playlists. 
   Note that it doesn't actually play from Spotify, it just finds the songs on YouTube & plays them from there.
   Due to this, the bot might, in very rare cases, queue the wrong song. <b>This is a known issue that will not be fixed</b>
 - Modified: Play command can now queue multiple songs with a single command, if the input is a list of links.
   This does not work for search inputs
 - Modified: Code structure, commands are stores in their own classes, and categories have their own singleton class
 - Modified: README.md, includes instructions on setting up Spotify functionality

v1.1.0
- Added: YouTube search feature

v1.0.1:
- Added: README.md
- Added: Changelog.md
- Modified: Upon playing a YouTube video from a playlist, the bot will only play that video, and not the entire playlist
- Modified: The token file name specified inside "activeBot.txt" is no longer case sensitive

v1.0.0:
- Added: /ping
- Added: /say
- Added: /join
- Added: /leave
- Added: /stop (shortcut for /leave)
- Added: /play
- Added: /p (shortcut for /play)
- Added: /skip
- Added: /queue
- Added: /q (shortcut for /queue)