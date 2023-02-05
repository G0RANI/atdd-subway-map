package subway.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query(value = "SELECT l FROM Line l join fetch l.stations.upStation join fetch l.stations.downStation WHERE l.id = :id")
    Optional<Line> findByIdWithStation(@Param("id") Long id);

    @Query(value = "SELECT l FROM Line l join fetch l.stations.upStation join fetch l.stations.downStation")
    List<Line> findAllWithStation();
}
