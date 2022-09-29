package com.hellozj.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IPUtils {
	private static Logger logger = LoggerFactory.getLogger(IPUtils.class);

	/**
	 * 获取本机IP,IP为空则获取MAC
	 * 
	 * @return
	 */
	public static final String getLocalIp() {
		String ipString = "";
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address && !ip.getHostAddress().equals("127.0.0.1")) {
						ipString = ip.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (StringUtils.isBlank(ipString)) {
			String mac = "";
			try {
				Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
				while (enumeration.hasMoreElements()) {
					StringBuffer stringBuffer = new StringBuffer();
					NetworkInterface networkInterface = enumeration.nextElement();
					if (networkInterface != null) {
						byte[] bytes = networkInterface.getHardwareAddress();
						if (bytes != null) {
							for (int i = 0; i < bytes.length; i++) {
								if (i != 0) {
									stringBuffer.append("-");
								}
								int tmp = bytes[i] & 0xff; // 字节转换为整数
								String str = Integer.toHexString(tmp);
								if (str.length() == 1) {
									stringBuffer.append("0" + str);
								} else {
									stringBuffer.append(str);
								}
							}
							mac = stringBuffer.toString().toUpperCase();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			ipString = mac;
		}

		return ipString;
	}

	/**
	 * 获取IP地址 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = null;
		try {
			ip = request.getHeader("X-Forwarded-For");

			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				// X-Real-IP：nginx服务代理
				ip = request.getHeader("X-Real-IP");
			}

			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {
			logger.error("IPUtils ERROR ", e);
		}

		// 如果是多级代理，那么取第一个ip为客户端ip
		if (StringUtils.isNotBlank(ip) && ip.indexOf(",") != -1) {
			ip = ip.substring(0, ip.indexOf(",")).trim();
		}

		return ip;
	}

	public static void main(String[] args) throws Exception {
		String ip = "123.123.252.214, 220.194.106.93";

		// 如果是多级代理，那么取第一个ip为客户端ip
		if (StringUtils.isNotBlank(ip) && ip.indexOf(",") != -1) {
			ip = ip.substring(0, ip.indexOf(",")).trim();
		}

		System.out.println(ip);

		System.out.println(IPUtils.getLocalIp());
	}

}
