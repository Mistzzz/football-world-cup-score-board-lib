package pl.football.worldcup.scoreboard;

import java.time.LocalDateTime;
import java.util.List;

import pl.football.worldcup.scoreboard.model.FootballMatch;
import pl.football.worldcup.scoreboard.model.MatchScore;

public interface ScoreBoard {

    Long createMatch(String homeTeam, String awayTeam);

    Long createMatch(String homeTeam, String awayTeam, LocalDateTime startTime);

    boolean updateMatch(Long id, MatchScore matchScore);

    void finishMatch(Long id);

    List<FootballMatch> getSummaryMatchesByTotalScore();
}
