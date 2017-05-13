

import java.io.*;
import java.util.*;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;

import net.sourceforge.plantuml.SourceStringReader;

public class EngineParserSeq {
    String Code_mine;
    final String entryPath;
    final String exitPath;
    final String func_name_in;
    final String class_name_in;

    HashMap<String, String> Hashmap_meth;
    ArrayList<CompilationUnit> Comp_arr_unit;
    HashMap<String, ArrayList<MethodCallExpr>> Arraylist_meth;
    
    private String produce(String source) throws IOException {

        OutputStream png = new FileOutputStream(exitPath);
        SourceStringReader reader = new SourceStringReader(source);
        String desc = reader.generateImage(png);
        return desc;

    }

    EngineParserSeq(String inPath, String inClassName, String inFuncName,
            String outFile) {
        this.entryPath = inPath;
        this.exitPath = inPath + "/" + outFile + ".png";
        this.class_name_in = inClassName;
        this.func_name_in = inFuncName;
        Hashmap_meth = new HashMap<String, String>();
        Arraylist_meth = new HashMap<String, ArrayList<MethodCallExpr>>();
        Code_mine = "@startuml\n";
    }

    

   

    private void design() {
        for (CompilationUnit cu : Comp_arr_unit) {
            String className = "";
            List<TypeDeclaration> td = cu.getTypes();
            for (Node n : td) {
                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
                className = coi.getName();
                for (BodyDeclaration bd : ((TypeDeclaration) coi)
                        .getMembers()) {
                    if (bd instanceof MethodDeclaration) {
                        MethodDeclaration md = (MethodDeclaration) bd;
                        ArrayList<MethodCallExpr> mcea = new ArrayList<MethodCallExpr>();
                        for (Object bs : md.getChildrenNodes()) {
                            if (bs instanceof BlockStmt) {
                                for (Object es : ((Node) bs)
                                        .getChildrenNodes()) {
                                    if (es instanceof ExpressionStmt) {
                                        if (((ExpressionStmt) (es))
                                                .getExpression() instanceof MethodCallExpr) {
                                            mcea.add(
                                                    (MethodCallExpr) (((ExpressionStmt) (es))
                                                            .getExpression()));
                                        }
                                    }
                                }
                            }
                        }
                        Arraylist_meth.put(md.getName(), mcea);
                        Hashmap_meth.put(md.getName(), className);
                    }
                }
            }
        }
        
    }

    private ArrayList<CompilationUnit> getCuArray(String inPath)
            throws Exception {
        File folder = new File(inPath);
        ArrayList<CompilationUnit> cuArray = new ArrayList<CompilationUnit>();
        for (final File f : folder.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".java")) {
                FileInputStream in = new FileInputStream(f);
                CompilationUnit cu;
                try {
                    cu = JavaParser.parse(in);
                    cuArray.add(cu);
                } finally {
                    in.close();
                }
            }
        }
        return cuArray;
    }

   

    @SuppressWarnings("unused")
    private void printMaps() {
        System.out.println("Arraylist_meth:");
        Set<String> keys = Arraylist_meth.keySet(); 
        for (String i : keys) {
            System.out.println(i + "->" + Arraylist_meth.get(i));
        }
        System.out.println("---");
        keys = null;

        System.out.println("Hashmap_meth:");
        keys = Hashmap_meth.keySet(); 
        for (String i : keys) {
            System.out.println(i + "->" + Hashmap_meth.get(i));
        }
        System.out.println("---");
    }
    
    
    public void process() throws Exception {
        Comp_arr_unit = getCuArray(entryPath);
        design();
        Code_mine += "actor user #blue\n";
        Code_mine += "user" + " -> " + class_name_in + " : " + func_name_in + "\n";
        Code_mine += "activate " + Hashmap_meth.get(func_name_in) + "\n";
        parse(func_name_in);
        Code_mine += "@enduml";
        produce(Code_mine);
        System.out.println("Plant UML Code:\n" + Code_mine);
    }
    
    private void parse(String callerFunc) {

        for (MethodCallExpr mce : Arraylist_meth.get(callerFunc)) {
            String callerClass = Hashmap_meth.get(callerFunc);
            String calleeFunc = mce.getName();
            String calleeClass = Hashmap_meth.get(calleeFunc);
            if (Hashmap_meth.containsKey(calleeFunc)) {
                Code_mine += callerClass + " -> " + calleeClass + " : "
                        + mce.toStringWithoutComments() + "\n";
                Code_mine += "activate " + calleeClass + "\n";
                parse(calleeFunc);
                Code_mine += calleeClass + " -->> " + callerClass + "\n";
                Code_mine += "deactivate " + calleeClass + "\n";
            }
        }
    }

}
