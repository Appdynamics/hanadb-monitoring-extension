package com.appdynamics.monitors.hanadb;

import com.appdynamics.TaskInputArgs;
import com.appdynamics.extensions.crypto.CryptoUtil;
import com.appdynamics.monitors.hanadb.config.Config;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.appdynamics.monitors.hanadb.config.Globals.PASSWORD_ENCRYPTED;

/**
 * Created by michi on 20.02.17.
 */
class Utilities {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Utilities.class);

    static String convertToString(final Object field, final String defaultStr){
        if(field == null){
            return defaultStr;
        }
        return field.toString();
    }

    public static String[] split(final String metricType,final String splitOn) {
        return metricType.split(splitOn);
    }

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

    static String getPassword(Config config) {
        String password = config.getPassword();
        if (!Strings.isNullOrEmpty(password)) {
            return password;
        }
        String encryptionKey = config.getEncryptionKey();
        String encryptedPassword = config.getEncryptionPassword();
        if (!Strings.isNullOrEmpty(encryptionKey) && !Strings.isNullOrEmpty(encryptedPassword)) {
            java.util.Map<String, String> cryptoMap = Maps.newHashMap();
            cryptoMap.put(PASSWORD_ENCRYPTED, encryptedPassword);
            cryptoMap.put(TaskInputArgs.ENCRYPTION_KEY, encryptionKey);
            return CryptoUtil.getPassword(cryptoMap);
        }
        return null;
    }

    static String getURL(Config config) {
        return config.getJdbcPrefix() +
                config.getHost() +
                ":" +
                config.getPort() +
                config.getJdbcOptions();
    }
}
