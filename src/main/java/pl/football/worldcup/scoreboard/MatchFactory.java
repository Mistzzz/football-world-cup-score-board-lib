package pl.football.worldcup.scoreboard;

import java.time.LocalDateTime;

import pl.football.worldcup.scoreboard.model.FootballMatch;
import pl.football.worldcup.scoreboard.model.MatchScore;

public interface MatchFactory {

    FootballMatch createMatch(String homeTeam, String awayTeam, LocalDateTime startTime);

    FootballMatch updateMatchScore(FootballMatch match, MatchScore matchScore);

    FootballMatch finishMatch(FootballMatch match, LocalDateTime endTime);
}
