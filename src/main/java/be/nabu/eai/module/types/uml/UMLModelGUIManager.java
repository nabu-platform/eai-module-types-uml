package be.nabu.eai.module.types.uml;

import be.nabu.eai.developer.managers.TypeRegistryGUIManager;
import be.nabu.libs.types.uml.UMLRegistry;

public class UMLModelGUIManager extends TypeRegistryGUIManager<UMLRegistry> {

	public UMLModelGUIManager() {
		super(new UMLModelManager(), "UML Model");
	}
	
	@Override
	public String getCategory() {
		return "Types";
	}

}
