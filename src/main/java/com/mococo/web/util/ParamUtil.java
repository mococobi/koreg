package com.mococo.web.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ParamUtil {
    public static final BigDecimal getBigDecimal(Object object) {
        BigDecimal result = null;

        if (object != null) {
            if (object instanceof BigDecimal) {
                result = (BigDecimal) object;
            } else if (object instanceof String) {
                result = new BigDecimal((String) object);
            } else if (object instanceof BigInteger) {
                result = new BigDecimal((BigInteger) object);
            } else if (object instanceof Number) {
                result = new BigDecimal(((Number) object).doubleValue());
            }
        }

        return result;
    }
}
