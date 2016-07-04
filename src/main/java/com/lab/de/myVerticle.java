package com.lab.de;

import static io.vertx.ext.sync.Sync.awaitResult;
import static io.vertx.ext.sync.Sync.fiberHandler;


import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.eventbus.Message;

import io.vertx.core.json.JsonObject;

public class myVerticle extends io.vertx.ext.sync.SyncVerticle {
    String ADDRESS1 = "myAddress1";
    String ADDRESS2 = "myAddress2";
   
    @Override
    @Suspendable
    public void start() {
        listenForMessages();
        vertx.setTimer(1000, tid -> {sendInitialMessage(); });
    }

    private void sendInitialMessage() {
        vertx.eventBus().send(ADDRESS1, "myRequest");
    }

    @Suspendable
    private void listenForMessages() {
        vertx.eventBus().consumer(ADDRESS1, fiberHandler((Message<JsonObject> msg) -> {
            System.out.println("Received Message on " + ADDRESS1);
           
            Message<String> reply = null;
            try {
                reply = awaitResult(h -> vertx.eventBus().send(ADDRESS2, "myMessageContent",h),10*1000);
            } catch(Exception e) {
                System.err.println("Did not Receive any reply: " + e.getMessage());
                return;
            }
            System.out.println("received Reply: " + reply.body());
        }));
       
        vertx.eventBus().consumer(ADDRESS2, fiberHandler((Message<String> msg) -> {
            System.out.println("Received Message on " + ADDRESS2);
            msg.reply("myReplyString");
        }));
       
    }

}
