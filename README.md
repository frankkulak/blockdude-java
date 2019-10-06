# Block Dude (Java)

## About

This project was inspired by the popular TI-84 game _Block Dude_ by Brandon Sterner. This project is not meant to replicate _Block Dude_; its purpose is simply for my own practice with the MVC design pattern. You can find an exact replica of _Block Dude_, created by Andrew Zich, [here](http://azich.org/blockdude/).

I created and worked on this project in April 2019, but created the git repo and made some changes to it in October 2019. As of right now, the only way to play the game is through the console of your IDE. I may come back to this project at some point and add a more user-friendly view.

## Bugs

There are two known bugs in the latest version of this project:
1. If the player is adjacent to, but not facing, a solid game piece (either a block or a wall), the view will not render when the player turns to face this game piece, even though it is stored in the model correctly. Making another valid move will render the board correctly. This bug can easily be observed at the start of level 2 (pass: ARo).
2. If the player picks up the block after encountering bug #1, the view will remove the block from where it previously was, but will not render it above the player. Note that the model knows that the player has picked up the block, but for some reason this is not rendered by the view until the player makes another valid move. This bug can easily be observed at the start of level 2 (pass: ARo).
