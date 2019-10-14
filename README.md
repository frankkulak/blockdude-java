# Block Dude (Java)

## About

This project was inspired by the popular TI-84 game _Block Dude_ by Brandon Sterner. This project is not meant to replicate _Block Dude_; its purpose is just for my own practice with the MVC design pattern. You can find an exact replica of _Block Dude_, created by Andrew Zich, [here](http://azich.org/blockdude/).

I initially created this project in April 2019, but made the git repo and touched up the code dramatically in October 2019.

## Playing the game

To play the game, run the main method in BlockDude.java with the configurations `-source levels.txt -view text`.

The argument given for the `-source` prompt specifies the name of the file containing level data. The file containing the data for the levels from the original _Block Dude_ game is named `levels.txt`, and is stored in the `levelSources` folder.

Right now, `text` is the only supported argument for the `-view` prompt, and it will run the game in the console of your IDE (directions for how to play will be printed as soon as the game begins). I plan to come back to this project at some point and add a more user-friendly view, more closely mimicking the original game.

## Bugs

There are no bugs that I am currently aware of.