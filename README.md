# About

1. This application is the Client side of the server board Game.

# Design

1. This application is java comandline base 

2. When the application start the Player is prompted for his user name, if accepted he is added then wait for the second player.

3. The second player is a replica code of the first player, meaning duplicate this code and run the same commands in the folders to have 2 players.

4. When 2 players have joined the game will display a message as to whose turn it is.

5. The game will end when we have a winer or a draw. 

# Technology

1. Java 11

2. Client side: exec maven plugin, Jackson data binding, Http client apache Api, Stack data structure and List for the board game

# Test

1. The Client is supplied as support for this backend project.

# Start game clients

1. you need 2 clients to run against the server

2. Run => "mvn clean install"

3. Run => "mvn exec:java"
