
public class Driver {

    public static void main(String[] args) throws Exception {
      if (args[0].equals("class")) {
            EngineParser pe = new EngineParser(args[1],args[2]);
            pe.start();
        } else if (args[0].equals(("seq"))) {
        	EngineParserSeq pse = new EngineParserSeq(args[1], args[2], args[3], args[4]);
            pse.process();
        } else {
            System.out.println("Invalid keyword " + args[0]);
        }
    	
    /*	//args[0] = "seq";
    	EngineParserSeq pse = new EngineParserSeq("/Users/ipshamohanty/Desktop/Test Classes/uml-sequence-test","Optimist" ,"update", "diagram");
        pse.process();*/
    	
    }

}
