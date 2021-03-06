package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.game.Clue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("clueRepository")
public interface ClueRepository extends JpaRepository<Clue, Long> {
    Optional<Clue> findById(Long id);
}
