package recycling.back.recycle.dto;

public class RecycleTotalCount {
    private Long totalPP;
    private Long totalPS;
    private Long totalPE;

    public RecycleTotalCount() {}

    public RecycleTotalCount(Long totalPP, Long totalPS, Long totalPE) {
        this.totalPP = totalPP;
        this.totalPS = totalPS;
        this.totalPE = totalPE;
    }

    public Long getTotalPP() {
        return totalPP;
    }

    public Long getTotalPS() {
        return totalPS;
    }

    public Long getTotalPE() {
        return totalPE;
    }
}
