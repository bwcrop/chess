# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZgFUAzpQrVCKOn0aCYAcWABbFBKpSZDAaUQo86zb21MYoygBEAggHkt-U1YCuYABbW7xh-KWqPmDmAwYAAjYDEYDhDMTHQwKABPEVwiNABzGAAGADoATkw0qAgnbABiABYAZgAOACZckBhS5DTXMBCEJxQYACUUNKQReKCkCDRMRFRSAFoAPnJKGigALhgAbQAFGzIAFQBdGAB6JzEoAB00AG8AIhPKNBUUa+XrmGuAGjfcEREAd2gOM9Xh83ihlMAkAggW8AL6YdSUGBzVjsLiUVY3O5QB6qaEg67fP4AvGfa5giFQl6wzBsTjcWDI8xQQwgFCrKD9QZgSgACj6AyGlD6AEcukMAJSYJks7qM04eVZpFBgJk8rE4lCSpkeJHzKLLABiSE4ZnlQWAMBCCRg6semBCHOAAGtTYjfkg3DbThqYMAEI6ONaUAAPLkiKXy2xIuXiRZSVba80R2OSVnR+YIlYwKz+-SBnbO9AAUWDrOwBDG8MWiOmyMmcVW5UylQuN1UP2ASuebyLUCKWdtql9ueAgZgwSd6GucPQHGTUB1tfm0rjrNWaCcCAQ85lutdC9syxAju5qqxFnNWsjNj3+qsHA4+4vwR3q9l82cbgVx-03M-rh5YAXHcS9MH-Rc9RCZZ70fcCkxXVNZWRWk0SzfkuSFFARE3LAUPpdMFmodEYExb1HleVZgVJIC3B2CBJzQCi3mpTMCPrDBVlqTJMlbW4yNxGBKLeajgLohimOBGdOBiQpijKaB2CVJoDTgIsLDgKwYAAGQgAZxnY5hkUzVZNm2fYjjEH5RjQXjByeKl8UJf4oEBBy4VY5DUXpDE+PuciHNJJziTcmkvJrZdThlVYEF040eR0vSRTFMArxTDQ0xjA8bEVZUz34zV5wgyIoKNE1E2CS1rTs+1HRdJkYDQCBmAAM2KaTtSjJd9yihrN23BD0qQjNqyzIQHmA6AkAALxQDgSzLCtxg8utkAbGAmwARl49sRE7eyez7aBVicca3EmmbASkucOpvLqBvjJ8kwdfQXQ2cIiRcyIICwtAAHJmHBMAQFcV9EII4yzFO1xztm+aUHLayqyIhkVqmMBG0yLarmuHa9u7a5e37Y6oZhy7YnayK3z3cDDx-IIUH-QDgOfYBJRp27IOgh9s2Z+DKbBrq8OIhLjT6bCEFwsKUeG5GfLsiThLeGjXDEqdBOY6ckaWNjVo4mAuJ47H5fVqildE+i1aEzXZxkooSlKQoUHQJoWjaR3nYS4osAMvcIbWCwiy0osdiLA5DkskRrIuZXVaWka9yFrMY4t8ZE73e61xgGK0i9pnaJT1LmSp5F2ZysBGeThi2eZzrOdK2DeYqq1fXNhiapenn84YhqmpgVqTrndmCIztleq3UHBvBkaExJqBpth0t4cWrWa1RtbNu2rDdq7SjCaOm1Z-nsmbaHu7+cG1Y4JfEfqZr7KOBQbg-2AvOVYLsC79vKC+mUCAaE7kC19z5SAIonVY2EQCsh+KFOkq8ZZLAxO5eOXUDKcW4q2K6ts5KlA5I+Uo2BjQui0lyRQjxww+yMtPdYChg5hzSI8aOrd0ArwZPMMBLcu7MLTsiXwag3zRS5LwkQr9Y6Sl4buEud8y4VyYWgauX5a7FUNMaBuX5zSVQ4W-Nuz1nQANjj3FqbVB6fy6uI-hY9txmIFpQ2WkNlakzhgjSsy15ioPWpjTeHYd4HSJgfexc8LrW2kqfHhjwepX2AJgKxk9Ql+EPMgIYQieRiMeEVfUChlTDgQKQvwSZeGLliXwxCqw4AQC3CgcA1kAA8+TzSzCiWE4unlYFZmIYkshYscIwNQlPWxNx6Htm7GsbIIy9hIORjrNGaDDb9LIUM2ZqgACSFhuwbVqJUcopJfiuA9CgJkGoSRvA6KAJ0+z-Km2uAMlAAA5c5sIxnkznAUO2ZQOAAHZciZBQJkJoRZKhwFUgANngCebovCYATF1oZeBxFTK7DoQwqGsdeJXNubiEKHk2FS1WJXdAKLHhovspJbp+FCk9TptyXhIiU6fDgKC3hSUsIpQaaoCRH4pEwCVOXF+uK5EfwURzJR9cAEsw0by9uuj-z6MaoYge-L3CdTJeYjc49okgK6hDMa-ij6OOXi4+AUKMZYzbFvPGu9DoDkPoEzBIT5hqszhEllRSYl2tSbTeljweSoseCkuJgq7zc1qS+fJirXWsvMVc5ZTqJHNNQiUj1qhOkSxJXAwiCCSKXMeMs1Z6zyjjO1igw1+t0HY0jSs1YayNmYOedghIj8Yq-BdkgVoYA61bggI2gAUhAY0OTuilGOSAJ0kK0a+yoZsIQ5lDhXMYZwmy2NsAIGAHWqApSYpQCBAAdRYIskOhwABCWkrBwAANL4jLTmqtLCE7Ys0cihdS6V1rugFunde7D3HrPaSC9Fbc1wm4WG51D0ABWPa0BUt5Z8btxoGUoFFEy31QGMrsoFdInlsj5EKv9SVFRIr1HN3FTol0UqU4GL7kY+VBTAM9RVZYxp1iYWjStQvBaiN9VuI3tjXG3iCYWuJtq61jzKOhr7eExukT7Xvj7QqTlyoqVlsQ2knDJog0WmbmWiVLpwUyvI3KkNt0lXFIsdGppjHVj7tHIyoYuq2PILXnrJsMycamp43vLMYRHwcng0MIJc59MEUk6sVTmnehweSrNGAIAynQGHAGa0rhwi+hgIu5dtBJO9PTTmOLBYGI2ecXZ1xRamwti4y5-avHfF+ji+OQsjFMHpcKQqEogRKWequSzRTij9RCGwC1sFqT1Huk9HZX0IhkuPsoM+qAJmGMohaeApwkCt4pulmm4ilx82prcQbDBQma320KMuptLbDvWj0HgX02BF2EBGGMCFFCzPrADkHEOYcojXtjd5SL3ALteeSitvc52i5GZAD9qAyTIX6GBy6p8tMwcQ5ul-ZYP8-7dD9Nk8qkSgdsuk3DqHEP-PIn1Cj-+6PpNJmx8XFDWGjzw8w0p5HYJUdZLwy+Sns32EQKgeGbhNj00bY+-Z9GxaZmYKAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
