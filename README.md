# Football World Cup Score Board

The boards support the following operations:
1. **Start a game**. When a game starts, it should capture (being initial score 0-0)
  a. Home team\
  b. Away Team
2. **Finish a game**. It will remove a match from the scoreboard.
3. **Update score**. Receiving the pair score; home team score and away team score updates a game score.
4. **Get a summary of games by total score**. Those games with the same total score will be returned ordered by the most recently added to our system.

## As an example, being the current data in the system:
&nbsp;&nbsp;&nbsp;&nbsp;a. Mexico - Canada: 0 – 5\
&nbsp;&nbsp;&nbsp;&nbsp;b. Spain - Brazil: 10 – 2\
&nbsp;&nbsp;&nbsp;&nbsp;c. Germany - France: 2 – 2\
&nbsp;&nbsp;&nbsp;&nbsp;d. Uruguay - Italy: 6 – 6\
&nbsp;&nbsp;&nbsp;&nbsp;e. Argentina - Australia: 3 - 1

## The summary would provide with the following information:
1. Uruguay 6 - Italy 6
2. Spain 10 - Brazil 2
3. Mexico 0 - Canada 5
4. Argentina 3 - Australia 1
5. Germany 2 - France 2
