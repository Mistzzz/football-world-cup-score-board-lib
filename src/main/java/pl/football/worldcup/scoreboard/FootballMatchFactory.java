package pl.football.worldcup.scoreboard;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import pl.football.worldcup.scoreboard.exception.FootballMatchException;
import pl.football.worldcup.scoreboard.model.FootballMatch;
import pl.football.worldcup.scoreboard.model.MatchScore;
import pl.football.worldcup.scoreboard.util.MatchValidator;

public class FootballMatchFactory implements MatchFactory {

    @Override
    public FootballMatch createMatch(String homeTeam, String awayTeam, LocalDateTime startTime) {
        validateTeamNames(homeTeam, awayTeam);

        return FootballMatch.builder()
                .id(0L)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .startTime(startTime)
                .matchScore(initMatchScore())
                .build();
    }

    @Override
    public FootballMatch updateMatchScore(FootballMatch match, MatchScore matchScore) {
        if (match == null) {
            throw new FootballMatchException("Match can not be null");
        }
        if (!MatchValidator.isCorrectMatchScores(matchScore)) {
            throw new FootballMatchException("Match score is incorrect");
        }
        return match.toBuilder()
                .matchScore(matchScore)
                .build();
    }

    @Override
    public FootballMatch finishMatch(FootballMatch match, LocalDateTime endTime) {
        if (match == null) {
            throw new FootballMatchException("Match can not be null");
        }
        if (endTime == null) {
            throw new FootballMatchException("Match end time can not be null");
        }
        if (match.fetchEndTime().isPresent()) {
            throw new FootballMatchException("Match has been already finished");
        }
        if (MatchValidator.isEndAfterStartTime(match.startTime(), endTime)) {
            return match.toBuilder()
                    .endTime(endTime)
                    .build();
        } else {
            throw new FootballMatchException("Match end time value is before start time");
        }
    }

    private void validateTeamNames(String homeTeam, String awayTeam) {
        boolean incorrectHomeTeam = MatchValidator.isIncorrectName(homeTeam);
        boolean incorrectAwayTeam = MatchValidator.isIncorrectName(awayTeam);
        if (incorrectHomeTeam && incorrectAwayTeam) {
            throw new FootballMatchException(MessageFormat.format("Given team names ({0}:{1}) are incorrect", homeTeam, awayTeam));
        } else {
            String teamName = null;
            if (incorrectHomeTeam) {
                teamName = homeTeam;
            } else if (incorrectAwayTeam) {
                teamName = awayTeam;
            }
            if (teamName != null) {
                throw new FootballMatchException(MessageFormat.format("Given team name ({0}) is incorrect", teamName));
            }
        }
    }

    private MatchScore initMatchScore() {
        return new MatchScore(0, 0);
    }
}
