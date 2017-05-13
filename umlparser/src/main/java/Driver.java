

public class Driver {

    public static void main(String[] args) throws Exception {
       if (args[0].equals(("seq"))) {
        	EngineParserSeq pse = new EngineParserSeq(args[1], args[2], args[3], args[4]);
            pse.process();
        } else {
            System.out.println("Invalid keyword " + args[0]);
        }
    	
    }

}