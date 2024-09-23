package pl.football.worldcup.scoreboard;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import pl.football.worldcup.scoreboard.model.FootballMatch;
import pl.football.worldcup.scoreboard.model.MatchScore;
import pl.football.worldcup.scoreboard.storage.MatchStorage;

public class FootballWorldCupScoreBoard implements ScoreBoard {

    private final MatchFactory matchFactory;
    private final MatchStorage matchStorage;

    public FootballWorldCupScoreBoard(MatchStorage storage) {
        this(new FootballMatchFactory(), storage);
    }

    public FootballWorldCupScoreBoard(MatchFactory matchFactory, MatchStorage storage) {
        this.matchFactory = matchFactory;
        this.matchStorage = storage;
    }

    @Override
    public Long createMatch(String homeTeam, String awayTeam) {
        return createMatch(homeTeam, awayTeam, LocalDateTime.now());
    }

    @Override
    public Long createMatch(String homeTeam, String awayTeam, LocalDateTime startTime) {
        FootballMatch match = matchFactory.createMatch(homeTeam, awayTeam, startTime);
        match = matchStorage.saveMatch(match);

        return match.id();
    }

    @Override
    public boolean updateMatch(Long id, MatchScore matchScore) {
        try {
            FootballMatch match = matchStorage.getMatch(id);
            match = matchFactory.updateMatchScore(match, matchScore);
            matchStorage.updateMatch(match);
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public void finishMatch(Long id) {
        FootballMatch match = matchStorage.getMatch(id);
        match = matchFactory.finishMatch(match, LocalDateTime.now());
        matchStorage.updateMatch(match);
    }

    @Override
    public List<FootballMatch> getSummaryMatchesByTotalScore() {
        List<FootballMatch> allInProgressMatches = matchStorage.getAllMatchesInProgress();
        allInProgressMatches
                .sort(Comparator.comparingInt(FootballMatch::getTotalScore)
                        .thenComparing(FootballMatch::startTime)
                        .reversed());

        return allInProgressMatches;
    }
}
