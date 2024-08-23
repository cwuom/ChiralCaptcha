package com.cwuom.chiralcaptcha.statics;

public final class Constants {
    private Constants() {
        throw new AssertionError("No instances for you!");
    }

    public static final int MAX_MOLECULES_L1 = 5000;
    public static final int MAX_MOLECULES_L2 = 4999;
    public static final int MAX_MOLECULES_L3 = 1000;
    public static final int MAX_MOLECULES_L4 = 373;
    public static final int[] MAX_MOLECULES = {MAX_MOLECULES_L1, MAX_MOLECULES_L2, MAX_MOLECULES_L3, MAX_MOLECULES_L4};

    public static final int ACTION_REANSWER = 1;


    public static final String GITHUB_LINK = "https://github.com/cwuom/ChiralCaptcha";
    public static final String TELEGRAM_LINK = "https://t.me/cwuoms_group";
    public static final String QQ_LINK = "http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=UD5xNmXt0Otz0OrvpXCaKnSd04BDf0rm&authKey=40ctuZ7TZLHzf1LBJZ29Nqvu%2F55gAnvqJ%2FB7s8oJvWsM7AA07%2BXIF8J2GKctM4hD&noverify=0&group_code=923071208";

}