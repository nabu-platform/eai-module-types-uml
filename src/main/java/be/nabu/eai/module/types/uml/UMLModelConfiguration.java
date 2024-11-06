/*
* Copyright (C) 2016 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.eai.module.types.uml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;

@XmlRootElement(name = "umlModel")
public class UMLModelConfiguration {
	
	private boolean generateCollectionNames, addDatabaseFields = true, generateFlatDocuments = true, inverseParentChildRelationship, useLongs, useExtensions;
	private String createdField, modifiedField;
	private List<UMLModelArtifact> imports;
	
	public UMLModelConfiguration() {
		// auto
	}
	public UMLModelConfiguration(boolean initial) {
		if (initial) {
			createdField = "created";
			modifiedField = "modified";
		}
	}
	
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

	public boolean isUseLongs() {
		return useLongs;
	}
	public void setUseLongs(boolean useLongs) {
		this.useLongs = useLongs;
	}
	public boolean isUseExtensions() {
		return useExtensions;
	}
	public void setUseExtensions(boolean useExtensions) {
		this.useExtensions = useExtensions;
	}
}
