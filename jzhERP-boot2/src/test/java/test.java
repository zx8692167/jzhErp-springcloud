public class test {
    public static void main(String []args){
        String values = "[10]";
        String roleId = null;
        if(values!=null) {
            values = values.replaceAll("\\[\\]",",").replace("[","").replace("]","");
        }
        System.out.println(values);
        String [] valueArray=values.split(",");
        if(valueArray.length>0) {
            roleId = valueArray[0];
        }
        System.out.println(roleId);
    }
}
