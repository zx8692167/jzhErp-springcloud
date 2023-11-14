import java.math.BigDecimal;
import java.util.Scanner;

public class ggg {
    public static void main(String[] args){
        Scanner sca = new Scanner(System.in);
        System.out.println("请输入您要查看的月数：");
        int month = sca.nextInt();
        BigDecimal[] num = new BigDecimal[month];
//        BigDecimal bigDecimal1=new BigDecimal(1);
//        BigDecimal bigDecimal2=new BigDecimal(1);
        //前两位是固定值，设置固定值
        num[0] = new BigDecimal(1);
        num[1] = new BigDecimal(1);;
        for (int i = 2; i < num.length; i++) {
            num[i] = num[i-1].add(num[i-2]);
        }
        System.out.println(month + "月兔子共有：" + num[month-1] + "对");
    }

}
