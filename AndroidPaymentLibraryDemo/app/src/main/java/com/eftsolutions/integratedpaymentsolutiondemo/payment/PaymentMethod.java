package com.eftsolutions.integratedpaymentsolutiondemo.payment;

public enum PaymentMethod {
    ALIPAY_HK(0, "ALIPAYHK"),
    ALIPAY_CN(1, "ALIPAYCN"),
    WECHAT_PAY_HK(2, "WECHAT"),
    MASTERCARD(3, "VM"),
    FPS(4, "FPS"),
    OCTOPUS(5, "OCTOPUS");

    private final int method;
    private final String type;

    PaymentMethod(int method, String type) {
        this.method = method;
        this.type = type;
    }

    public int getMethod() {
        return this.method;
    }

    public String getType() {
        return this.type;
    }

    public static PaymentMethod getByMethod(int lookupValue) {
        if (lookupValue < 0) {
            return null;
        }

        for (PaymentMethod paymentMethod : values()) {
            if (paymentMethod.method == lookupValue) {
                return paymentMethod;
            }
        }

        return null;
    }
}