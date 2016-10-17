package be.nabu.eai.module.types.uml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;

@XmlRootElement(name = "umlModel")
public class UMLModelConfiguration {
	
	private boolean generateCollectionNames, addDatabaseFields = true, generateFlatDocuments = true, inverseParentChildRelationship;
	private String createdField = "created", modifiedField = "modified";
	private List<UMLModelArtifact> imports;
	
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
	
	@XmlJavaTypeAdapter(value = ArtifactXMLAdapter.class)
	public List<UMLModelArtifact> getImports() {
		return imports;
	}
	public void setImports(List<UMLModelArtifact> imports) {
		this.imports = imports;
	}
	
}
