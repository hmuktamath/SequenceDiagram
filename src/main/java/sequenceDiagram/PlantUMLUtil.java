package sequenceDiagram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.javaparser.ast.expr.MethodCallExpr;

import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLUtil {

	public String geenratePlantUMLScript(String functionName, HashMap<String, ArrayList<MethodCallExpr>> mapMethodCalls,
			HashMap<String, String> mapMethodClassName, String code) {
		for (MethodCallExpr methodCallExpr : mapMethodCalls.get(functionName)) {
			if (methodCallExpr instanceof MethodCallExpr) {
				String methodName = methodCallExpr.getName();
				if (null != mapMethodClassName.get(methodName)) {
					code = code + "\n" + mapMethodClassName.get(functionName) + "->"
							+ mapMethodClassName.get(methodName) + ":" + methodCallExpr.toString();
					code = code + "\n ACTIVATE " + mapMethodClassName.get(methodName);
					code = geenratePlantUMLScript(methodName, mapMethodCalls, mapMethodClassName, code);
					code = code + "\n DEACTIVATE " + mapMethodClassName.get(methodName);

				}
			}
		}
		return code;
	}

	public void drawDiagram(String plantUMLSource, File filePath, String fileName) throws IOException {

		String imagePath = "";
		if (System.getProperty("os.name").equalsIgnoreCase("Windows")) {
			// If OS is Windows.
			imagePath = filePath + "\\" + fileName + ".png";
		} else {
			// Non Windows
			imagePath = filePath + "/" + fileName + ".png";
		}
		try {
			OutputStream os = new FileOutputStream(imagePath);
			SourceStringReader reader = new SourceStringReader(plantUMLSource);
			reader.generateImage(os);
		} catch (Exception e) {
			System.out.println("Exception hit.");
		}
	}
}
