package be.nabu.eai.module.types.uml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import be.nabu.eai.module.types.xml.EntryResourceResolver;
import be.nabu.eai.repository.api.ResourceEntry;
import be.nabu.eai.repository.managers.base.TypeRegistryManager;
import be.nabu.libs.resources.ResourceReadableContainer;
import be.nabu.libs.resources.api.ManageableContainer;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.Resource;
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
				marshal(artifact.getConfiguration(), IOUtils.toOutputStream(writable));
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
					registry.setConfiguration(unmarshal(IOUtils.toInputStream(readable)));
					registry.setAddDatabaseFields(registry.getConfiguration().isAddDatabaseFields());
					registry.setGenerateCollectionNames(registry.getConfiguration().isGenerateCollectionNames());
					registry.setGenerateFlatDocuments(registry.getConfiguration().isGenerateFlatDocuments());
					registry.setCreatedField(registry.getConfiguration().getCreatedField());
					registry.setModifiedField(registry.getConfiguration().getModifiedField());
					registry.setInverseParentChildRelationship(registry.getConfiguration().isInverseParentChildRelationship());
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
			for (Resource child : entry.getContainer()) {
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

	public static UMLModelConfiguration unmarshal(InputStream input) {
		try {
			JAXBContext context = JAXBContext.newInstance(UMLModelConfiguration.class);
			return (UMLModelConfiguration) context.createUnmarshaller().unmarshal(input);
		}
		catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void marshal(UMLModelConfiguration configuration, OutputStream output) {
		try {
			JAXBContext context = JAXBContext.newInstance(UMLModelConfiguration.class);
			context.createMarshaller().marshal(configuration, output);
		}
		catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
