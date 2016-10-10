package be.nabu.eai.module.types.uml;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "umlModel")
public class UMLModelConfiguration {
	
	private boolean generateCollectionNames, addDatabaseFields = true, generateFlatDocuments = true, inverseParentChildRelationship;
	private String createdField = "created", modifiedField = "modified";
	
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
	public boolean isInverseParentChildRelationship() {
		return inverseParentChildRelationship;
	}
	public void setInverseParentChildRelationship(boolean inverseParentChildRelationship) {
		this.inverseParentChildRelationship = inverseParentChildRelationship;
	}
	
}
