package be.nabu.eai.module.types.uml;

import be.nabu.eai.repository.api.Repository;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.types.uml.UMLRegistry;

public class UMLModelArtifact extends UMLRegistry {

	private ResourceContainer<?> container;
	private Repository repository;

	public UMLModelArtifact(String id, ResourceContainer<?> container, Repository repository) {
		super(id);
		this.container = container;
		this.repository = repository;
	}

	public ResourceContainer<?> getContainer() {
		return container;
	}

	public Repository getRepository() {
		return repository;
	}

}
