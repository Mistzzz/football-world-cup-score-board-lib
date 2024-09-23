package pl.football.worldcup.scoreboard.model;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public record FootballMatch(Long id, String homeTeam, String awayTeam, LocalDateTime startTime, LocalDateTime endTime, MatchScore matchScore) {

    public Optional<LocalDateTime> fetchEndTime() {
        return Optional.ofNullable(endTime);
    }

    public Integer getTotalScore() {
        return matchScore.homeScore() + matchScore.awayScore();
    }
}
