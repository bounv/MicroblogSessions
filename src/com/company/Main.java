package com.company;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
//    public static User user;
    public static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
//        ArrayList<Message> messages = new ArrayList<>();

        Spark.init();

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);

                    HashMap m = new HashMap();
                    if(user == null) {
                        return new ModelAndView(m, "index.html");
                    } else {
                        m.put("name", user.name);
                        m.put("messages", user.messages);
                        return new ModelAndView(m, "messages.html");
                    }
                }),
                new MustacheTemplateEngine()
        );

        // "/login"
        // username
        // password
        // Get user from HashMap with username
        // if user is null
        //      create a new user
        //      add new user to the Hashmap
        // if users password matches entered password
        //      set the username in the session
        //redirect
        //return

        Spark.post(
                "/create-user",
                ((request, response) -> {
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("loginPassword");
                    User user = users.get(name);

                    if(user == null) {
                        user = new User(name, password);
                        users.put(name, user);
                    }

                    if(password.equals(user.password)) {
                        Session session = request.session();
                        session.attribute("userName", name);
//                        session.attribute("userPassword", password);
                    }

                    response.redirect("/");
                    return "";


                })
        );

        // /edit-message (similar as below)
        //      message number
        //      new message test
        // Get username from session
        // Get user out of HashMap
        // Get message out of ArrayList
        // -- this --
        // Change text of the message
        // -- or:
        // remove message
        // add new message at the message #

        // tip: check to see if message is valid number with IF statement to not through error
        //      if(message # <= list size

        // redirect
        // return



        Spark.post(

                "/create-message",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);
                    if (user == null) {
                        throw new Exception("User is not logged in");
                    }

                    String text = request.queryParams("userMessage");
                    Message p = new Message (text);
                    user.messages.add(p);


                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(

                "/delete-message",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);
                    if (user == null) {
                        throw new Exception("User is not logged in");
                    }

                    int id = Integer.parseInt(request.queryParams("deleteMessage"));

                    user.messages.remove(id -1);


                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(

                "/edit-message",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);
                    if (user == null) {
                        throw new Exception("User is not logged in");
                    }

                    int id = Integer.parseInt(request.queryParams("messageNumber"));
                    user.messages.remove(id -1);

                    String message = request.queryParams("editMessage");
                    Message p = new Message (message);
                    user.messages.add(id -1, p);

                    response.redirect("/");
                    return "";
                })
        );


        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );
    }
}
