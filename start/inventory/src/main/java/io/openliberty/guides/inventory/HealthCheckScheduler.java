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

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class HealthCheckScheduler {
    
    @Inject
    private InventoryManager inventoryManager;
    
    @PostConstruct
    public void init() {
        System.out.println("HealthCheckScheduler EJB initialized and will check system health every 30 seconds");
    }
    
    @Schedule(hour = "*", minute = "*", second = "*/30", persistent = false)
    public void performHealthChecks() {
        int updated = inventoryManager.refreshAllSystemsHealth();
        System.out.println("Scheduled health check completed. Updated " + updated + " system(s).");
    }
}
