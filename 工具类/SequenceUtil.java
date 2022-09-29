package com.xx.common.util;

import com.xx.common.sequence.SnowflakeIdWorker;

public class SequenceUtil {

	private static final SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

	/**
	 * 增加sequenceId值
	 * 
	 * @return
	 */
	public static long getSeqId() {
		return idWorker.nextId();
	}
	
	public static String getSeqIdString() {
		return String.valueOf(getSeqId());
	}

//	public static void main(String[] args) {
//        Map<String,Object> map = new HashMap<>();
//        for (int i = 0; i < 100; i++){
//        	map.put(getSeqIdString(), "value");
//        }
//		System.out.println(map.toString());
//        System.out.println(map.size());
//	}
}
