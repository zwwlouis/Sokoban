public class Main {
    private static String stage = "8888888\n" +
                                    "8103018\n" +
                                    "8002008\n" +
                                    "8320238\n" +
                                    "8012108\n" +
                                    "8403008\n" +
                                    "8888888";
    public static void main(String[] args) {
        System.out.println("Hello World!");

        String a = "abcde";
        byte[] b = a.getBytes();
        for (int i = 0; i < b.length-1; i++) {
            System.out.println(b[i]+b[i+1]);
        }
    }



}
