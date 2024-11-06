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

import be.nabu.eai.repository.api.Repository;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.types.uml.UMLRegistry;

public class UMLModelArtifact extends UMLRegistry {

	private ResourceContainer<?> container;
	private Repository repository;
	private UMLModelConfiguration configuration;

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

	public UMLModelConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(UMLModelConfiguration configuration) {
		this.configuration = configuration;
	}

}
