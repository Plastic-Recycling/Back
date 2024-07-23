package recycling.back.recycle.dto;

public class DetectionResult {
    private String label;
    private int estimateWeight;
    private byte[] processedImage;

    public DetectionResult(){}

    public DetectionResult(String label, int estimateWeight, byte[] processedImage) {
        this.label = label;
        this.estimateWeight = estimateWeight;
        this.processedImage = processedImage;
    }

    public byte[] getProcessedImage() {
        return processedImage;
    }

    public void setProcessedImage(byte[] processedImage) {
        this.processedImage = processedImage;
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
