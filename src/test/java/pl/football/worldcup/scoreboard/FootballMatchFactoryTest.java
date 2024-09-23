package pl.football.worldcup.scoreboard;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pl.football.worldcup.scoreboard.exception.FootballMatchException;
import pl.football.worldcup.scoreboard.model.FootballMatch;
import pl.football.worldcup.scoreboard.model.MatchScore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FootballMatchFactoryTest {

    private static final String HOME_TEAM = "HomeTeam";
    private static final String AWAY_TEAM = "AwayTeam";

    private MatchFactory factory;

    @BeforeEach
    void resetState() {
        factory = new FootballMatchFactory();
    }

    @Test
    void createMatch_successfully() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();

        // WHEN
        FootballMatch match = factory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);

        // THEN
        assertNotNull(match);
        assertEquals(HOME_TEAM, match.homeTeam());
        assertEquals(AWAY_TEAM, match.awayTeam());
        assertEquals(startTime, match.startTime());
    }

    @Test
    void createMatch_toShortNames() {
        // GIVEN
        String homeTeam = "Ab";
        String awayTeam = "Cd";
        LocalDateTime startTime = LocalDateTime.now();

        // WHEN
        FootballMatchException exception = assertThrows(FootballMatchException.class, () -> factory.createMatch(homeTeam, awayTeam, startTime));

        // THEN
        assertEquals("Given team names (Ab:Cd) are incorrect", exception.getMessage());
    }

    @Test
    void createMatch_toShortName() {
        // GIVEN
        String homeTeam = "Ab";
        String awayTeam = "Cdef";
        LocalDateTime startTime = LocalDateTime.now();

        // WHEN
        FootballMatchException exception = assertThrows(FootballMatchException.class, () -> factory.createMatch(homeTeam, awayTeam, startTime));

        // THEN
        assertEquals("Given team name (Ab) is incorrect", exception.getMessage());
    }

    @Test
    void updateMatch_successfully() {
        // GIVEN
        int score = 1;
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = factory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);

        // WHEN
        FootballMatch footballMatch = factory.updateMatchScore(match, new MatchScore(score, score));

        // THEN
        assertNotNull(footballMatch);
        assertEquals(HOME_TEAM, footballMatch.homeTeam());
        assertEquals(AWAY_TEAM, footballMatch.awayTeam());
        assertEquals(startTime, footballMatch.startTime());
        assertEquals(score, footballMatch.matchScore().homeScore());
        assertEquals(score, footballMatch.matchScore().awayScore());
    }

    @ParameterizedTest
    @MethodSource("matchScore")
    void updateMatch_scoreIncorrect(MatchScore matchScore) {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = factory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);

        // WHEN
        FootballMatchException exception = assertThrows(FootballMatchException.class, () -> factory.updateMatchScore(match, matchScore));

        // THEN
        assertEquals("Match score is incorrect", exception.getMessage());
    }

    @Test
    void finishMatch_successfully() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = factory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);
        LocalDateTime endTime = startTime.plusSeconds(1);

        // WHEN
        match = factory.finishMatch(match, endTime);

        // THEN
        assertNotNull(match);
        assertEquals(HOME_TEAM, match.homeTeam());
        assertEquals(AWAY_TEAM, match.awayTeam());
        assertEquals(startTime, match.startTime());
        assertEquals(endTime, match.endTime());
    }

    @Test
    void finishMatch_endBeforeStartTime() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = factory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);
        LocalDateTime endTime = startTime.minusSeconds(1);

        // WHEN
        FootballMatchException exception = assertThrows(FootballMatchException.class, () -> factory.finishMatch(match, endTime));

        // THEN
        assertEquals("Match end time value is before start time", exception.getMessage());
    }

    @Test
    void finishMatch_alreadyFinished() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = factory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);
        LocalDateTime endTime = startTime.plusSeconds(1);
        final FootballMatch savedMatch = factory.finishMatch(match, endTime);

        // WHEN
        FootballMatchException exception = assertThrows(FootballMatchException.class, () -> factory.finishMatch(savedMatch, endTime));

        // THEN
        assertEquals("Match has been already finished", exception.getMessage());
    }

    private static List<MatchScore> matchScore() {
        return Arrays.asList(
                null,
                new MatchScore(-1, 0),
                new MatchScore(0, -1)
        );
    }
}