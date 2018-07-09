package com.xio.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * md5帮助类
 * 
 * @title:
 * @Description:md5帮助类
 * @version 1.0
 * @since EIDC V100R001C02B03
 * @author:jiangt
 */

public final class MD5Util
{

    private static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private MD5Util()
    {

    }

    /**
     * md5Str
     * 
     * @param str
     *            String
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String md5Str(String str)
    {
        if (str == null)
        {
            return "";
        }
        return md5Str(str, 0);
    }
    
    /**
     * 16位MD5加密算法
     * @param str
     * @return
     */
    public static String md5Str16(String str)
    {
        String str32 = md5Str(str);
        if (str32.length() == 32) {
        	return str32.substring(8, 24);
		} else {
			return "";
		}
       
    }

    /**
     * 计算消息摘要。
     * 
     * @param offset
     *            数据偏移地址。
     * 
     * @param str
     *            数据 。
     * 
     * @return 摘要结果。(16字节)
     */
    public static String md5Str(String str, int offset)
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] b = str.getBytes("UTF8");
            md5.update(b, offset, b.length);
            return byteArrayToHexString(md5.digest());
        }
        catch (NoSuchAlgorithmException ex)
        {
            // ExceptionMsg exceptionMsg = new ExceptionMsg();
            // exceptionMsg.runLog(RunLogData.ERROR,
            // exceptionMsg.getExceptionMsg(ex));
            // ex.printStackTrace();
            return null;
        }
        catch (UnsupportedEncodingException ex)
        {
            // ex.printStackTrace();
            // ExceptionMsg exceptionMsg = new ExceptionMsg();
            // exceptionMsg.runLog(RunLogData.ERROR,
            // exceptionMsg.getExceptionMsg(ex));
            return null;
        }
    }

    /**
     * 字节数组转换成字符串
     * 
     * @param b
     *            byte[]
     * @return String
     */
    public static String byteArrayToHexString(byte[] b)
    {
        // String result = "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
        {
            sb.append(byteToHexString(b[i]));
        }
        return sb.toString();
    }

    /**
     * 将字节转换为对应的16进制明文
     * 
     * @param b
     *            byte
     * @return String
     */
    public static String byteToHexString(byte b)
    {
        int n = b;
        if (n < 0)
        {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
    
    public static String getMD5Digest(String str) {
		try {
			byte[] res = str.getBytes();
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] result = md.digest(res);
			for (int i = 0; i < result.length; i++) {
				md.update(result[i]);
			}
			byte[] hash = md.digest();
			StringBuffer d = new StringBuffer("");
			for (int i = 0; i < hash.length; i++) {
				int v = hash[i] & 0xFF;
				if (v < 16)
					d.append("0");
				d.append(Integer.toString(v, 16).toUpperCase() + "");
			}
			return d.toString();
		} catch (Exception e) {
			return "";
		}
	}


    public static void main(String[] args)
    {
//        String str = "adm123";
//        String ened2 = md5Str16(str);
//        
//        System.out.println(md5Str(str));
//        System.out.println(md5Str16(str));
//        //System.out.println(md5Str(str, 9));
//        System.out.println(getMD5Digest(str));
        //System.out.println(ened2.length());
        //System.out.println(getMD5Digest("40006020140915114912200010082345wert"));
    	
    	//账号、随机密码生成的demo
    	for (int i = 1; i < 11; i++) {
    		System.out.print("账号:yyz"+String.format("%03d", i)+"\t");
    		String num = (int)(100000+Math.random()*100000)+"";
			System.out.print("密码:"+num+"\t");
			System.out.println("\t"+md5Str(num));
		}
    	
    }
}
