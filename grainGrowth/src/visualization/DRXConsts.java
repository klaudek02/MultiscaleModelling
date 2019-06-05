package visualization;

public enum DRXConsts {
    A(8.671097E13),
    B(9.41),
    DISTRIBUTION_PERCENT(0.3),
    CRITICAL_RO(4.2158E12);

    private final double value;

    DRXConsts(double value){
        this.value = value;
    }

    double getValue(){
        return this.value;
    }
}
