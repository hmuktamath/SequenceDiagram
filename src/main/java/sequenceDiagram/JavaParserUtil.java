package sequenceDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;

public class JavaParserUtil {

	public void parseJava(HashMap<CompilationUnit, String> cuMap,
			HashMap<String, ArrayList<MethodCallExpr>> mapMethodCalls, HashMap<String, String> mapMethodClassName,
			HashMap<String, ArrayList<MethodDeclaration>> mapMethod) {
		for (CompilationUnit cu : cuMap.keySet()) {
			ArrayList<MethodDeclaration> methodDeclartionList = new ArrayList<MethodDeclaration>();
			String className = "";
			List<TypeDeclaration> typeDeclarationList = cu.getTypes();
			for (Node node : typeDeclarationList) {
				ClassOrInterfaceDeclaration ClassDeclaration = (ClassOrInterfaceDeclaration) node;
				className = ClassDeclaration.getName();
				for (BodyDeclaration bd : ((TypeDeclaration) ClassDeclaration).getMembers()) {
					if (bd instanceof MethodDeclaration) {
						mapMethodCalls.put(((MethodDeclaration) (bd)).getName(),
								getMethodBodyList(((MethodDeclaration) (bd))));
						methodDeclartionList.add(((MethodDeclaration) (bd)));
						mapMethodClassName.put(((MethodDeclaration) (bd)).getName(), className);
					}
				}
			}
			mapMethod.put(className, methodDeclartionList);
		}
	}

	public void functionInCu(String functionName, HashMap<CompilationUnit, String> cuMap) {
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

	public ArrayList<MethodCallExpr> getMethodBodyList(MethodDeclaration methodDeclaration) {
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
}
