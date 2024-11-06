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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import be.nabu.eai.module.types.xml.EntryResourceResolver;
import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.api.ResourceEntry;
import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;
import be.nabu.eai.repository.managers.base.TypeRegistryManager;
import be.nabu.libs.resources.ResourceReadableContainer;
import be.nabu.libs.resources.api.ManageableContainer;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.resources.api.WritableResource;
import be.nabu.libs.types.uml.UMLRegistry;
import be.nabu.libs.validator.api.Validation;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;
import be.nabu.utils.io.api.WritableContainer;
import be.nabu.utils.xml.XMLUtils;

public class UMLModelManager extends TypeRegistryManager<UMLModelArtifact> {

	public UMLModelManager() {
		super(UMLModelArtifact.class);
	}

	@Override
	public List<Validation<?>> save(ResourceEntry entry, UMLModelArtifact artifact) throws IOException {
		Resource resource = entry.getContainer().getChild("uml-model.xml");
		if (artifact.getConfiguration() != null) {
			if (resource == null) {
				resource = ((ManageableContainer<?>) entry.getContainer()).create("uml-model.xml", "application/xml");
			}
			WritableContainer<ByteBuffer> writable = ((WritableResource) resource).getWritable();
			try {
				marshal(artifact.getRepository(), artifact.getConfiguration(), IOUtils.toOutputStream(writable));
			}
			finally {
				writable.close();
			}
		}
		else {
			((ManageableContainer<?>) entry.getContainer()).delete("uml-model.xml");
		}
		return new ArrayList<Validation<?>>();
	}
	
	@Override
	public UMLModelArtifact load(ResourceEntry entry, List<Validation<?>> messages) throws IOException, ParseException {
		try {
			UMLModelArtifact registry = new UMLModelArtifact(entry.getId(), entry.getContainer(), entry.getRepository());

			Resource configurationResource = entry.getContainer().getChild("uml-model.xml");
			if (configurationResource != null) {
				ReadableContainer<ByteBuffer> readable = ((ReadableResource) configurationResource).getReadable();
				try {
					registry.setConfiguration(unmarshal(entry.getRepository(), IOUtils.toInputStream(readable)));
					registry.setAddDatabaseFields(registry.getConfiguration().isAddDatabaseFields());
					registry.setGenerateCollectionNames(registry.getConfiguration().isGenerateCollectionNames());
					registry.setGenerateFlatDocuments(registry.getConfiguration().isGenerateFlatDocuments());
					registry.setCreatedField(registry.getConfiguration().getCreatedField());
					registry.setModifiedField(registry.getConfiguration().getModifiedField());
					registry.setInverseParentChildRelationship(registry.getConfiguration().isInverseParentChildRelationship());
					registry.setImports(registry.getConfiguration().getImports());
					registry.setUuids(!registry.getConfiguration().isUseLongs());
					registry.setUseExtensions(registry.getConfiguration().isUseExtensions());
				}
				finally {
					readable.close();
				}
			}
			
			registry.setResourceResolver(new EntryResourceResolver(entry));
			// always load the base types first
			InputStream input = UMLRegistry.class.getClassLoader().getResourceAsStream("baseTypes.xmi");
			try {
				registry.load(XMLUtils.toDocument(input, true));
			}
			finally {
				input.close();
			}
			List<Document> documents = new ArrayList<Document>();
			// load own documents
			loadDocuments(entry.getContainer(), documents);
			// load all existing xmi files together (because we don't know the order they should be loaded in)
			registry.load(documents.toArray(new Document[documents.size()]));
			
			return registry;
		}
		catch (SAXException e) {
			throw new ParseException(e.getMessage(), 0);
		}
		catch (ParserConfigurationException e) {
			throw new ParseException(e.getMessage(), 0);
		}
	}

	private void loadDocuments(ResourceContainer<?> container, List<Document> documents) throws IOException, SAXException, ParserConfigurationException {
		for (Resource child : container) {
			if (child.getName().endsWith(".xmi") && child instanceof ReadableResource) {
				ReadableContainer<ByteBuffer> readable = new ResourceReadableContainer((ReadableResource) child);
				try {
					documents.add(XMLUtils.toDocument(IOUtils.toInputStream(readable), true));
				}
				finally {
					readable.close();
				}
			}
		}
	}

	public static UMLModelConfiguration unmarshal(Repository repository, InputStream input) {
		try {
			JAXBContext context = JAXBContext.newInstance(UMLModelConfiguration.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setAdapter(new ArtifactXMLAdapter(repository));
			return (UMLModelConfiguration) unmarshaller.unmarshal(input);
		}
		catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void marshal(Repository repository, UMLModelConfiguration configuration, OutputStream output) {
		try {
			JAXBContext context = JAXBContext.newInstance(UMLModelConfiguration.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setAdapter(new ArtifactXMLAdapter(repository));
			marshaller.marshal(configuration, output);
		}
		catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
