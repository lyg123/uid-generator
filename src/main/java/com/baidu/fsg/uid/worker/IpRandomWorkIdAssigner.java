package com.baidu.fsg.uid.worker;

import com.baidu.fsg.uid.utils.NetUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Random;

public class IpRandomWorkIdAssigner implements WorkerIdAssigner {

	private static final Random RANDOM = new Random();
	private static final int[] random = new int[90];

	public IpRandomWorkIdAssigner() {
		for (int i = 10; i < 100; i++) {
			random[i - 10] = i;
		}
	}

	@Override
	public long assignWorkerId() {
		String ip = NetUtils.getLocalAddress();
		String[] ips = ip.split("\\.");
		StringBuilder sb = new StringBuilder();
		sb.append(random[RANDOM.nextInt(90)]).append(StringUtils.leftPad(ips[2], 3, '0'))
				.append(StringUtils.leftPad(ips[3], 3, '0'));
		return Long.parseLong(sb.toString());
	}

}