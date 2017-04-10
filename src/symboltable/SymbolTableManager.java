package symboltable;

import java.util.HashMap;
import java.util.Map;

public class SymbolTableManager {

	private static Map<String, SymbolTable> class_TableMap = new HashMap<>();

	public static void addSymbolTable(String className, SymbolTable st) {
		class_TableMap.put(className, st);
	}

	public static void removeSymbolTable(String className) {
		class_TableMap.remove(className);
	}

	public static SymbolTable getSymbolTable(String className) {
		return class_TableMap.get(className);
	}

	public static SymbolTable createChildSymbolTable(String parentClassName) {
		SymbolTable st = class_TableMap.get(parentClassName);
		if (st != null) {

			SymbolTable cst = new SymbolTable();
			cst.setParentTable(st);
			return cst;
		}
		return new SymbolTable();
	}

}
