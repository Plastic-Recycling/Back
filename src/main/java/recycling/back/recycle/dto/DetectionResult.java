package recycling.back.recycle.dto;

public class DetectionResult {
    private String label;
    private int estimateWeight;

    public DetectionResult(){}

    public DetectionResult(String label, int estimateWeight) {
        this.label = label;
        this.estimateWeight = estimateWeight;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getEstimateWeight() {
        return estimateWeight;
    }

    public void setEstimateWeight(int estimateWeight) {
        this.estimateWeight = estimateWeight;
    }
}
