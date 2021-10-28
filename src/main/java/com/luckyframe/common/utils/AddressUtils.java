package com.luckyframe.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.luckyframe.common.utils.http.HttpUtils;
import com.luckyframe.framework.config.LuckyFrameConfig;

/**
 * 获取地址类
 * 
 * @author ruoyi
 */
public class AddressUtils
{
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    public static final String IP_URL = "https://ip.ws.126.net/ipquery";

    public static String getRealAddressByIP(String ip)
    {
        String address = "XX XX";
        // 内网不查询
        if (IpUtils.internalIp(ip))
        {
            return "内网IP";
        }
        if (LuckyFrameConfig.isAddressEnabled())
        {
            String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip);
            if (StringUtils.isEmpty(rspStr))
            {
                log.error("获取地理位置异常 {}", ip);
                return address;
            }
            String line1 = rspStr.split("\n")[0];
            return line1.replace("var","").replace(";","").replace("\"","").replace(" ","").replace(","," ");
//            JSONObject obj = JSONObject.parseObject(rspStr);
//            JSONObject data = obj.getObject("data", JSONObject.class);
//            String region = data.getString("province");
//            String city = data.getString("city");
//            address = region + " " + city;
        }
        return address;
    }
}
