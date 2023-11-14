class A {
    static{
        System.out.println("a");
    }
    public A(){
        System.out.println("b");
    }
}

class B extends A{
    static{
        System.out.println("1");
    }
    public B(){
        System.out.println("2");
    }
}


public class test {
    public static void main(String []args){
        A a=new B();
          a=new B();
    }
}






/*1.integer的200 是否等于int的200

2.数组冒牌排序


3.查找数组第三大的值*/

















