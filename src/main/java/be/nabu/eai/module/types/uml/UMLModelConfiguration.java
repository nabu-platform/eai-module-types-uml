package be.nabu.eai.module.types.uml;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "umlModel")
public class UMLModelConfiguration {
	
	private boolean generateCollectionNames, addDatabaseFields = true, generateFlatDocuments = true;
	private String createdField = "dbCreatedUtc", modifiedField = "dbModifiedUtc";
	
	public boolean isGenerateCollectionNames() {
		return generateCollectionNames;
	}
	public void setGenerateCollectionNames(boolean generateCollectionNames) {
		this.generateCollectionNames = generateCollectionNames;
	}
	public boolean isAddDatabaseFields() {
		return addDatabaseFields;
	}
	public void setAddDatabaseFields(boolean addDatabaseFields) {
		this.addDatabaseFields = addDatabaseFields;
	}
	public boolean isGenerateFlatDocuments() {
		return generateFlatDocuments;
	}
	public void setGenerateFlatDocuments(boolean generateFlatDocuments) {
		this.generateFlatDocuments = generateFlatDocuments;
	}
	public String getCreatedField() {
		return createdField;
	}
	public void setCreatedField(String createdField) {
		this.createdField = createdField;
	}
	public String getModifiedField() {
		return modifiedField;
	}
	public void setModifiedField(String modifiedField) {
		this.modifiedField = modifiedField;
	}
}
