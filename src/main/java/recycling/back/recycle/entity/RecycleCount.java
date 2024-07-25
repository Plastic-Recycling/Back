package recycling.back.recycle.entity;

import jakarta.persistence.*;
import recycling.back.user.register.entity.User;

import java.math.BigDecimal;

@Entity
public class RecycleCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int pe;

    private int pp;

    private int ps;

    @Column(nullable = false)
    private BigDecimal peRate;

    @Column(nullable = false)
    private BigDecimal ppRate;

    @Column(nullable = false)
    private BigDecimal psRate;

    private int peWeight;
    private int ppWeight;
    private int psWeight;

    @Column(nullable = false)
    private BigDecimal peCO2;

    @Column(nullable = false)
    private BigDecimal ppCO2;

    @Column(nullable = false)
    private BigDecimal psCO2;

    @OneToOne(mappedBy = "recycleCount")
    private User user;

    public RecycleCount(){
        this.peRate = BigDecimal.ZERO;
        this.ppRate = BigDecimal.ZERO;
        this.psRate = BigDecimal.ZERO;
        this.peCO2 = BigDecimal.ZERO;
        this.ppCO2 = BigDecimal.ZERO;
        this.psCO2 = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public int getPe() {
        return pe;
    }

    public int getPp() {
        return pp;
    }

    public int getPs() {
        return ps;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getPeRate() {
        return peRate;
    }

    public BigDecimal getPpRate() {
        return ppRate;
    }

    public BigDecimal getPsRate() {
        return psRate;
    }

    public int getPeWeight() {
        return peWeight;
    }

    public int getPpWeight() {
        return ppWeight;
    }

    public int getPsWeight() {
        return psWeight;
    }

    public BigDecimal getPeCO2() {
        return peCO2;
    }

    public BigDecimal getPpCO2() {
        return ppCO2;
    }

    public BigDecimal getPsCO2() {
        return psCO2;
    }

    public void ppCountUp(){
        this.pp += 1;
    }

    public void peCountUp(){
        this.pe += 1;
    }

    public void psCountUp(){
        this.ps += 1;
    }

    public void ppWeightUp(int weight){
        this.ppWeight += weight;
    }

    public void psWeightUp(int weight){
        this.psWeight += weight;
    }

    public void peWeightUp(int weight){
        this.peWeight += weight;
    }

    public void ppRateUp(BigDecimal rate){
        this.ppRate = this.ppRate.add(rate);
    }

    public void psRateUp(BigDecimal rate){
        this.psRate = this.psRate.add(rate);
    }

    public void peRateUp(BigDecimal rate){
        this.peRate = this.peRate.add(rate);
    }

    public void ppCO2Up(BigDecimal weight){
        this.ppCO2 = this.ppCO2.add(weight);
    }

    public void psCO2Up(BigDecimal weight){
        this.psCO2 = this.psCO2.add(weight);
    }

    public void peCO2Up(BigDecimal weight){
        this.peCO2 = this.peCO2.add(weight);
    }
}
