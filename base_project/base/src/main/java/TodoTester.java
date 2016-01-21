package java;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import cs263w16.Todo;

public class TodoTester {
  public static void main(String[] args) {


    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    WebTarget service = client.target(getBaseURI());

    // create one todo
    Todo todo = new Todo("3", "Blabla");
    Response response = service.path("rest").path("todos").path(todo.getId()).request(MediaType.APPLICATION_XML).put(Entity.entity(todo,MediaType.APPLICATION_XML),Response.class);

    // Return code should be 201 == created resource
    System.out.println(response.getStatus());

    // Get the Todos
    System.out.println(service.path("rest").path("todos").request().accept(MediaType.TEXT_XML).get(String.class));

//    // Get JSON for application
//    System.out.println(service.path("rest").path("todos").request().accept(MediaType.APPLICATION_JSON).get(String.class));

    // Get XML for application
    System.out.println(service.path("rest").path("todos").request().accept(MediaType.APPLICATION_XML).get(String.class));

    //Get Todo with id 1
    System.out.println(service.path("rest").path("todos/1").request().accept(MediaType.APPLICATION_XML).get(String.class));

    //Delete Todo with id 1
    service.path("rest").path("todos/1").request().delete();

    //Get get all Todos id 1 should be deleted
    System.out.println(service.path("rest").path("todos").request().accept(MediaType.APPLICATION_XML).get(String.class));

    //Create a Todo
    Form form =new Form();
    form.param("id", "4");
    form.param("summary","Demonstration of the client lib for forms");
    response = service.path("rest").path("todos").request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED),Response.class);
    System.out.println("Form response " + response.getStatus());

    //Get all the todos, id 4 should have been created
    System.out.println(service.path("rest").path("todos").request().accept(MediaType.APPLICATION_XML).get(String.class));

  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost:8080/com.vogella.jersey.todo").build();
  }
} 
