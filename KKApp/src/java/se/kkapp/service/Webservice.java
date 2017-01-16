/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kkapp.service;

import javax.ejb.EJB;
import javax.json.JsonArray;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import se.kkapp.bean.DBBean;
import se.kkapp.support.User;

/**
 *
 * @author Casper
 */
@Path("")
public class Webservice {

    @EJB
    DBBean DBB;

    @GET
    @Path("/recept")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecept(@Context HttpHeaders httpHeaders) {
        if (!User.authenticate(httpHeaders, 0)) {
            return Response.status(401).build();
        }
        JsonArray data = DBB.getRecept();

        if (data == null) {
            return Response.serverError().build();
        }

        return Response.ok(data).build();
    }

    @DELETE
    @Path("/recept/{id}")
    public Response removeRecept(@PathParam("id") int id, @Context HttpHeaders httpHeaders) {
        if (!User.authenticate(httpHeaders, 3)) {
            return Response.status(401).build();
        }
        if (!DBB.removeRecept(id)) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("/recept")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postRecept(String body, @Context HttpHeaders httpHeaders) {
        if (!User.authenticate(httpHeaders, 1)) {
            return Response.status(401).build();
        }
        if (!DBB.postRecept(body)) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @PUT
    @Path("/recept/{id}")
    public Response putRecept(String body, @PathParam("id") int id, @Context HttpHeaders httpHeaders) {
        if (!User.authenticate(httpHeaders, 1)) {
            return Response.status(401).build();
        }
        if (!DBB.putRecept(body, id)) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/ingrediense/{id}")
    public Response getIngrediens(@PathParam("id") int id, @Context HttpHeaders httpHeaders) {
        if (!User.authenticate(httpHeaders, 0)) {
            return Response.status(401).build();
        }
        JsonArray data = DBB.getIngrediense(id);

        if (data == null) {
            return Response.serverError().build();
        }
        return Response.ok(data).build();
    }

    @POST
    @Path("/ingrediense")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postIngrediens(String body, @Context HttpHeaders httpHeaders) {
        if (!User.authenticate(httpHeaders, 1)) {
            return Response.status(401).build();
        }
        if (!DBB.postIngrediens(body)) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @PUT
    @Path("/ingrediense/{id}")
    public Response putIngrediens(String body, @PathParam("id") int id, @Context HttpHeaders httpHeaders) {
        if (!User.authenticate(httpHeaders, 1)) {
            return Response.status(401).build();
        }
        if (!DBB.putIngrediense(body, id)) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    /*            
    [{
    "name": "Casper",
    "password": "Passwooord",
    }]
    */
    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String body) {
        if (!User.createUser(body)) {
            return Response.status(406).build();
        }
        return Response.status(201).build();
    }

}
