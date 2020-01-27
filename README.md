# Block Dude (Java)

This project was inspired by the TI-84 game _Block Dude_ (created by Brandon Sterner), but is not meant to be an exact replica - its purpose is for my own practice with the MVC design pattern. You can find an exact replica of _Block Dude_, created by Andrew Zich, [here](http://azich.org/blockdude/).

I started this project in April 2019, but created this repository and touched up the code in October 2019. I plan to come back to this project to add more unit tests and a user-friendly view using Java Swing.

## Playing the game

To run this project in your console with the levels from the original _Block Dude_ game, use `-source levels.txt -view text` for the run configurations.

> **Run configurations**
>
> `-source`: Specifies the name of the file from which to read level data. The name must include the file's extension, and this file must be located in the levelSources directory. There is an existing file named `levels.txt` that contains data for the levels from the original _Block Dude_ game.
>
> `-view`: Specifies the type of view to use when running the game. As of right now, the only supported view is a text view that runs the game in the console of your IDE; this view can be speicifed using the value `text`.

## Bugs

There are no bugs that I am currently aware of. Please report any that you come across.
