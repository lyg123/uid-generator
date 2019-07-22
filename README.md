优化点：
1.用完即弃的数据库操作使用mybatis注解，请看代码`WorkerNodeDAO`
2.`CachedUidGenerator#setPaddingFactor`增加，用于spring注入

例如节点采取用完即弃的WorkerIdAssigner策略, 重启频率为12次/天, 那么配置成{"workerBits":23,"timeBits":31,"seqBits":9}时, 可支持28个节点以整体并发量14400 UID/s的速度持续运行68年. 时间 2的31次方-1 / 86400 / 365 = 68年

序号 2的9次方 = 512

workid数 2的23次方 = 8388608

重启次数 68 乘上 365 乘上 12 天 = 297840

应用数 节点 除以 重启次数 = 8388608 / 297840 = 28

并发 28 乘上 512 = 14336

另一个例子

workid 30位 = 1073741824 (2位随机数+后两段ip)

time 29位 为17年

seq 4位 为16

重启次数 17 * 365 * 12 = 950460

应用 = workid数 / 重启数 = 1129

并发 1128 * 16 = 1W8

应用重启的时候时间回拨+随机数一样，概率很低

2位随机数+后两段ip生成workid代码如下

import com.baidu.fsg.uid.worker.WorkerIdAssigner;
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
