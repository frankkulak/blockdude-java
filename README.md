# Block Dude (Java)

This project was inspired by the TI-84 game _Block Dude_ (created by Brandon Sterner). This project is not meant to be an exact replica of _Block Dude_ - its purpose is just for my own practice with the MVC design pattern. You can find an exact replica of _Block Dude_, created by Andrew Zich, [here](http://azich.org/blockdude/).

I created this project in April 2019, but made the git repo and touched up the code in October 2019.

## Playing the game

To play the game, run the main method in BlockDude.java with the configurations `-source levels.txt -view text`.

The argument given for the `-source` prompt specifies the name of the file to read level data from. The file named `levels.txt` (located in the levelSources folder) contains the level data from the original _Block Dude_ game.

Right now, `text` is the only supported argument for the `-view` prompt, and it runs the game in the console of your IDE. I plan to come back to this project at some point and add a more user-friendly view using JavaFX, more closely mimicking the original game.

## Bugs

There are no bugs that I am currently aware of. Please report any that you come across.
