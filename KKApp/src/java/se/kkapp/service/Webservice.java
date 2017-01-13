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
        JsonArray data = DBB.getRecept();

        if (data == null) {
            return Response.serverError().build();
        }

        return Response.ok(data).build();
    }
    
    @DELETE
    @Path("/recept/{id}")
    public Response removeRecept(@PathParam("id") int id){
        if (!DBB.removeRecept(id)){
            return Response.serverError().build();
        }
        return Response.ok().build();
    }
    
    @POST
    @Path("/recept")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postRecept(String body){
        if(!DBB.postRecept(body)){
            return Response.serverError().build();
        }
        return Response.ok().build();
    }
    
    @PUT
    @Path("/recept/{id}")
    public Response putRecept(String body, @PathParam("id") int id){
        if(!DBB.putRecept(body, id)){
            return Response.serverError().build();
        }
        return Response.ok().build();
    }
}
