

import java.io.*;
import java.util.*;
import java.lang.*;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class EngineParser {
    final String input_file_path;
    final String output_file_string;
    HashMap<String, Boolean> doesmap;
    HashMap<String, String> classMap;
    String ast_string;
    ArrayList<CompilationUnit> compilationUnit;
    
    
private String parser(CompilationUnit cu) {
        String result = "";
        String className = "";
        String classShortName = "";
        String functions = "";
        String symbols = "";
        String append = ",";

        ArrayList<String> makeFieldPublic = new ArrayList<String>();
        List<TypeDeclaration> ltd = cu.getTypes();
        Node node = ltd.get(0); 

       
        ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) node;
        if (coi.isInterface()) {
            className = "[" + "<<interface>>;";
        } else {
            className = "[";
        }
        className += coi.getName();
        classShortName = coi.getName();

       
        boolean nextParam = false;
        for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {
           
            if (bd instanceof ConstructorDeclaration) {
                ConstructorDeclaration cd = ((ConstructorDeclaration) bd);
                if (cd.getDeclarationAsString().startsWith("public")
                        && !coi.isInterface()) {
                    if (nextParam)
                        functions += ";";
                    functions += "+ " + cd.getName() + "(";
                    for (Object gcn : cd.getChildrenNodes()) {
                        if (gcn instanceof Parameter) {
                            Parameter paramCast = (Parameter) gcn;
                            String paramClass = paramCast.getType().toString();
                            String paramName = paramCast.getChildrenNodes()
                                    .get(0).toString();
                            functions += paramName + " : " + paramClass;
                            if (doesmap.containsKey(paramClass)
                                    && !doesmap.get(classShortName)) {
                                append += "[" + classShortName
                                        + "] uses -.->";
                                if (doesmap.get(paramClass))
                                    append += "[<<interface>>;" + paramClass
                                            + "]";
                                else
                                    append += "[" + paramClass + "]";
                            }
                            append += ",";
                        }
                    }
                    functions += ")";
                    nextParam = true;
                }
            }
        }
        for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = ((MethodDeclaration) bd);
              
                if (md.getDeclarationAsString().startsWith("public")
                        && !coi.isInterface()) {
                 
                    if (md.getName().startsWith("set")
                            || md.getName().startsWith("get")) {
                        String varName = md.getName().substring(3);
                        makeFieldPublic.add(varName.toLowerCase());
                    } else {
                        if (nextParam)
                            functions += ";";
                        functions += "+ " + md.getName() + "(";
                        for (Object gcn : md.getChildrenNodes()) {
                            if (gcn instanceof Parameter) {
                                Parameter paramCast = (Parameter) gcn;
                                String paramClass = paramCast.getType()
                                        .toString();
                                String paramName = paramCast.getChildrenNodes()
                                        .get(0).toString();
                                functions += paramName + " : " + paramClass;
                                if (doesmap.containsKey(paramClass)
                                        && !doesmap.get(classShortName)) {
                                    append += "[" + classShortName
                                            + "] uses -.->";
                                    if (doesmap.get(paramClass))
                                        append += "[<<interface>>;"
                                                + paramClass + "]";
                                    else
                                        append += "[" + paramClass + "]";
                                }
                                append += ",";
                            } else {
                                String methodBody[] = gcn.toString().split(" ");
                                for (String foo : methodBody) {
                                    if (doesmap.containsKey(foo)
                                            && !doesmap.get(classShortName)) {
                                        append += "[" + classShortName
                                                + "] uses -.->";
                                        if (doesmap.get(foo))
                                            append += "[<<interface>>;" + foo
                                                    + "]";
                                        else
                                            append += "[" + foo + "]";
                                        append += ",";
                                    }
                                }
                            }
                        }
                        functions += ") : " + md.getType();
                        nextParam = true;
                    }
                }
            }
        }
       
        boolean nextField = false;
        for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = ((FieldDeclaration) bd);
                String fieldScope = accessmodifier(
                        bd.toStringWithoutComments().substring(0,
                                bd.toStringWithoutComments().indexOf(" ")));
                String fieldClass = bracemodify(fd.getType().toString());
                String fieldName = fd.getChildrenNodes().get(1).toString();
                if (fieldName.contains("="))
                    fieldName = fd.getChildrenNodes().get(1).toString()
                            .substring(0, fd.getChildrenNodes().get(1)
                                    .toString().indexOf("=") - 1);
             
                if (fieldScope.equals("-")
                        && makeFieldPublic.contains(fieldName.toLowerCase())) {
                    fieldScope = "+";
                }
                String getDepen = "";
                boolean getDepenMultiple = false;
                if (fieldClass.contains("(")) {
                    getDepen = fieldClass.substring(fieldClass.indexOf("(") + 1,
                            fieldClass.indexOf(")"));
                    getDepenMultiple = true;
                } else if (doesmap.containsKey(fieldClass)) {
                    getDepen = fieldClass;
                }
                if (getDepen.length() > 0 && doesmap.containsKey(getDepen)) {
                    String connection = "-";

                    if (classMap
                            .containsKey(getDepen + "-" + classShortName)) {
                        connection = classMap
                                .get(getDepen + "-" + classShortName);
                        if (getDepenMultiple)
                            connection = "*" + connection;
                        classMap.put(getDepen + "-" + classShortName,
                                connection);
                    } else {
                        if (getDepenMultiple)
                            connection += "*";
                        classMap.put(classShortName + "-" + getDepen,
                                connection);
                    }
                }
                if (fieldScope == "+" || fieldScope == "-") {
                    if (nextField)
                        symbols += "; ";
                    symbols += fieldScope + " " + fieldName + " : " + fieldClass;
                    nextField = true;
                }
            }

        }
       
        if (coi.getExtends() != null) {
            append += "[" + classShortName + "] " + "-^ " + coi.getExtends();
            append += ",";
        }
        if (coi.getImplements() != null) {
            List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) coi
                    .getImplements();
            for (ClassOrInterfaceType intface : interfaceList) {
                append += "[" + classShortName + "] " + "-.-^ " + "["
                        + "<<interface>>;" + intface + "]";
                append += ",";
            }
        }
       
        result += className;
        if (!symbols.isEmpty()) {
            result += "|" + bracemodify(symbols);
        }
        if (!functions.isEmpty()) {
            result += "|" + bracemodify(functions);
        }
        result += "]";
        result += append;
        return result;
    }

   private String accessmodifier(String stringScope) {
        switch (stringScope) {
        case "private":
            return "-";
        case "public":
            return "+";
        default:
            return "";
        }
    }

    private void mapcreate(ArrayList<CompilationUnit> cuArray) {
        for (CompilationUnit cu : cuArray) {
            List<TypeDeclaration> cl = cu.getTypes();
            for (Node n : cl) {
                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
                doesmap.put(coi.getName(), coi.isInterface()); 
            }
        }
    }

    @SuppressWarnings("unused")
    private void testprints() {
        System.out.println("Map:");
        Set<String> keys = classMap.keySet(); 
        for (String i : keys) {
            System.out.println(i + "->" + classMap.get(i));
        }
        System.out.println("---");
    }
    
    
    
    protected String bracemodify(String c) {
        c = c.replace("[", "(");
        c = c.replace("]", ")");
        c = c.replace("<", "(");
        c = c.replace(">", ")");
        return c;
    }

  

    private String determine(String code) {
        String[] codeLines = code.split(",");
        String[] uniqueCodeLines = new LinkedHashSet<String>(
                Arrays.asList(codeLines)).toArray(new String[0]);
        String result = String.join(",", uniqueCodeLines);
        return result;
    }
    
    
    EngineParser(String inPath, String outFile) {
        this.input_file_path = inPath;
        this.output_file_string = inPath + "/" + outFile + ".png";
        doesmap = new HashMap<String, Boolean>();
        classMap = new HashMap<String, String>();
        ast_string = "";
    }

   
    public void start() throws Exception {
        compilationUnit = getCuArray(input_file_path);
        mapcreate(compilationUnit);
        for (CompilationUnit cu : compilationUnit)
            ast_string += parser(cu);
        ast_string += modifyParse();
        ast_string = determine(ast_string);
        System.out.println("Unique Code: " + ast_string);
        DiagramCreator.pngDiag(ast_string, output_file_string);
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
    
    protected String modifyParse() {
        String result = "";
        Set<String> keys = classMap.keySet(); 
        for (String i : keys) {
            String[] classes = i.split("-");
            if (doesmap.get(classes[0]))
                result += "[<<interface>>;" + classes[0] + "]";
            else
                result += "[" + classes[0] + "]";
            result += classMap.get(i); 
            if (doesmap.get(classes[1]))
                result += "[<<interface>>;" + classes[1] + "]";
            else
                result += "[" + classes[1] + "]";
            result += ",";
        }
        return result;
    }
    

}
