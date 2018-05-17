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

import java.net.InetAddress;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("properties")
public class PropertiesResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject getProperties() {

		JsonObjectBuilder builder = Json.createObjectBuilder();
		try {
			InetAddress myHost = InetAddress.getLocalHost();
			builder.add("version", "1");
			builder.add("hostname", myHost.getHostName());

			SortedMap<String, String> env = new TreeMap<>(System.getenv());
			for (String envName : env.keySet()) {
				builder.add(envName, env.get(envName));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.build();
	}
}
