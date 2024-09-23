package pl.football.worldcup.scoreboard.util;

import java.time.LocalDateTime;

import lombok.experimental.UtilityClass;
import pl.football.worldcup.scoreboard.model.MatchScore;

@UtilityClass
public class MatchValidator {

    private static final String TEAM_NAME_PATTERN = "^\\w{3,}$";

    public boolean isIncorrectName(String name) {
        return name == null || !name.matches(TEAM_NAME_PATTERN);
    }

    public boolean isEndAfterStartTime(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime != null && endTime != null && endTime.isAfter(startTime);
    }

    public boolean isCorrectMatchScores(MatchScore matchScores) {
        return matchScores != null && matchScores.homeScore() >= 0 && matchScores.awayScore() >= 0;
    }
}
