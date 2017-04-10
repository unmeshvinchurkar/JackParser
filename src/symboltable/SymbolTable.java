package symboltable;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

	private Map<String, Symbol> table = new HashMap<>();
	private SymbolTable parentTable = null;
	private String parentName = null;

	public void setParentTable(SymbolTable st) {
		parentTable = st;
	}

	public void addSymbol(Symbol s) {
		table.put(s.getName(), s);
	}

	public Symbol findSymbol(String name) {

		Symbol s = table.get(name);

		if (s != null) {
			return s;
		} else if (parentTable != null) {

			return parentTable.findSymbol(name);

		}

		return null;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

}
