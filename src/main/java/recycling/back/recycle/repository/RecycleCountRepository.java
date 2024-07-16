package recycling.back.recycle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import recycling.back.recycle.dto.RecycleTotalCount;
import recycling.back.recycle.entity.RecycleCount;

@Repository
public interface RecycleCountRepository extends JpaRepository<RecycleCount, Long> {
    @Query("SELECT SUM(r.pp) as totalPP, SUM(r.pe) as totalPE, SUM(r.ps) as totalPS FROM RecycleCount r")
    RecycleTotalCount getTotalRecycleCount();

}
