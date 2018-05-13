package io.rsl.pragma.screens.txdispatch;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class GasAmount {

    private BigInteger gasAmount;
    private BigDecimal ethAmount;

    private BigDecimal e18 = new BigDecimal("1000000000000000000");

    public GasAmount(BigInteger gasAmount, BigInteger gasPrice) {
        this.gasAmount = gasAmount;
        this.ethAmount = new BigDecimal(gasPrice.multiply(gasAmount)).setScale(18, RoundingMode.HALF_UP).divide(e18, RoundingMode.UNNECESSARY).stripTrailingZeros();
    }

    public BigInteger getGasAmount() {
        return gasAmount;
    }

    public void setGasAmount(BigInteger gasAmount) {
        this.gasAmount = gasAmount;
    }

    public BigDecimal getEthAmount() {
        return ethAmount;
    }

    public void recalcEthPrice(BigInteger gasPrice) {
        ethAmount = new BigDecimal(gasPrice.multiply(gasAmount)).setScale(18, RoundingMode.HALF_UP).divide(e18, RoundingMode.UNNECESSARY).stripTrailingZeros();
    }

    @Override
    public String toString() {
        return String.format("%s Gas (%s Eth)", gasAmount.toString(), ethAmount.toPlainString());
    }
}
