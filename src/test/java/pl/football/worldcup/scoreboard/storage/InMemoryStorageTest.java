package pl.football.worldcup.scoreboard.storage;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.football.worldcup.scoreboard.FootballMatchFactory;
import pl.football.worldcup.scoreboard.MatchFactory;
import pl.football.worldcup.scoreboard.exception.MatchStorageException;
import pl.football.worldcup.scoreboard.model.FootballMatch;
import pl.football.worldcup.scoreboard.model.MatchScore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryStorageTest {

    private static final String HOME_TEAM = "HomeTeam";
    private static final String AWAY_TEAM = "AwayTeam";

    private MatchFactory footballMatchFactory;
    private Map<Long, FootballMatch> storage;
    private MatchStorage matchStorage;

    @BeforeEach
    void resetState() {
        footballMatchFactory = new FootballMatchFactory();
        storage = new HashMap<>();
        matchStorage = new InMemoryStorage(storage);
    }


    @Test
    void saveMatch_successfully() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = footballMatchFactory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);

        // WHEN
        match = matchStorage.saveMatch(match);

        // THEN
        assertEquals(1, storage.size());
        FootballMatch storedMatch = storage.get(match.id());
        assertEquals(HOME_TEAM, storedMatch.homeTeam());
        assertEquals(AWAY_TEAM, storedMatch.awayTeam());
    }

    @Test
    void saveMatch_alreadyExist() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = footballMatchFactory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);
        final FootballMatch savedMatch = matchStorage.saveMatch(match);

        // WHEN
        MatchStorageException exception = assertThrows(MatchStorageException.class, () -> matchStorage.saveMatch(savedMatch));

        // THEN
        assertEquals("Match object already exist in storage", exception.getMessage());
    }

    @Test
    void updateMatch_successfully() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = footballMatchFactory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);
        match = matchStorage.saveMatch(match);
        match = match.toBuilder()
                .matchScore(new MatchScore(match.matchScore().homeScore(), 1))
                .build();

        // WHEN
        match = matchStorage.updateMatch(match);

        // THEN
        assertEquals(1, storage.size());
        FootballMatch storedMatch = storage.get(match.id());
        assertEquals(HOME_TEAM, storedMatch.homeTeam());
        assertEquals(AWAY_TEAM, storedMatch.awayTeam());
        assertEquals(1, storedMatch.matchScore().awayScore());
    }

    @Test
    void updateMatch_notExist() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = footballMatchFactory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);

        // WHEN
        MatchStorageException exception = assertThrows(MatchStorageException.class, () -> matchStorage.updateMatch(match));
        // THEN
        assertEquals("There is no match object in storage", exception.getMessage());
    }

    @Test
    void updateMatch_exceptionWhileWrongId() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = footballMatchFactory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);
        final FootballMatch mathWithWrongId = match.toBuilder()
                .id(1000L)
                .build();

        // WHEN
        MatchStorageException exception = assertThrows(MatchStorageException.class, () -> matchStorage.updateMatch(mathWithWrongId));

        // THEN
        assertEquals("There is no match object in storage", exception.getMessage());
    }

    @Test
    void getAllMatches_oneMatch() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = footballMatchFactory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);
        match = matchStorage.saveMatch(match);

        // WHEN
        List<FootballMatch> allMatches = matchStorage.getAllMatches();

        // THEN
        assertEquals(1, storage.size());
        assertEquals(1, allMatches.size());
        FootballMatch storedMatch = storage.get(match.id());
        assertEquals(HOME_TEAM, storedMatch.homeTeam());
        assertEquals(AWAY_TEAM, storedMatch.awayTeam());
    }

    @Test
    void getAllInProgressMatches_oneMatch() {
        // GIVEN
        LocalDateTime startTime = LocalDateTime.now();
        FootballMatch match = footballMatchFactory.createMatch(HOME_TEAM, AWAY_TEAM, startTime);
        match = matchStorage.saveMatch(match);

        // WHEN
        List<FootballMatch> allMatchesInProgress = matchStorage.getAllMatchesInProgress();

        // THEN
        assertEquals(1, storage.size());
        assertEquals(1, allMatchesInProgress.size());
        FootballMatch storedMatch = storage.get(match.id());
        assertEquals(HOME_TEAM, storedMatch.homeTeam());
        assertEquals(AWAY_TEAM, storedMatch.awayTeam());
    }

    @Test
    void getAllInProgressMatches_noMatch() {
        // WHEN
        List<FootballMatch> allMatchesInProgress = matchStorage.getAllMatchesInProgress();

        // THEN
        assertEquals(0, storage.size());
        assertEquals(0, allMatchesInProgress.size());
    }
}