public class TestEnum {

//    public enum Data {
//        A(1), B(1), C(1);
//        int x;
//
//        Data(int x) {
//            this.x = x;
//        }
//
//    }
    public enum Data {
        A, B, C
    }

    public static void main(String[] args) {
        test(Data.A);
        test(Data.B);
        test(Data.C);
    }

//    public static void test(Data e_num){
//        switch (e_num) {
//            case A:
//                System.out.println(e_num + "(" + e_num.x + ")");//A(1)
//                break;
//            case B:
//                System.out.println(e_num + "(" + e_num.x + ")");//B(2)
//                break;
//            case C:
//                System.out.println(e_num + "(" + e_num.x + ")");//C(3)
//                break;
//        }
//    }
    public static void test(Data e_num){
        switch (e_num) {
            case A:
                System.out.println(e_num);//A
                break;
            case B:
                System.out.println(e_num);//B
                break;
            case C:
                System.out.println(e_num);//C
                break;
        }
    }
}
