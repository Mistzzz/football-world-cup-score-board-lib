package pl.football.worldcup.scoreboard.storage;

import java.util.List;

import pl.football.worldcup.scoreboard.model.FootballMatch;

public interface MatchStorage {

    FootballMatch saveMatch(FootballMatch match);

    FootballMatch updateMatch(FootballMatch match);

    FootballMatch getMatch(Long id);

    List<FootballMatch> getAllMatches();

    List<FootballMatch> getAllMatchesInProgress();
}
