package pl.football.worldcup.scoreboard.storage;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import pl.football.worldcup.scoreboard.exception.MatchStorageException;
import pl.football.worldcup.scoreboard.model.FootballMatch;

@Slf4j
public class InMemoryStorage implements MatchStorage {

    private final AtomicLong idCounter = new AtomicLong(1);

    private final Map<Long, FootballMatch> storage;

    public InMemoryStorage() {
        this(new HashMap<>());
    }

    public InMemoryStorage(Map<Long, FootballMatch> storage) {
        this.storage = storage;
    }

    @Override
    public FootballMatch saveMatch(FootballMatch match) {
        log.debug("Saving match " + match);
        if (match.id() > 0L && storage.containsKey(match.id())) {
            throw new MatchStorageException("Match object already exist in storage");
        } else {
            FootballMatch matchNew = match;
            if (matchNew.id() == 0L) {
                matchNew = matchNew.toBuilder()
                        .id(idCounter.getAndIncrement())
                        .build();
            }
            storage.put(matchNew.id(), matchNew);

            return matchNew;
        }
    }

    @Override
    public FootballMatch updateMatch(FootballMatch match) {
        log.debug("Updating match " + match);
        if (storage.containsKey(match.id())) {
            storage.put(match.id(), match);
            return match;
        } else {
            throw new MatchStorageException("There is no match object in storage");
        }
    }

    @Override
    public FootballMatch getMatch(Long id) {
        log.debug("Try to fetch match using id " + id);
        if (!storage.containsKey(id)) {
            throw new MatchStorageException(MessageFormat.format("There is no match with id={0}", id));
        }
        return storage.get(id);
    }

    @Override
    public List<FootballMatch> getAllMatches() {
        return storage.values()
                .stream()
                .toList();
    }

    @Override
    public List<FootballMatch> getAllMatchesInProgress() {
        return storage.values()
                .stream()
                .filter(match -> match.fetchEndTime().isEmpty())
                .collect(Collectors.toList());
    }
}
