// tag::comment[]
/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
// end::comment[]
package io.openliberty.guides.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("health")
public class HealthCheckStatus {
	static final long start = System.currentTimeMillis();
	static final long delay = 30000 + (long) (Math.random() * 30000);

	private static final Logger log = Logger.getLogger(HealthCheckStatus.class.getName());

	@GET
	public Response getHealthCheckStatus() {
		if (System.currentTimeMillis() - start > delay) {
			log.info("HealthCheckStatus returned: HTTP Error 500 (INTERNAL_SERVER_ERROR)");
			return Response.serverError().build();
		}

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("status", "ok");
		builder.add("remaining-time-ms", "" + (start + delay - System.currentTimeMillis()));
		String json = builder.build().toString();
		log.log(Level.INFO,"HealthCheckStatus returned: {0}", json);

		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}
}
