# Block Dude (Java)

## About

This project was inspired by the popular TI-84 game _Block Dude_ by Brandon Sterner. This project is not meant to replicate _Block Dude_; its purpose is simply for my own practice with the MVC design pattern. You can find an exact replica of _Block Dude_, created by Andrew Zich, [here](http://azich.org/blockdude/).

I created this project in April 2019, and I have not updated it since then (despite creating a git repo for it in October 2019). As of right now, the only way to play the game is through the console of your IDE. I may come back to this project at some point and add a more user-friendly view.

## Bugs

There are two known bugs in the latest version of the game:
1. If the player turns to the left at the start of a level, the view will not render that the player has turned if there is a block immediately to the left of the player. Note that the model knows that the player has turned, but for some reason this is not rendered by the view until the player makes another valid move. This bug can easily be observed at the start of level 2.
2. If the player picks up the block to their left after encountering bug #1, the view will remove the block from where it previously was, but will not render it above the player. Note that the model knows that the player has picked up the block, but for some reason this is not rendered by the view until the player makes another valid move. This bug can easily be observed at the start of level 2.
