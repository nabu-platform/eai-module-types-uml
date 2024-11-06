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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.TypeRegistryGUIManager;
import be.nabu.eai.developer.managers.util.SimpleProperty;
import be.nabu.eai.developer.managers.util.SimplePropertyUpdater;
import be.nabu.eai.developer.util.EAIDeveloperUtils;
import be.nabu.eai.repository.api.Entry;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.jfx.control.tree.TreeItem;
import be.nabu.libs.artifacts.api.Artifact;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.resources.api.ManageableContainer;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.resources.api.WritableResource;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.Container;
import be.nabu.utils.io.api.WritableContainer;

// TODO: don't delete files on upload
// instead, show all current XMI files in a listview with delete option
public class UMLModelGUIManager extends TypeRegistryGUIManager<UMLModelArtifact> {

	public UMLModelGUIManager() {
		super(new UMLModelManager(), "UML Model");
	}
	
	@Override
	public String getCategory() {
		return "Types";
	}

	@Override
	protected UMLModelArtifact newInstance(MainController controller, RepositoryEntry entry, Value<?>... values) throws IOException {
		return new UMLModelArtifact(entry.getId(), entry.getContainer(), entry.getRepository());
	}
	
	private AnchorPane tryModel(UMLModelArtifact artifact) {
		try {
			AnchorPane pane = new AnchorPane();
			Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass("be.nabu.eai.module.data.model.DataModelGUIManager");
			for (Method method : loadClass.getMethods()) {
				if (method.getName().equals("draw")) {
					method.invoke(null, artifact, pane);
					return pane;
				}
			}
		}
		catch (Throwable e) {
			// ignore
			System.err.println("Could not load data model view: " + e.getMessage());
		}
		return null;
	}
	
	@Override
	public void display(MainController controller, AnchorPane pane, UMLModelArtifact artifact) throws IOException, ParseException {
		final ResourceContainer<?> container = artifact.getContainer();
		final ListView<String> files = new ListView<String>();
		for (Resource resource : container) {
			if (resource.getName().endsWith(".xmi")) {
				files.getItems().add(resource.getName());
			}
		}
		
		Button deleteFiles = new Button("Delete");
		deleteFiles.disableProperty().bind(files.getSelectionModel().selectedItemProperty().isNull());
		deleteFiles.addEventHandler(ActionEvent.ANY, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String selectedItem = files.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {
					Resource child = container.getChild(selectedItem);
					if (child != null) {
						try {
							((ManageableContainer<?>) container).delete(selectedItem);
							reload(artifact);
						}
						catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
					files.getItems().remove(selectedItem);
				}
			}
		});
		
		Button addFiles = new Button("Add XMI");
		addFiles.addEventHandler(ActionEvent.ANY, new EventHandler<ActionEvent>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void handle(ActionEvent arg0) {
				SimpleProperty<File> simpleProperty = new SimpleProperty<File>("XMI File", File.class, true);
				simpleProperty.setInput(true);
//				simpleProperty.setList(true);
				Set properties = new LinkedHashSet(Arrays.asList(new Property [] {
					simpleProperty
				}));
				final SimplePropertyUpdater updater = new SimplePropertyUpdater(true, properties);
				EAIDeveloperUtils.buildPopup(MainController.getInstance(), updater, "Add XMI files", new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						File file = updater.getValue("XMI File");
						if (file != null) {
							try {
								if (file.isFile() && file.getName().endsWith(".xmi")) {
									Container<ByteBuffer> wrap = IOUtils.wrap(file);
									try {
										Resource resource = container.getChild(file.getName());
										if (resource == null) {
											resource = ((ManageableContainer<?>) container).create(file.getName(), "application/xml");
											files.getItems().add(file.getName());
										}
										WritableContainer<ByteBuffer> writable = ((WritableResource) resource).getWritable();
										try {
											IOUtils.copyBytes(wrap, writable);
										}
										finally {
											writable.close();
										}
									}
									finally {
										wrap.close();
									}
								}
								reload(artifact);
							}
							catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
					}
				}, true);
			}
		});
		HBox box = new HBox();
		box.getStyleClass().add("buttons");
		box.getChildren().addAll(addFiles, deleteFiles);
		
		if (artifact.getConfiguration() == null) {
			artifact.setConfiguration(new UMLModelConfiguration(true));
		}
		SimplePropertyUpdater createUpdater = EAIDeveloperUtils.createUpdater(artifact.getConfiguration(), null);
		AnchorPane properties = new AnchorPane();
		MainController.getInstance().showProperties(createUpdater, properties, true);
		
		VBox vbox = new VBox();
		vbox.getChildren().addAll(box, files, properties);
		
		// check if we have the data modeling module installed
		AnchorPane tryModel = tryModel(artifact);
		if (tryModel != null) {
			TabPane tabs = new TabPane();
			tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
			tabs.setSide(Side.RIGHT);
			
			Tab model = new Tab("Model");
			model.setContent(tryModel);
			
			Tab configuration = new Tab("Configuration");
			configuration.setContent(vbox);
			
			tabs.getTabs().addAll(model, configuration);
			pane.getChildren().add(tabs);
			
			AnchorPane.setLeftAnchor(tabs, 0d);
			AnchorPane.setRightAnchor(tabs, 0d);
			AnchorPane.setTopAnchor(tabs, 0d);
			AnchorPane.setBottomAnchor(tabs, 0d);
		}
		// original behavior without data model
		else {
			pane.getChildren().add(vbox);
			AnchorPane.setLeftAnchor(vbox, 0d);
			AnchorPane.setRightAnchor(vbox, 0d);
			AnchorPane.setTopAnchor(vbox, 0d);
			AnchorPane.setBottomAnchor(vbox, 0d);
		}
		
	}
	
	public static void reload(Artifact artifact) {
		try {
			// reload artifact in repo
			MainController.getInstance().getRepository().reload(artifact.getId());
			// reload remote
			MainController.getInstance().getServer().getRemote().reload(artifact.getId());
			// trigger refresh in tree
			TreeItem<Entry> resolve = MainController.getInstance().getTree().resolve(artifact.getId().replace('.', '/'));
			if (resolve != null) {
				resolve.refresh();
				resolve.getParent().refresh();
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
