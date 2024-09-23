package pl.football.worldcup.scoreboard;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.football.worldcup.scoreboard.model.FootballMatch;
import pl.football.worldcup.scoreboard.model.MatchScore;
import pl.football.worldcup.scoreboard.storage.InMemoryStorage;
import pl.football.worldcup.scoreboard.storage.MatchStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FootballWorldCupScoreBoardTest {

    private static final String HOME_TEAM = "HomeTeam";
    private static final String AWAY_TEAM = "AwayTeam";

    private MatchStorage matchStorage;
    private ScoreBoard scoreBoard;

    @BeforeEach
    void initBeforeTest() {
        matchStorage = new InMemoryStorage();
        scoreBoard = new FootballWorldCupScoreBoard(new FootballMatchFactory(), matchStorage);
    }

    @Test
    void createMatch_successfully() {
        // WHEN
        Long matchId = scoreBoard.createMatch(HOME_TEAM, AWAY_TEAM);

        // THEN
        List<FootballMatch> summary = scoreBoard.getSummaryMatchesByTotalScore();
        assertNotNull(matchId);
        assertEquals(1, summary.size());

        FootballMatch match = summary.get(0);
        assertEquals(HOME_TEAM, match.homeTeam());
        assertEquals(AWAY_TEAM, match.awayTeam());

        MatchScore matchScore = match.matchScore();
        assertEquals(0, matchScore.homeScore());
        assertEquals(0, matchScore.awayScore());
        assertEquals(0, match.getTotalScore());
    }

    @Test
    void updateMatch_successfully() {
        // GIVEN
        Long matchId = scoreBoard.createMatch(HOME_TEAM, AWAY_TEAM);

        // WHEN
        boolean successfulUpdate = scoreBoard.updateMatch(matchId, new MatchScore(0, 1));

        // THEN
        List<FootballMatch> summaryMatches = scoreBoard.getSummaryMatchesByTotalScore();
        assertTrue(successfulUpdate);
        assertEquals(1, summaryMatches.size());
        FootballMatch match = summaryMatches.get(0);
        assertEquals(HOME_TEAM, match.homeTeam());
        assertEquals(AWAY_TEAM, match.awayTeam());

        MatchScore matchScore = match.matchScore();
        assertEquals(0, matchScore.homeScore());
        assertEquals(1, matchScore.awayScore());
        assertEquals(1, match.getTotalScore());
    }

    @Test
    void finishMatch_successfully() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.of(2024, 9, 1, 1, 1, 1, 0);
        Long matchId = scoreBoard.createMatch(HOME_TEAM, AWAY_TEAM, dateTime);

        // WHEN
        scoreBoard.finishMatch(matchId);

        // THEN
        List<FootballMatch> summaryMatchesByTotalScore = scoreBoard.getSummaryMatchesByTotalScore();
        assertEquals(0, summaryMatchesByTotalScore.size());

        List<FootballMatch> allMatches = matchStorage.getAllMatches();
        assertEquals(1, allMatches.size());

        FootballMatch match = allMatches.get(0);
        assertEquals(Boolean.TRUE, match.fetchEndTime().isPresent());
        assertEquals(HOME_TEAM, match.homeTeam());
        assertEquals(AWAY_TEAM, match.awayTeam());
    }

    @Test
    void getSummaryMatches_oneMatch() {
        // GIVEN
        scoreBoard.createMatch(HOME_TEAM, AWAY_TEAM);

        // WHEN
        List<FootballMatch> summaryMatchesByTotalScore = scoreBoard.getSummaryMatchesByTotalScore();

        // THEN
        assertEquals(1, summaryMatchesByTotalScore.size());
        FootballMatch match = summaryMatchesByTotalScore.get(0);
        assertEquals(HOME_TEAM, match.homeTeam());
        assertEquals(AWAY_TEAM, match.awayTeam());
        MatchScore matchScore = match.matchScore();
        assertEquals(0, matchScore.homeScore());
        assertEquals(0, matchScore.awayScore());
        assertEquals(0, match.getTotalScore());
    }

    @Test
    void getSummaryMatches_noMatch() {
        // WHEN
        List<FootballMatch> summaryMatchesByTotalScore = scoreBoard.getSummaryMatchesByTotalScore();

        // THEN
        assertEquals(0, summaryMatchesByTotalScore.size());
    }

    @Test
    void getSummaryMatches_gameWithMultipleMatches() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.of(2024, 9, 1, 1, 1, 1, 0);
        List<Long> matchIdList = new ArrayList<>();
        matchIdList.add(createMatchWithScore("Mexico", "Canada", startTime.plusSeconds(1), 0, 5));
        matchIdList.add(createMatchWithScore("Spain", "Brazil", startTime.plusSeconds(2), 10, 2));
        matchIdList.add(createMatchWithScore("Germany", "France", startTime.plusSeconds(3), 2, 2));
        matchIdList.add(createMatchWithScore("Uruguay", "Italy", startTime.plusSeconds(4), 6, 6));
        matchIdList.add(createMatchWithScore("Argentina", "Australia", startTime.plusSeconds(5), 3, 1));

        System.out.println("\nCurrent data in the system:\n====================================");
        AtomicInteger currentDataIndex = new AtomicInteger(0);
        matchIdList.forEach(id -> {
            FootballMatch match = matchStorage.getMatch(id);
            System.out.printf("%s. %-10s - %-10s %3d - %-3d%n", (char) ('a' + currentDataIndex.getAndIncrement()), match.homeTeam(), match.awayTeam(),
                    match.matchScore().homeScore(), match.matchScore().awayScore());
        });

        // WHEN
        List<FootballMatch> summaryMatchesByTotalScore = scoreBoard.getSummaryMatchesByTotalScore();

        System.out.println("\nSummary of games by total score:\n====================================");
        AtomicInteger summaryIndex = new AtomicInteger(1);
        summaryMatchesByTotalScore
                .forEach(match -> System.out.printf("%d. %-10s - %-10s %3d - %-3d%n", summaryIndex.getAndIncrement(), match.homeTeam(),
                        match.awayTeam(), match.matchScore().homeScore(), match.matchScore().awayScore()));

        // THEN
        assertNotNull(summaryMatchesByTotalScore);
        assertEquals(5, matchIdList.size());
        assertEquals(5, summaryMatchesByTotalScore.size());

        // 1
        FootballMatch match = summaryMatchesByTotalScore.get(0);
        assertEquals("Uruguay", match.homeTeam());
        assertEquals("Italy", match.awayTeam());
        assertEquals(12, match.getTotalScore());

        // 2
        match = summaryMatchesByTotalScore.get(1);
        assertEquals("Spain", match.homeTeam());
        assertEquals("Brazil", match.awayTeam());
        assertEquals(12, match.getTotalScore());

        // 3
        match = summaryMatchesByTotalScore.get(2);
        assertEquals("Mexico", match.homeTeam());
        assertEquals("Canada", match.awayTeam());
        assertEquals(5, match.getTotalScore());

        // 4
        match = summaryMatchesByTotalScore.get(3);
        assertEquals("Argentina", match.homeTeam());
        assertEquals("Australia", match.awayTeam());
        assertEquals(4, match.getTotalScore());

        // 5
        match = summaryMatchesByTotalScore.get(4);
        assertEquals("Germany", match.homeTeam());
        assertEquals("France", match.awayTeam());
        assertEquals(4, match.getTotalScore());
    }

    private Long createMatchWithScore(String homeTeam, String awayTeam, LocalDateTime startTime, Integer homeScore, Integer awayScore) {
        Long matchId = scoreBoard.createMatch(homeTeam, awayTeam, startTime);
        if (scoreBoard.updateMatch(matchId, new MatchScore(homeScore, awayScore))) {
            return matchId;
        } else {
            throw new RuntimeException(MessageFormat.format("Match for ({0},{1}) cannot be updated", homeTeam, awayTeam));
        }
    }
}