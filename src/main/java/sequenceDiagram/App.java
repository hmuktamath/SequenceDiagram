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

public class App {

	public static HashMap<CompilationUnit, String> cuMap = new HashMap<CompilationUnit, String>();
	public static HashMap<String, ArrayList<MethodDeclaration>> classMethodMap = new HashMap<String, ArrayList<MethodDeclaration>>();
	public static HashMap<String, String> methodClassNameMap = new HashMap<String, String>();
	public static HashMap<String, ArrayList<MethodCallExpr>> methodCallsMap = new HashMap<String, ArrayList<MethodCallExpr>>();

	public static String code = "@startuml \n autonumber \n";
	private static String os = System.getProperty("os.name");

	public static void main(String[] args) throws ParseException, IOException {
		String name = "";
		File dir = new File(args[0]);
		String[] files = dir.list();
		String basePath = dir.getAbsolutePath();
		if (files.length > 0) {
			System.out.println("*** Parsing started on Java Files ***");
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".java")) {
					if (os.equalsIgnoreCase("windows")) {
						name = basePath + "\\" + files[i];
					} else {
						name = basePath + "/" + files[i];
					}
					FileInputStream in;
					CompilationUnit cu;
					try {
						in = new FileInputStream(name);
						try {
							cu = JavaParser.parse(in);
							cuMap.put(cu, "");
						} finally {
							in.close();
						}
					} catch (FileNotFoundException e) {
						System.out.println("File not found");
						e.printStackTrace();
					} catch (ParseException e) {
						System.out.println("Java parsing errors");
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		String functionName = args[1];
		parseJavaFiles(cuMap);
		System.out.println("*** Java Parsing is done ***");

		code = code + "participant user \n";
		code = code + "user->" + methodClassNameMap.get(functionName) + " : " + functionName;
		code = code + "\nACTIVATE " + methodClassNameMap.get(functionName);
		
		findFunctionInCu(functionName, cuMap);
		
		System.out.println("*** Sequence Diagram is generating ***");
		drawSequenceDiagram(functionName);
		
		code = code + "\n" + methodClassNameMap.get(functionName) + " --> user:return " + functionName;
		code = code + "\nDEACTIVATE " + methodClassNameMap.get(functionName);
		code = code + "\n@enduml";
		
		drawSequenceDiagram(code, dir, args[2]);
		System.out.println("*** Sequence Diagram is completed at " + dir + "/" + args[2] + ".png ***");
	}

	private static void parseJavaFiles(HashMap<CompilationUnit, String> cuMap) {
		for (CompilationUnit cu : cuMap.keySet()) {
			ArrayList<MethodDeclaration> methodDeclartionList = new ArrayList<MethodDeclaration>();
			String className = "";
			List<TypeDeclaration> typeDeclarationList = cu.getTypes();
			for (Node node : typeDeclarationList) {
				ClassOrInterfaceDeclaration ClassDeclaration = (ClassOrInterfaceDeclaration) node;
				className = ClassDeclaration.getName();
				for (BodyDeclaration t : ((TypeDeclaration) ClassDeclaration).getMembers()) {
					if (t instanceof MethodDeclaration) {
						methodCallsMap.put(((MethodDeclaration) (t)).getName(),
								getMethodBodyList(((MethodDeclaration) (t))));
						methodDeclartionList.add(((MethodDeclaration) (t)));
						methodClassNameMap.put(((MethodDeclaration) (t)).getName(), className);
					}
				}
			}
			classMethodMap.put(className, methodDeclartionList);
		}
	}

	private static ArrayList<MethodCallExpr> getMethodBodyList(MethodDeclaration methodDeclaration) {
		ArrayList<MethodCallExpr> methodCallList = new ArrayList<MethodCallExpr>();
		for (Object blockstmt : methodDeclaration.getChildrenNodes()) {
			if (blockstmt instanceof BlockStmt) {
				for (Object exprstmt : ((Node) blockstmt).getChildrenNodes()) {
					if (exprstmt instanceof ExpressionStmt) {
						if (((ExpressionStmt) (exprstmt)).getExpression() instanceof MethodCallExpr) {
							methodCallList.add((MethodCallExpr) (((ExpressionStmt) (exprstmt)).getExpression()));
						}
					}
				}
			}
		}
		return methodCallList;
	}

	private static void findFunctionInCu(String functionName, HashMap<CompilationUnit, String> cuMap) {
		for (CompilationUnit cu : cuMap.keySet()) {
			List<TypeDeclaration> typeDeclarationList = cu.getTypes();
			for (Node node : typeDeclarationList) {
				ClassOrInterfaceDeclaration ClassDeclaration = (ClassOrInterfaceDeclaration) node;
				for (BodyDeclaration t : ((TypeDeclaration) ClassDeclaration).getMembers()) {
					if (t instanceof MethodDeclaration) {
						if (functionName.equals(((MethodDeclaration) t).getName())) {
							cuMap.put(cu, "main");
						}
					}
				}
			}
		}
	}

	private static void drawSequenceDiagram(String functionName) {
		for (MethodCallExpr methodCallExpr : methodCallsMap.get(functionName)) {
			if (methodCallExpr instanceof MethodCallExpr) {
				String methodName = methodCallExpr.getName();
				if (null != methodClassNameMap.get(methodName)) {
					code = code + "\n" + methodClassNameMap.get(functionName) + "->"
							+ methodClassNameMap.get(methodName) + ":" + methodCallExpr.toString();
					code = code + "\n ACTIVATE " + methodClassNameMap.get(methodName);
					drawSequenceDiagram(methodName);
					//code = code + "\n " + methodClassNameMap.get(methodName) + "-->" + methodClassNameMap.get(functionName);
					code = code + "\n DEACTIVATE " + methodClassNameMap.get(methodName);
				}
			}
		}
	}

	private static void drawSequenceDiagram(String source, File filePath, String fileName) throws IOException {

		String imagePath = "";
		if (System.getProperty("os.name").equalsIgnoreCase("Windows")) { // Windows
			imagePath = filePath + "\\" + fileName + ".png";
		} else { // Linux and Mac
			imagePath = filePath + "/" + fileName + ".png";
		}
		try {
			OutputStream os = new FileOutputStream(imagePath);
			SourceStringReader reader = new SourceStringReader(source);
			reader.generateImage(os);
		} catch (Exception e) {
			System.out.println("Exception while drawing sequence diagram.");
		}
	}

}
