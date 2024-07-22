package recycling.back.recycle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import recycling.back.recycle.entity.MountPlastic;

import java.util.Optional;

public interface MountPlasticRepository extends JpaRepository<MountPlastic, Long> {
    @Query("SELECT e FROM MountPlastic e WHERE FUNCTION('YEAR', e.updateDate) = :year AND FUNCTION('MONTH', e.updateDate) = :month")
    Optional<MountPlastic> findByUpdateDateYearAndMonth(@Param("year") int year, @Param("month") int month);
}
