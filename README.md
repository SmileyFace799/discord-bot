<h1>My discord bot :D</h1>
<p>This repository is the source code for my discord bot.

The code is designed to be easily runnable on any bot account that has a stored token file.
A token file is a plain text file with a bot token inside it.</p>

<h2>Setup Guide:</h2>
<h3>Setting up the bot:</h3>
<p>
    These are the basic steps for setting up a working bot, and are also the only required steps for a working bot.<br/>
    <b>Requires:</b> Bot account token
</p>
<ol>
    <li>Download a <a href="https://github.com/SmileyFace799/discord-bot/releases">release</a> of the bot</li>
    <li>Put the downloaded .jar / source code inside a designated bot folder</li>
    <li>Create a folder named "tokens" (case-sensitive)</li>
    <li>Inside the newly created folder, create a text file named "{...}.token". Make sure the name is in all lowercase</li>
    <li>Open the newly created token file, and paste the bot's token inside it.</li>
    <li>Go back to the designated bot folder, and create a text file named "activeBot.txt" (case-sensitive)</li>
    <li>Open the newly created text file, and paste the name of the token file that should be used when starting the bot,
    without the ".token" file extension (case-insensitive)</li>
    <li>If done correctly, the bot is now runnable. Upon running, it should print "{BOT_NAME} is ready" in the console</li>
</ol>
<p><b>Note (v1.0.0):</b> The token file name in "activeBot.txt" is case-sensitive.</p>

<h3>Enabling Spotify features (v1.2.0+):</h3>
<p>
    The bot also has certain Spotify features, allowing the bot to "play" songs, playlists & albums from spotify.
    The bot doesn't actually play songs through Spotify (mainly because it can't), but it can get the song info from a link,
    and then play the song on another platform, such as YouTube. This however, requires access to the Spotify API,
    meaning a Spotify client ID & secret must be provided for these features.<br/>
    <b>Requires:</b>Spotify client ID, Spotify client secret
</p>
<ol>
    <li>Inside the "tokens"-folder, create a new file named "spotifyClient.sptoken" (case-sensitive)</li>
    <li>
        Inside the newly created Spotify token file, paste the following information:
        <ul>
            <li>Line 1: The Spotify client ID</li>
            <li>Line 2: The Spotify client secret</li>
        </ul>
    </li>
    <li>If done correctly, the bot should now be runnable with Spotify features enabled</li>
</ol>

<h2>Owner commands (v1.6.2+):</h2>
<p>
    As the bot owner, you have access to certain special "owner commands".
    These all use the `--[command]` syntax, and they are case-sensitive.
</p>
<ul>
    <li>
        <p>
            `--newBotAnnouncement`: Reply to a message in your bot's private message channel with this command,
            and it will broadcast that message in every user who are subscribed to bot notifications
        </p>
        <p>
            Example usage (in private message channel with the bot):<br/>
            <img src="readmeImgs/newBotAnnouncement.png" alt="image showing how to use the newBotAnnouncement owner command">
        </p>
    </li>
</ul>