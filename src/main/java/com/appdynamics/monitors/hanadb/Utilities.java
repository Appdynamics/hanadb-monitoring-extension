package com.appdynamics.monitors.hanadb;

import com.appdynamics.extensions.crypto.CryptoUtil;
import com.appdynamics.monitors.hanadb.config.Globals;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

class Utilities {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Utilities.class);

    static String toBigIntString(final BigDecimal metricValue) {
        return metricValue.setScale(0, RoundingMode.HALF_UP).toBigInteger().toString();
    }

    static BigDecimal convert(String convertFrom, String convertTo, BigDecimal metricValue){
        BigDecimal byteDivisor = new BigDecimal("1024");
        if (convertFrom.toLowerCase().equals("b")){
            if (convertTo.toLowerCase().equals("kb")){
                return metricValue.divide(byteDivisor,0,RoundingMode.HALF_UP);
            }
            else if (convertTo.toLowerCase().equals("mb")){
                return metricValue.divide(byteDivisor,0,RoundingMode.HALF_UP).divide(byteDivisor,0,RoundingMode.HALF_UP);
            }
            else if (convertTo.toLowerCase().equals("gb")){
                return metricValue.divide(byteDivisor,0,RoundingMode.HALF_UP).divide(byteDivisor,0,RoundingMode.HALF_UP).divide(byteDivisor,0,RoundingMode.HALF_UP);
            }
            else if (convertTo.toLowerCase().equals("tb")){
                return metricValue.divide(byteDivisor,0,RoundingMode.HALF_UP).divide(byteDivisor,0,RoundingMode.HALF_UP).divide(byteDivisor,0,RoundingMode.HALF_UP).divide(byteDivisor,0,RoundingMode.HALF_UP);
            }
            else {
                logger.error("Error converting metric {} from type {} to type {}",metricValue,convertFrom,convertTo);
                return metricValue;
            }
        }
        else {
            return metricValue;
        }
    }

    static String getPassword(Map<String, ?> config) {
        String password = (String) config.get(Globals.password);
        if (!Strings.isNullOrEmpty(password)) {
            return password;
        }
        String encryptionKey = (String) config.get(Globals.encryptionKey);
        String encryptedPassword = (String) config.get(Globals.passwordEncrypted);
        if (!Strings.isNullOrEmpty(encryptionKey) && !Strings.isNullOrEmpty(encryptedPassword)) {
            java.util.Map<String, String> cryptoMap = Maps.newHashMap();
            cryptoMap.put(Globals.passwordEncrypted, encryptedPassword);
            cryptoMap.put(Globals.encryptionKey, encryptionKey);
            return CryptoUtil.getPassword(cryptoMap);
        }
        return null;
    }

    static String getJdbcDriverClass(Map<String, ?> config) {
        if(config.get(Globals.driver) != null){
            Globals.jdbcDriverClass = (String) config.get(Globals.driver);
        }
        return Globals.jdbcDriverClass;
    }
}
