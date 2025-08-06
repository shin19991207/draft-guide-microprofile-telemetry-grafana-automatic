// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.openliberty.guides.inventory.client.SystemClient;
import io.openliberty.guides.inventory.model.InventoryList;
import io.openliberty.guides.inventory.model.SystemData;

@ApplicationScoped
public class InventoryManager {

    @Inject
    @ConfigProperty(name = "system.http.port")
    private int SYSTEM_PORT;

    private List<SystemData> systems = Collections.synchronizedList(new ArrayList<>());

    public Properties getProperties(String hostname) {
        try (SystemClient client = new SystemClient()) {
            client.init(hostname, SYSTEM_PORT);
            return client.getProperties();
        }
    }

    public String getHealth(String hostname) {
        try (SystemClient client = new SystemClient()) {
            client.init(hostname, SYSTEM_PORT);
            return client.getHealth();
        }
    }

    // tag::listMethod[]
    public InventoryList list() {
        return new InventoryList(systems);
    }
    // end::listMethod[]

    // tag::addMethod[]
    public void add(String host, Properties systemProps) {
        Properties props = new Properties();
        props.setProperty("os.name", systemProps.getProperty("os.name"));
        props.setProperty("user.name", systemProps.getProperty("user.name"));
        String health = getHealth(host);
        SystemData system = new SystemData(host, props, health);
        if (!systems.contains(system)) {
            systems.add(system);
        }
    }
    // end::addMethod[]

    public int refreshAllSystemsHealth() {
        int updated = 0;
        for (SystemData system : systems) {
            String hostname = system.getHostname();
            String currentHealth = system.getHealth();
            String newHealth = getHealth(hostname);
            if (!newHealth.equals(currentHealth)) {
                system.setHealth(newHealth);
                updated++;
            }
        }
        return updated;
    }

    int clear() {
        int propertiesClearedCount = systems.size();
        systems.clear();
        return propertiesClearedCount;
    }
}
