# intotheheavens

This is a game project I started building with Bishop, a friend of mine.  The goal was to create a text adventure game engine which allowed players to play different campaigns and build their own.

The engine is a work in progress and some features are not implemented.  I do not plan to finish this project, so feel free to fork it if you wish.

What Works
----------
* Interpreter and compilier for a custom scripting language.
* Save game system, which persists the state of any custom scripts and restores it on load game.
* Simple UI for interacting with the game.
* Ability to play .wav and .ogg files from a script.
* In game text command system.  Available commands are driven by Command handlers in the scripts.

What Doesn't Work
-----------------
* Story Manager.  The plan was to have this be the place to load stories created by other people.
