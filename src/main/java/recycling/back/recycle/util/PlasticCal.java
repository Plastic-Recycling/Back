package recycling.back.recycle.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class PlasticCal {
    private BigDecimal recyclePE;
    private BigDecimal recyclePP;
    private BigDecimal recyclePS;

    @PostConstruct
    private void initialize(){
        Calculate cal = new Calculate();
        this.recyclePE = cal.recyclePE;
        this.recyclePP = cal.recyclePP;
        this.recyclePS = cal.recyclePS;
    }

    public BigDecimal getRecyclePE() {
        return recyclePE;
    }

    public BigDecimal getRecyclePP() {
        return recyclePP;
    }

    public BigDecimal getRecyclePS() {
        return recyclePS;
    }

    private static class Calculate {

        // 정밀도 반올림 모드
        MathContext mc = new MathContext(10, RoundingMode.HALF_UP);

        // 플라스틱으로 인한 총 배출량(Gt CO2e)
        BigDecimal totalEmissions = new BigDecimal("2.24e9", mc);

        // 유형별 배출 비율
        BigDecimal emissionPerPE = new BigDecimal("0.22", mc);
        BigDecimal emissionPerPP = new BigDecimal("0.15", mc);
        BigDecimal emissionPerPS = new BigDecimal("0.06", mc);

        // 유형별 배출량
        BigDecimal emissionPE = totalEmissions.multiply(emissionPerPE, mc);
        BigDecimal emissionPP = totalEmissions.multiply(emissionPerPP, mc);
        BigDecimal emissionPS = totalEmissions.multiply(emissionPerPS, mc);

        // 플라스틱 총 생산량(Mt)
        BigDecimal totalProduction = new BigDecimal("460e6", mc);

        // 유형별 생산 비율
        BigDecimal productionPerPE = new BigDecimal("0.24", mc);
        BigDecimal productionPerPP = new BigDecimal("0.17", mc);
        BigDecimal productionPerPS = new BigDecimal("0.05", mc);

        // 유형 별 생산량
        BigDecimal productionPE = totalProduction.multiply(productionPerPE, mc);
        BigDecimal productionPP = totalProduction.multiply(productionPerPP, mc);
        BigDecimal productionPS = totalProduction.multiply(productionPerPS, mc);

        // 유형별 배출량/생산량
        BigDecimal carbonPE = emissionPE.divide(productionPE, mc);
        BigDecimal carbonPP = emissionPP.divide(productionPP, mc);
        BigDecimal carbonPS = emissionPS.divide(productionPS, mc);

        // 재활용 시 탄소배출 감소량
        BigDecimal recyclePE = carbonPE.multiply(new BigDecimal("0.8"), mc);
        BigDecimal recyclePP = carbonPP.multiply(new BigDecimal("0.8"), mc);
        BigDecimal recyclePS = carbonPS.multiply(new BigDecimal("0.8"), mc);

    }
}
