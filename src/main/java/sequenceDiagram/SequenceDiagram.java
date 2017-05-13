package sequenceDiagram;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;


import net.sourceforge.plantuml.SourceStringReader;

public class SequenceDiagram {

	public static HashMap<CompilationUnit, String> mapCu = new HashMap<CompilationUnit, String>();
	public static HashMap<String, String> mapMethodClassName = new HashMap<String, String>();
	public static HashMap<String, ArrayList<MethodDeclaration>> mapMethod = new HashMap<String, ArrayList<MethodDeclaration>>();	
	public static HashMap<String, ArrayList<MethodCallExpr>> mapMethodCalls = new HashMap<String, ArrayList<MethodCallExpr>>();

	private static String os = System.getProperty("os.name");
	public static String code = "@startuml \n autonumber \n";
	;
	
	
	public static void main(String[] args) throws ParseException, IOException {
		PlantUMLUtil plantUMLUtil = new PlantUMLUtil();
		JavaParserUtil javaParserUtil = new JavaParserUtil();
		String name = "";
		File dir = new File(args[0]);
		String[] files = dir.list();
		String basePath = dir.getAbsolutePath();
		if (files.length > 0) {
			System.out.println("Start Parsing ---->");
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".java")) {
					if (os.equalsIgnoreCase("windows")) {
						name = basePath + "\\" + files[i];
					} else {
				
						name = basePath + "/" + files[i];
					}
					FileInputStream input;
					CompilationUnit compUnit;
					try {
						input = new FileInputStream(name);
						try {
							compUnit = JavaParser.parse(input);
							mapCu.put(compUnit, "");
						} finally {
							input.close();
						}
					} catch (FileNotFoundException e) {
						System.out.println("File not found");
						e.printStackTrace();
					} catch (ParseException e) {
						System.out.println("Errors in Parsing");
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		String nameOfFunction = args[1];
		javaParserUtil.parseJava(mapCu,mapMethodCalls, mapMethodClassName, mapMethod);
		System.out.println("Parsing complete ---->");

		code = code + "participant user \n";
		code = code + "user->" + mapMethodClassName.get(nameOfFunction) + " : " + nameOfFunction;
		code = code + "\nACTIVATE " + mapMethodClassName.get(nameOfFunction);
		
		javaParserUtil.functionInCu(nameOfFunction, mapCu);
		
		System.out.println("Generate Sequence Diagram ----->");

		code = plantUMLUtil.geenratePlantUMLScript(nameOfFunction, mapMethodCalls, mapMethodClassName, code);
		code = code + "\n" + mapMethodClassName.get(nameOfFunction) + " --> user:return " + nameOfFunction;
		code = code + "\nDEACTIVATE " + mapMethodClassName.get(nameOfFunction);
		code = code + "\n@enduml";
		
		plantUMLUtil.drawDiagram(code, dir, args[2]);
		System.out.println("Sequence Diagram at " + dir + "/" + args[2] + ".png ---->");
	}


}
