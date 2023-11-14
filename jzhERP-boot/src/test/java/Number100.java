
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author soberw
 * @Classname Number100
 * @Description 用五个线程实现，求123456789 之间放+-和100的表达式，如果一个线程求出结果，立即告诉其它停止。
 * @Date 2021-12-27 21:14
 */
public class Number100 {
    //原子类，保证原子性
    AtomicBoolean ab = new AtomicBoolean(true);

    public void show() {
        String[] ss = {"", "+", "-"};
        StringBuilder sbu = new StringBuilder();
        sbu.append("1");
        Random random = new Random();
        while (ab.get()) {
            for (int i = 2; i < 9; i++) {
                sbu.append(ss[random.nextInt(3)]);
                sbu.append(i);
            }
            Pattern p = Pattern.compile("[0-9]+|-[0-9]+");
            Matcher m = p.matcher(sbu.toString());
            int sum = 0;
            while (m.find()) {
                sum += Integer.parseInt(m.group());
            }
            if (sum == 100) {
                ab.set(false);
                System.out.println(Thread.currentThread().getName() + ":" + sbu.toString() + " = 100");
            }
            sbu.delete(1, sbu.length());
        }
    }

    public static void main(String[] args) {
        var n = new Number100();
        for (int i = 0; i < 5; i++) {
            new Thread(n::show).start();
        }
    }
}
